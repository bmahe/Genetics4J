package net.bmahe.genetics4j.gpu.spec.fitness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext;
import net.bmahe.genetics4j.gpu.opencl.model.Device;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.CLData;
import net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext.KernelExecutionContext;

/**
 * GPU-accelerated fitness evaluator that executes a single OpenCL kernel for fitness computation.
 * 
 * <p>SingleKernelFitness provides a comprehensive framework for implementing fitness evaluation using
 * a single OpenCL kernel. It manages the complete lifecycle of GPU computation including data loading,
 * kernel execution, and result extraction, making it suitable for most GPU-accelerated evolutionary
 * algorithm scenarios.
 * 
 * <p>Key features:
 * <ul>
 * <li><strong>Single kernel execution</strong>: Executes one OpenCL kernel per fitness evaluation</li>
 * <li><strong>Data management</strong>: Handles static data, dynamic data, and result allocation</li>
 * <li><strong>Memory lifecycle</strong>: Automatic cleanup of OpenCL memory objects</li>
 * <li><strong>Multi-device support</strong>: Supports concurrent execution across multiple devices</li>
 * <li><strong>Local memory</strong>: Configurable local memory allocation for kernel optimization</li>
 * </ul>
 * 
 * <p>Data flow architecture:
 * <ul>
 * <li><strong>Static data</strong>: Algorithm parameters loaded once before all evaluations</li>
 * <li><strong>Dynamic data</strong>: Population data loaded before each generation</li>
 * <li><strong>Local memory</strong>: Work group local memory allocated based on kernel requirements</li>
 * <li><strong>Result data</strong>: Output buffers allocated for fitness results and intermediate data</li>
 * </ul>
 * 
 * <p>Typical usage pattern:
 * <pre>{@code
 * // Define kernel and data configuration
 * SingleKernelFitnessDescriptor descriptor = SingleKernelFitnessDescriptor.builder()
 *     .kernelName("fitness_evaluation")
 *     .addDataLoader(0, populationDataLoader)
 *     .addStaticDataLoader(1, parametersDataLoader)
 *     .addResultAllocator(2, fitnessResultAllocator)
 *     .kernelExecutionContextComputer(executionContextComputer)
 *     .build();
 * 
 * // Define fitness extraction from GPU results
 * FitnessExtractor<Double> extractor = (context, kernelCtx, executor, generation, genotypes, results) -> {
 *     float[] fitnessValues = results.extractFloatArray(context, 2);
 *     return Arrays.stream(fitnessValues)
 *         .mapToDouble(f -> (double) f)
 *         .boxed()
 *         .collect(Collectors.toList());
 * };
 * 
 * // Create single kernel fitness evaluator
 * SingleKernelFitness<Double> fitness = SingleKernelFitness.of(descriptor, extractor);
 * }</pre>
 * 
 * <p>Kernel execution workflow:
 * <ol>
 * <li><strong>Initialization</strong>: Load static data once before all evaluations</li>
 * <li><strong>Data preparation</strong>: Load generation-specific data and allocate result buffers</li>
 * <li><strong>Kernel setup</strong>: Configure kernel arguments with data references</li>
 * <li><strong>Execution</strong>: Launch kernel with optimized work group configuration</li>
 * <li><strong>Result extraction</strong>: Extract fitness values from GPU memory</li>
 * <li><strong>Cleanup</strong>: Release generation-specific memory resources</li>
 * </ol>
 * 
 * <p>Memory management strategy:
 * <ul>
 * <li><strong>Static data persistence</strong>: Static data remains allocated across generations</li>
 * <li><strong>Dynamic allocation</strong>: Generation data is allocated and released per evaluation</li>
 * <li><strong>Result buffer reuse</strong>: Result buffers can be reused with proper sizing</li>
 * <li><strong>Automatic cleanup</strong>: Memory is automatically released in lifecycle methods</li>
 * </ul>
 * 
 * <p>Performance optimization features:
 * <ul>
 * <li><strong>Asynchronous execution</strong>: Kernel execution returns CompletableFuture for pipeline processing</li>
 * <li><strong>Work group optimization</strong>: Configurable work group sizes for optimal device utilization</li>
 * <li><strong>Memory coalescing</strong>: Support for optimized memory access patterns</li>
 * <li><strong>Local memory utilization</strong>: Efficient use of device local memory for performance</li>
 * </ul>
 * 
 * @param <T> the fitness value type, must be Comparable for optimization algorithms
 * @see OpenCLFitness
 * @see SingleKernelFitnessDescriptor
 * @see FitnessExtractor
 * @see net.bmahe.genetics4j.gpu.spec.fitness.cldata.DataLoader
 */
public class SingleKernelFitness<T extends Comparable<T>> extends OpenCLFitness<T> {
	public static final Logger logger = LogManager.getLogger(SingleKernelFitness.class);

	private final SingleKernelFitnessDescriptor singleKernelFitnessDescriptor;
	private final FitnessExtractor<T> fitnessExtractor;

	private final Map<Device, Map<Integer, CLData>> staticData = new ConcurrentHashMap<>();
	private final Map<Device, Map<Integer, CLData>> data = new ConcurrentHashMap<>();
	private final Map<Device, Map<Integer, CLData>> resultData = new ConcurrentHashMap<>();

	private final Map<Device, KernelExecutionContext> kernelExecutionContexts = new ConcurrentHashMap<>();

	protected void clearStaticData(final Device device) {
		if (MapUtils.isEmpty(staticData) || MapUtils.isEmpty(staticData.get(device))) {
			return;
		}

		final Map<Integer, CLData> mapData = staticData.get(device);
		for (final CLData clData : mapData.values()) {
			CL.clReleaseMemObject(clData.clMem());
		}

		mapData.clear();
		staticData.remove(device);
	}

	protected void clearData(final Device device) {
		if (MapUtils.isEmpty(data) || MapUtils.isEmpty(data.get(device))) {
			return;
		}

		final Map<Integer, CLData> mapData = data.get(device);
		for (final CLData clData : mapData.values()) {
			CL.clReleaseMemObject(clData.clMem());
		}

		mapData.clear();
		data.remove(device);
	}

	protected void clearResultData(final Device device) {
		if (MapUtils.isEmpty(resultData) || MapUtils.isEmpty(resultData.get(device))) {
			return;
		}

		final Map<Integer, CLData> mapData = resultData.get(device);
		for (final CLData clData : mapData.values()) {
			CL.clReleaseMemObject(clData.clMem());
		}

		mapData.clear();
		resultData.remove(device);
	}

	/**
	 * Constructs a SingleKernelFitness with the specified kernel descriptor and fitness extractor.
	 * 
	 * @param _singleKernelFitnessDescriptor configuration for kernel execution and data management
	 * @param _fitnessExtractor function to extract fitness values from GPU computation results
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public SingleKernelFitness(final SingleKernelFitnessDescriptor _singleKernelFitnessDescriptor,
			final FitnessExtractor<T> _fitnessExtractor) {
		Validate.notNull(_singleKernelFitnessDescriptor);
		Validate.notNull(_fitnessExtractor);

		this.singleKernelFitnessDescriptor = _singleKernelFitnessDescriptor;
		this.fitnessExtractor = _fitnessExtractor;
	}

	@Override
	public void beforeAllEvaluations(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService) {
		super.beforeAllEvaluations(openCLExecutionContext, executorService);

		final var device = openCLExecutionContext.device();
		clearStaticData(device);

		final var staticDataLoaders = singleKernelFitnessDescriptor.staticDataLoaders();
		for (final var entry : staticDataLoaders.entrySet()) {
			final int argumentIdx = entry.getKey();
			final var dataSupplier = entry.getValue();

			if (logger.isTraceEnabled()) {
				final var deviceName = openCLExecutionContext.device()
						.name();
				logger.trace("[{}] Loading static data for index {}", deviceName, argumentIdx);
			}
			final CLData clData = dataSupplier.load(openCLExecutionContext);

			final var mapData = staticData.computeIfAbsent(device, k -> new HashMap<>());
			if (mapData.put(argumentIdx, clData) != null) {
				throw new IllegalArgumentException("Multiple data configured for index " + argumentIdx);
			}
		}
	}

	@Override
	public void beforeEvaluation(OpenCLExecutionContext openCLExecutionContext, ExecutorService executorService,
			long generation, final List<Genotype> genotypes) {
		super.beforeEvaluation(openCLExecutionContext, executorService, generation, genotypes);

		final var device = openCLExecutionContext.device();
		final var kernels = openCLExecutionContext.kernels();

		final var kernelName = singleKernelFitnessDescriptor.kernelName();
		final var kernel = kernels.get(kernelName);

		if (kernelExecutionContexts.containsKey(device)) {
			throw new IllegalStateException("Found existing kernelExecutionContext");
		}
		final var kernelExecutionContextComputer = singleKernelFitnessDescriptor.kernelExecutionContextComputer();
		final var kernelExecutionContext = kernelExecutionContextComputer
				.compute(openCLExecutionContext, kernelName, generation, genotypes);
		kernelExecutionContexts.put(device, kernelExecutionContext);

		final var mapData = staticData.get(device);
		if (MapUtils.isNotEmpty(mapData)) {
			for (final var entry : mapData.entrySet()) {
				final int argumentIdx = entry.getKey();
				final var clStaticData = entry.getValue();

				logger.trace("[{}] Loading static data for index {}", device.name(), argumentIdx);

				CL.clSetKernelArg(kernel, argumentIdx, Sizeof.cl_mem, Pointer.to(clStaticData.clMem()));
			}
		}

		final var dataLoaders = singleKernelFitnessDescriptor.dataLoaders();
		if (MapUtils.isNotEmpty(dataLoaders)) {
			for (final var entry : dataLoaders.entrySet()) {
				final int argumentIdx = entry.getKey();
				final var dataLoader = entry.getValue();

				final var clDdata = dataLoader.load(openCLExecutionContext, generation, genotypes);

				final var dataMapping = data.computeIfAbsent(device, k -> new HashMap<>());
				if (dataMapping.put(argumentIdx, clDdata) != null) {
					throw new IllegalArgumentException("Multiple data configured for index " + argumentIdx);
				}
				logger.trace("[{}] Loading data for index {}", device.name(), argumentIdx);

				CL.clSetKernelArg(kernel, argumentIdx, Sizeof.cl_mem, Pointer.to(clDdata.clMem()));
			}
		}

		final var localMemoryAllocators = singleKernelFitnessDescriptor.localMemoryAllocators();
		if (MapUtils.isNotEmpty(localMemoryAllocators)) {
			for (final var entry : localMemoryAllocators.entrySet()) {
				final int argumentIdx = entry.getKey();
				final var localMemoryAllocator = entry.getValue();

				final var size = localMemoryAllocator
						.load(openCLExecutionContext, kernelExecutionContext, generation, genotypes);
				logger.trace("[{}] Setting local data for index {} with size of {}", device.name(), argumentIdx, size);

				CL.clSetKernelArg(kernel, argumentIdx, size, null);
			}
		}

		final var resultAllocators = singleKernelFitnessDescriptor.resultAllocators();
		if (MapUtils.isNotEmpty(resultAllocators)) {
			for (final var entry : resultAllocators.entrySet()) {
				final int argumentIdx = entry.getKey();
				final var resultAllocator = entry.getValue();

				final var clDdata = resultAllocator
						.load(openCLExecutionContext, kernelExecutionContext, generation, genotypes);

				final var dataMapping = resultData.computeIfAbsent(device, k -> new HashMap<>());
				if (dataMapping.put(argumentIdx, clDdata) != null) {
					throw new IllegalArgumentException("Multiple result allocators configured for index " + argumentIdx);
				}
				logger.trace("[{}] Preparing result data memory for index {}", device.name(), argumentIdx);

				CL.clSetKernelArg(kernel, argumentIdx, Sizeof.cl_mem, Pointer.to(clDdata.clMem()));
			}
		}

	}

	@Override
	public CompletableFuture<List<T>> compute(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService, final long generation, List<Genotype> genotypes) {

		return CompletableFuture.supplyAsync(() -> {
			final var clCommandQueue = openCLExecutionContext.clCommandQueue();
			final var kernels = openCLExecutionContext.kernels();

			final var kernelName = singleKernelFitnessDescriptor.kernelName();
			final var kernel = kernels.get(kernelName);
			if (kernel == null) {
				throw new IllegalStateException("Could not find kernel [" + kernelName + "]");
			}

			final var device = openCLExecutionContext.device();
			final var kernelExecutionContext = kernelExecutionContexts.get(device);

			final var globalWorkDimensions = kernelExecutionContext.globalWorkDimensions();
			final var globalWorkSize = kernelExecutionContext.globalWorkSize();
			final long[] workGroupSize = kernelExecutionContext.workGroupSize()
					.orElse(null);

			logger.trace(
					"Starting computation on kernel {} for {} genotypes and global work size {} and local work size {}",
					kernelName,
					genotypes.size(),
					globalWorkSize,
					workGroupSize);
			final long startTime = System.nanoTime();
			CL.clEnqueueNDRangeKernel(clCommandQueue,
					kernel,
					globalWorkDimensions,
					null,
					globalWorkSize,
					workGroupSize,
					0,
					null,
					null);

			final long endTime = System.nanoTime();
			final long duration = endTime - startTime;
			if (logger.isDebugEnabled()) {
				final var deviceName = openCLExecutionContext.device()
						.name();
				logger.debug("{} - Took {} microsec for {} genotypes", deviceName, duration / 1000., genotypes.size());
			}
			return kernelExecutionContext;
		}, executorService)
				.thenApply(kernelExecutionContext -> {

					final var resultExtractor = new ResultExtractor(resultData);
					return fitnessExtractor.compute(openCLExecutionContext,
							kernelExecutionContext,
							executorService,
							generation,
							genotypes,
							resultExtractor);
				});
	}

	@Override
	public void afterEvaluation(OpenCLExecutionContext openCLExecutionContext, ExecutorService executorService,
			long generation, List<Genotype> genotypes) {
		super.afterEvaluation(openCLExecutionContext, executorService, generation, genotypes);

		final var device = openCLExecutionContext.device();
		logger.trace("[{}] Releasing data", device.name());
		clearData(device);
		clearResultData(device);
		kernelExecutionContexts.remove(device);
	}

	@Override
	public void afterAllEvaluations(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService) {
		super.afterAllEvaluations(openCLExecutionContext, executorService);

		final var device = openCLExecutionContext.device();
		logger.trace("[{}] Releasing static data", device.name());
		clearStaticData(device);
	}

	/**
	 * Creates a new SingleKernelFitness instance with the specified configuration.
	 * 
	 * @param <U> the fitness value type
	 * @param singleKernelFitnessDescriptor configuration for kernel execution and data management
	 * @param fitnessExtractor function to extract fitness values from GPU computation results
	 * @return a new SingleKernelFitness instance
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public static <U extends Comparable<U>> SingleKernelFitness<U> of(
			final SingleKernelFitnessDescriptor singleKernelFitnessDescriptor,
			final FitnessExtractor<U> fitnessExtractor) {
		Validate.notNull(singleKernelFitnessDescriptor);
		Validate.notNull(fitnessExtractor);

		return new SingleKernelFitness<>(singleKernelFitnessDescriptor, fitnessExtractor);
	}
}