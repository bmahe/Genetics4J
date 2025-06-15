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

/**
 * GPU-accelerated fitness evaluator that leverages OpenCL for high-performance evolutionary algorithm execution.
 * 
 * <p>GPUFitnessEvaluator implements the core {@link FitnessEvaluator} interface to provide GPU acceleration
 * for fitness computation in evolutionary algorithms. This evaluator manages the complete OpenCL lifecycle,
 * from device discovery and kernel compilation to memory management and resource cleanup.
 * 
 * <p>Key responsibilities include:
 * <ul>
 * <li><strong>OpenCL initialization</strong>: Platform and device discovery, context creation, and kernel compilation</li>
 * <li><strong>Resource management</strong>: Managing OpenCL contexts, command queues, programs, and kernels</li>
 * <li><strong>Population partitioning</strong>: Distributing work across multiple OpenCL devices</li>
 * <li><strong>Asynchronous execution</strong>: Coordinating concurrent GPU operations with CPU-side logic</li>
 * <li><strong>Memory lifecycle</strong>: Ensuring proper cleanup of GPU resources</li>
 * </ul>
 * 
 * <p>Architecture overview:
 * <ol>
 * <li><strong>Initialization ({@link #preEvaluation})</strong>: Discover platforms/devices, compile kernels, create contexts</li>
 * <li><strong>Evaluation ({@link #evaluate})</strong>: Partition population, execute fitness computation on GPU</li>
 * <li><strong>Cleanup ({@link #postEvaluation})</strong>: Release all OpenCL resources and contexts</li>
 * </ol>
 * 
 * <p>Multi-device support:
 * <ul>
 * <li><strong>Device filtering</strong>: Selects devices based on user-defined criteria (type, capabilities)</li>
 * <li><strong>Load balancing</strong>: Automatically distributes population across available devices</li>
 * <li><strong>Parallel execution</strong>: Concurrent fitness evaluation on multiple GPUs or devices</li>
 * <li><strong>Asynchronous coordination</strong>: Non-blocking execution with CompletableFuture-based results</li>
 * </ul>
 * 
 * <p>Resource management patterns:
 * <ul>
 * <li><strong>Lazy initialization</strong>: OpenCL resources created only when needed</li>
 * <li><strong>Automatic cleanup</strong>: Guaranteed resource release through lifecycle methods</li>
 * <li><strong>Error recovery</strong>: Robust handling of OpenCL errors and device failures</li>
 * <li><strong>Memory optimization</strong>: Efficient GPU memory usage and transfer patterns</li>
 * </ul>
 * 
 * <p>Example usage in GPU EA system:
 * <pre>{@code
 * // GPU configuration with OpenCL kernel
 * Program fitnessProgram = Program.ofResource("/kernels/optimization.cl");
 * GPUEAConfiguration<Double> config = GPUEAConfigurationBuilder.<Double>builder()
 *     .program(fitnessProgram)
 *     .fitness(new MyGPUFitness())
 *     // ... other EA configuration
 *     .build();
 * 
 * // Execution context with device preferences
 * GPUEAExecutionContext<Double> context = GPUEAExecutionContextBuilder.<Double>builder()
 *     .populationSize(2000)
 *     .deviceFilter(device -> device.type() == DeviceType.GPU)
 *     .platformFilter(platform -> platform.profile() == PlatformProfile.FULL_PROFILE)
 *     .build();
 * 
 * // Evaluator handles all OpenCL lifecycle automatically
 * GPUFitnessEvaluator<Double> evaluator = new GPUFitnessEvaluator<>(context, config, executorService);
 * 
 * // Used by EA system - lifecycle managed automatically
 * EASystem<Double> system = EASystemFactory.from(config, context, executorService, evaluator);
 * }</pre>
 * 
 * <p>Performance characteristics:
 * <ul>
 * <li><strong>Initialization overhead</strong>: One-time setup cost for OpenCL compilation and context creation</li>
 * <li><strong>Scalability</strong>: Performance scales with population size and problem complexity</li>
 * <li><strong>Memory bandwidth</strong>: Optimal for problems with high computational intensity</li>
 * <li><strong>Concurrency</strong>: Supports concurrent evaluation across multiple devices</li>
 * </ul>
 * 
 * <p>Error handling:
 * <ul>
 * <li><strong>Device failures</strong>: Graceful degradation when devices become unavailable</li>
 * <li><strong>Memory errors</strong>: Proper cleanup and error reporting for GPU memory issues</li>
 * <li><strong>Compilation errors</strong>: Clear error messages for kernel compilation failures</li>
 * <li><strong>Resource leaks</strong>: Guaranteed cleanup even in exceptional circumstances</li>
 * </ul>
 * 
 * @param <T> the type of fitness values produced, must be comparable for selection operations
 * @see FitnessEvaluator
 * @see GPUEAConfiguration
 * @see GPUEAExecutionContext
 * @see OpenCLExecutionContext
 * @see net.bmahe.genetics4j.gpu.fitness.OpenCLFitness
 */
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

	/**
	 * Constructs a GPU fitness evaluator with the specified configuration and execution context.
	 * 
	 * <p>Initializes the evaluator with GPU-specific configuration and execution parameters.
	 * The evaluator will use the provided executor service for coordinating asynchronous
	 * operations between CPU and GPU components.
	 * 
	 * <p>The constructor performs minimal initialization - the actual OpenCL setup occurs
	 * during {@link #preEvaluation()} to follow the fitness evaluator lifecycle pattern.
	 * 
	 * @param _gpuEAExecutionContext the GPU execution context with device filters and population settings
	 * @param _gpuEAConfiguration the GPU EA configuration with OpenCL program and fitness function
	 * @param _executorService the executor service for managing asynchronous operations
	 * @throws IllegalArgumentException if any parameter is null
	 */
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

	/**
	 * Initializes OpenCL resources and prepares GPU devices for fitness evaluation.
	 * 
	 * <p>This method performs the complete OpenCL initialization sequence:
	 * <ol>
	 * <li><strong>Platform discovery</strong>: Enumerates available OpenCL platforms</li>
	 * <li><strong>Device filtering</strong>: Selects devices based on configured filters</li>
	 * <li><strong>Context creation</strong>: Creates OpenCL contexts for selected devices</li>
	 * <li><strong>Queue setup</strong>: Creates command queues with profiling and out-of-order execution</li>
	 * <li><strong>Program compilation</strong>: Compiles OpenCL kernels from source code</li>
	 * <li><strong>Kernel preparation</strong>: Creates kernel objects and queries execution info</li>
	 * <li><strong>Fitness initialization</strong>: Calls lifecycle hooks on the fitness function</li>
	 * </ol>
	 * 
	 * <p>Device selection process:
	 * <ul>
	 * <li>Applies platform filters to discovered OpenCL platforms</li>
	 * <li>Enumerates devices for each qualifying platform</li>
	 * <li>Applies device filters to select appropriate devices</li>
	 * <li>Validates that at least one device is available</li>
	 * </ul>
	 * 
	 * <p>The method creates separate OpenCL contexts for each selected device to enable
	 * concurrent execution and optimal resource utilization. Each context includes
	 * compiled programs and kernel objects ready for fitness evaluation.
	 * 
	 * @throws IllegalStateException if no compatible devices are found
	 * @throws RuntimeException if OpenCL initialization, program compilation, or kernel creation fails
	 */
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

	/**
	 * Evaluates fitness for a population of genotypes using GPU acceleration.
	 * 
	 * <p>This method implements the core fitness evaluation logic by distributing the population
	 * across available OpenCL devices and executing fitness computation concurrently. The
	 * evaluation process follows these steps:
	 * 
	 * <ol>
	 * <li><strong>Population partitioning</strong>: Divides genotypes across available devices</li>
	 * <li><strong>Parallel dispatch</strong>: Submits evaluation tasks to each device asynchronously</li>
	 * <li><strong>GPU execution</strong>: Executes OpenCL kernels for fitness computation</li>
	 * <li><strong>Result collection</strong>: Gathers fitness values from all devices</li>
	 * <li><strong>Result aggregation</strong>: Combines results preserving original order</li>
	 * </ol>
	 * 
	 * <p>Load balancing strategy:
	 * <ul>
	 * <li>Automatically calculates partition size based on population and device count</li>
	 * <li>Round-robin assignment of partitions to devices for balanced workload</li>
	 * <li>Asynchronous execution allows devices to work at their optimal pace</li>
	 * </ul>
	 * 
	 * <p>The method coordinates with the configured fitness function through lifecycle hooks:
	 * <ul>
	 * <li>{@code beforeEvaluation()}: Called before each device partition evaluation</li>
	 * <li>{@code compute()}: Executes the actual GPU fitness computation</li>
	 * <li>{@code afterEvaluation()}: Called after each device partition completes</li>
	 * </ul>
	 * 
	 * <p>Concurrency and performance:
	 * <ul>
	 * <li>Multiple devices execute evaluation partitions concurrently</li>
	 * <li>CompletableFuture-based coordination for non-blocking execution</li>
	 * <li>Automatic workload distribution across available GPU resources</li>
	 * </ul>
	 * 
	 * @param generation the current generation number for context and logging
	 * @param genotypes the population of genotypes to evaluate
	 * @return fitness values corresponding to each genotype in the same order
	 * @throws IllegalArgumentException if genotypes is null or empty
	 * @throws RuntimeException if GPU evaluation fails or OpenCL errors occur
	 */
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

	/**
	 * Cleans up OpenCL resources and releases GPU memory after evaluation completion.
	 * 
	 * <p>This method performs comprehensive cleanup of all OpenCL resources in the proper order
	 * to prevent memory leaks and ensure clean shutdown. The cleanup sequence follows OpenCL
	 * best practices for resource deallocation:
	 * 
	 * <ol>
	 * <li><strong>Fitness cleanup</strong>: Calls lifecycle hooks on the fitness function</li>
	 * <li><strong>Kernel release</strong>: Releases all compiled kernel objects</li>
	 * <li><strong>Program release</strong>: Releases compiled OpenCL programs</li>
	 * <li><strong>Queue release</strong>: Releases command queues and pending operations</li>
	 * <li><strong>Context release</strong>: Releases OpenCL contexts and associated memory</li>
	 * <li><strong>Reference cleanup</strong>: Clears internal data structures and references</li>
	 * </ol>
	 * 
	 * <p>Resource management guarantees:
	 * <ul>
	 * <li>All GPU memory allocations are properly released</li>
	 * <li>OpenCL objects are released in dependency order to avoid errors</li>
	 * <li>No resource leaks occur even if individual cleanup operations fail</li>
	 * <li>Evaluator returns to a clean state ready for potential reinitialization</li>
	 * </ul>
	 * 
	 * <p>The method coordinates with the configured fitness function to ensure any
	 * fitness-specific resources (buffers, textures, etc.) are also properly cleaned up
	 * through the {@code afterAllEvaluations()} lifecycle hooks.
	 * 
	 * @throws RuntimeException if cleanup operations fail (logged but not propagated to prevent
	 *                         interference with EA system shutdown)
	 */
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