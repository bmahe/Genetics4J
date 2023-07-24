package net.bmahe.genetics4j.gpu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jocl.CL;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;
import org.jocl.cl_queue_properties;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.evaluation.FitnessEvaluator;
import net.bmahe.genetics4j.gpu.opencl.DeviceReader;
import net.bmahe.genetics4j.gpu.opencl.DeviceUtils;
import net.bmahe.genetics4j.gpu.opencl.KernelInfoReader;
import net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext;
import net.bmahe.genetics4j.gpu.opencl.PlatformReader;
import net.bmahe.genetics4j.gpu.opencl.PlatformUtils;
import net.bmahe.genetics4j.gpu.opencl.model.Device;
import net.bmahe.genetics4j.gpu.opencl.model.KernelInfo;
import net.bmahe.genetics4j.gpu.opencl.model.Platform;
import net.bmahe.genetics4j.gpu.spec.GPUEAConfiguration;
import net.bmahe.genetics4j.gpu.spec.GPUEAExecutionContext;
import net.bmahe.genetics4j.gpu.spec.Program;

public class GPUFitnessEvaluator<T extends Comparable<T>> implements FitnessEvaluator<T> {
	public static final Logger logger = LogManager.getLogger(GPUFitnessEvaluator.class);

	private final GPUEAExecutionContext<T> gpuEAExecutionContext;
	private final GPUEAConfiguration<T> gpuEAConfiguration;
	private final ExecutorService executorService;

	private List<Pair<Platform, Device>> selectedPlatformToDevice;

	final List<cl_context> clContexts = new ArrayList<>();
	final List<cl_command_queue> clCommandQueues = new ArrayList<>();
	final List<cl_program> clPrograms = new ArrayList<>();
	final List<Map<String, cl_kernel>> clKernels = new ArrayList<>();
	final List<OpenCLExecutionContext> clExecutionContexts = new ArrayList<>();

	public GPUFitnessEvaluator(final GPUEAExecutionContext<T> _gpuEAExecutionContext,
			final GPUEAConfiguration<T> _gpuEAConfiguration, final ExecutorService _executorService) {
		Validate.notNull(_gpuEAExecutionContext);
		Validate.notNull(_gpuEAConfiguration);
		Validate.notNull(_executorService);

		this.gpuEAExecutionContext = _gpuEAExecutionContext;
		this.gpuEAConfiguration = _gpuEAConfiguration;
		this.executorService = _executorService;

		CL.setExceptionsEnabled(true);
	}

	private String loadResource(final String filename) {
		Validate.notBlank(filename);

		try {
			return IOUtils.resourceToString(filename, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load resource " + filename, e);
		}
	}

	private List<String> grabProgramSources() {
		final Program programSpec = gpuEAConfiguration.program();

		logger.info("Load program source: {}", programSpec);

		final List<String> sources = new ArrayList<>();

		sources.addAll(programSpec.content());

		programSpec.resources()
				.stream()
				.map(resource -> loadResource(resource))
				.forEach(program -> {
					sources.add(program);
				});

		return sources;
	}

	@Override
	public void preEvaluation() {
		logger.trace("Init...");
		FitnessEvaluator.super.preEvaluation();

		final var platformReader = new PlatformReader();
		final var deviceReader = new DeviceReader();
		final var kernelInfoReader = new KernelInfoReader();

		final int numPlatforms = PlatformUtils.numPlatforms();
		logger.info("Found {} platforms", numPlatforms);

		final List<cl_platform_id> platformIds = PlatformUtils.platformIds(numPlatforms);

		logger.info("Selecting platform and devices");
		final var platformFilters = gpuEAExecutionContext.platformFilters();
		final var deviceFilters = gpuEAExecutionContext.deviceFilters();

		selectedPlatformToDevice = platformIds.stream()
				.map(platformReader::read)
				.filter(platformFilters)
				.flatMap(platform -> {
					final var platformId = platform.platformId();
					final int numDevices = DeviceUtils.numDevices(platformId);
					logger.trace("\tPlatform {}: {} devices", platform.name(), numDevices);

					final var deviceIds = DeviceUtils.getDeviceIds(platformId, numDevices);
					return deviceIds.stream()
							.map(deviceId -> Pair.of(platform, deviceId));
				})
				.map(platformToDeviceId -> {
					final var platform = platformToDeviceId.getLeft();
					final var platformId = platform.platformId();
					final var deviceID = platformToDeviceId.getRight();

					return Pair.of(platform, deviceReader.read(platformId, deviceID));
				})
				.filter(platformToDevice -> deviceFilters.test(platformToDevice.getRight()))
				.toList();

		if (logger.isTraceEnabled()) {
			logger.trace("============================");
			logger.trace("Selected devices:");
			selectedPlatformToDevice.forEach(pd -> {
				logger.trace("{}", pd.getLeft());
				logger.trace("\t{}", pd.getRight());
			});
			logger.trace("============================");
		}

		Validate.isTrue(selectedPlatformToDevice.size() > 0);

		final List<String> programs = grabProgramSources();
		final String[] programsArr = programs.toArray(new String[programs.size()]);

		for (final var platformAndDevice : selectedPlatformToDevice) {
			final var platform = platformAndDevice.getLeft();
			final var device = platformAndDevice.getRight();

			logger.info("Processing platform [{}] / device [{}]", platform.name(), device.name());

			logger.info("\tCreating context");
			cl_context_properties contextProperties = new cl_context_properties();
			contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform.platformId());

			final cl_context context = CL
					.clCreateContext(contextProperties, 1, new cl_device_id[] { device.deviceId() }, null, null, null);

			logger.info("\tCreating command queue");
			final cl_queue_properties queueProperties = new cl_queue_properties();
			queueProperties.addProperty(CL.CL_QUEUE_PROPERTIES,
					CL.CL_QUEUE_PROFILING_ENABLE | CL.CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE);
			final cl_command_queue commandQueue = CL
					.clCreateCommandQueueWithProperties(context, device.deviceId(), queueProperties, null);

			logger.info("\tCreate program");
			final cl_program program = CL.clCreateProgramWithSource(context, programsArr.length, programsArr, null, null);

			final var programSpec = gpuEAConfiguration.program();
			final var buildOptions = programSpec.buildOptions()
					.orElse(null);
			logger.info("\tBuilding program with options: {}", buildOptions);
			CL.clBuildProgram(program, 0, null, buildOptions, null, null);

			final Set<String> kernelNames = gpuEAConfiguration.program()
					.kernelNames();

			final Map<String, cl_kernel> kernels = new HashMap<>();
			final Map<String, KernelInfo> kernelInfos = new HashMap<>();
			for (final String kernelName : kernelNames) {

				logger.info("\tCreate kernel {}", kernelName);
				final cl_kernel kernel = CL.clCreateKernel(program, kernelName, null);
				Validate.notNull(kernel);

				kernels.put(kernelName, kernel);

				final var kernelInfo = kernelInfoReader.read(device.deviceId(), kernel, kernelName);
				logger.trace("\t{}", kernelInfo);
				kernelInfos.put(kernelName, kernelInfo);
			}

			clContexts.add(context);
			clCommandQueues.add(commandQueue);
			clKernels.add(kernels);
			clPrograms.add(program);

			final var openCLExecutionContext = OpenCLExecutionContext.builder()
					.platform(platform)
					.device(device)
					.clContext(context)
					.clCommandQueue(commandQueue)
					.kernels(kernels)
					.kernelInfos(kernelInfos)
					.clProgram(program)
					.build();

			clExecutionContexts.add(openCLExecutionContext);
		}

		final var fitness = gpuEAConfiguration.fitness();
		fitness.beforeAllEvaluations();
		for (final OpenCLExecutionContext clExecutionContext : clExecutionContexts) {
			fitness.beforeAllEvaluations(clExecutionContext, executorService);
		}
	}

	@Override
	public List<T> evaluate(final long generation, final List<Genotype> genotypes) {

		final var fitness = gpuEAConfiguration.fitness();

		/**
		 * TODO make it configurable from execution context
		 */
		final int partitionSize = (int) (Math.ceil((double) genotypes.size() / clExecutionContexts.size()));
		final var subGenotypes = ListUtils.partition(genotypes, partitionSize);
		logger.debug("Genotype decomposed in {} partition(s)", subGenotypes.size());
		if (logger.isTraceEnabled()) {
			for (int i = 0; i < subGenotypes.size(); i++) {
				final List<Genotype> subGenotype = subGenotypes.get(i);
				logger.trace("\tPartition {} with {} elements", i, subGenotype.size());
			}
		}

		final List<CompletableFuture<List<T>>> subResultsCF = new ArrayList<>();
		for (int i = 0; i < subGenotypes.size(); i++) {
			final var openCLExecutionContext = clExecutionContexts.get(i % clExecutionContexts.size());
			final var subGenotype = subGenotypes.get(i);

			fitness.beforeEvaluation(generation, subGenotype);
			fitness.beforeEvaluation(openCLExecutionContext, executorService, generation, subGenotype);

			final var resultsCF = fitness.compute(openCLExecutionContext, executorService, generation, subGenotype)
					.thenApply((results) -> {

						fitness.afterEvaluation(openCLExecutionContext, executorService, generation, subGenotype);
						fitness.afterEvaluation(generation, subGenotype);

						return results;
					});

			subResultsCF.add(resultsCF);
		}

		final List<T> resultsEvaluation = new ArrayList<>(genotypes.size());
		for (final CompletableFuture<List<T>> subResultCF : subResultsCF) {
			final var fitnessResults = subResultCF.join();
			resultsEvaluation.addAll(fitnessResults);
		}
		return resultsEvaluation;
	}

	@Override
	public void postEvaluation() {

		final var fitness = gpuEAConfiguration.fitness();

		for (final OpenCLExecutionContext clExecutionContext : clExecutionContexts) {
			fitness.afterAllEvaluations(clExecutionContext, executorService);
		}
		fitness.afterAllEvaluations();

		logger.debug("Releasing kernels");

		for (final Map<String, cl_kernel> kernels : clKernels) {
			for (final cl_kernel clKernel : kernels.values()) {
				CL.clReleaseKernel(clKernel);
			}
		}
		clKernels.clear();

		logger.debug("Releasing programs");
		for (final cl_program clProgram : clPrograms) {
			CL.clReleaseProgram(clProgram);
		}
		clPrograms.clear();

		logger.debug("Releasing command queues");
		for (final cl_command_queue clCommandQueue : clCommandQueues) {
			CL.clReleaseCommandQueue(clCommandQueue);
		}
		clCommandQueues.clear();

		logger.debug("Releasing contexts");
		for (final cl_context clContext : clContexts) {
			CL.clReleaseContext(clContext);
		}
		clContexts.clear();

		clExecutionContexts.clear();
		selectedPlatformToDevice = null;

		FitnessEvaluator.super.postEvaluation();
	}
}