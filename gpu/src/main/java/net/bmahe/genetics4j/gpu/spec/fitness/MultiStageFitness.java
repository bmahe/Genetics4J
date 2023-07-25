package net.bmahe.genetics4j.gpu.spec.fitness;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import net.bmahe.genetics4j.gpu.spec.fitness.multistage.MultiStageDescriptor;
import net.bmahe.genetics4j.gpu.spec.fitness.multistage.StageDescriptor;

public class MultiStageFitness<T extends Comparable<T>> extends OpenCLFitness<T> {
	public static final Logger logger = LogManager.getLogger(MultiStageFitness.class);

	private final MultiStageDescriptor multiStageDescriptor;
	private final FitnessExtractor<T> fitnessExtractor;

	private final Map<Device, Map<String, CLData>> staticData = new ConcurrentHashMap<>();

	protected void clearStaticData(final Device device) {
		if (MapUtils.isEmpty(staticData) || MapUtils.isEmpty(staticData.get(device))) {
			return;
		}

		final Map<String, CLData> mapData = staticData.get(device);
		for (final CLData clData : mapData.values()) {
			CL.clReleaseMemObject(clData.clMem());
		}

		mapData.clear();
		staticData.remove(device);
	}

	protected void clearData(final Map<Integer, CLData> data) {
		if (MapUtils.isEmpty(data)) {
			return;
		}

		for (final CLData clData : data.values()) {
			CL.clReleaseMemObject(clData.clMem());
		}

		data.clear();
	}

	protected void clearResultData(final Map<Integer, CLData> resultData) {
		if (MapUtils.isEmpty(resultData)) {
			return;
		}

		for (final CLData clData : resultData.values()) {
			CL.clReleaseMemObject(clData.clMem());
		}

		resultData.clear();
	}

	protected void prepareStaticData(final OpenCLExecutionContext openCLExecutionContext,
			final StageDescriptor stageDescriptor) {
		Validate.notNull(openCLExecutionContext);
		Validate.notNull(stageDescriptor);

		final var device = openCLExecutionContext.device();

		logger.trace("[{}] Preparing static data", device.name());

		final var kernels = openCLExecutionContext.kernels();
		final var kernelName = stageDescriptor.kernelName();

		final var kernel = kernels.get(kernelName);

		final var mapStaticDataAsArgument = stageDescriptor.mapStaticDataAsArgument();
		for (final var entry : mapStaticDataAsArgument.entrySet()) {
			final var argumentName = entry.getKey();
			final var argumentIndex = entry.getValue();

			final var staticDataMap = staticData.get(device);

			if (staticDataMap.containsKey(argumentName) == false) {
				throw new IllegalArgumentException("Unknown static argument " + argumentName);
			}

			final CLData clStaticData = staticDataMap.get(argumentName);

			logger.trace("[{}] Index {} - Loading static data with name {}", device.name(), argumentIndex, argumentName);

			CL.clSetKernelArg(kernel, argumentIndex, Sizeof.cl_mem, Pointer.to(clStaticData.clMem()));
		}
	}

	private void allocateLocalMemory(OpenCLExecutionContext openCLExecutionContext, StageDescriptor stageDescriptor,
			long generation, List<Genotype> genotypes, final KernelExecutionContext kernelExecutionContext) {
		Validate.notNull(openCLExecutionContext);
		Validate.notNull(stageDescriptor);
		Validate.notNull(kernelExecutionContext);

		final var device = openCLExecutionContext.device();

		logger.trace("[{}] Allocating local memory", device.name());

		final var kernels = openCLExecutionContext.kernels();
		final var kernelName = stageDescriptor.kernelName();

		final var kernel = kernels.get(kernelName);

		final var localMemoryAllocators = stageDescriptor.localMemoryAllocators();
		if (MapUtils.isNotEmpty(localMemoryAllocators)) {
			for (final var entry : localMemoryAllocators.entrySet()) {
				final int argumentIdx = entry.getKey();
				final var localMemoryAllocator = entry.getValue();

				final var size = localMemoryAllocator
						.load(openCLExecutionContext, kernelExecutionContext, generation, genotypes);
				logger.trace("[{}] Index {} - Setting local data with size of {}", device.name(), argumentIdx, size);

				CL.clSetKernelArg(kernel, argumentIdx, size, null);
			}
		}
	}

	protected void loadData(final OpenCLExecutionContext openCLExecutionContext, final StageDescriptor stageDescriptor,
			final Map<Integer, CLData> data, final long generation, final List<Genotype> genotypes) {
		Validate.notNull(openCLExecutionContext);
		Validate.notNull(stageDescriptor);
		Validate.notNull(data);

		final var device = openCLExecutionContext.device();

		logger.trace("[{}] Loading data", device.name());

		final var kernels = openCLExecutionContext.kernels();
		final var kernelName = stageDescriptor.kernelName();

		final var kernel = kernels.get(kernelName);

		final var dataLoaders = stageDescriptor.dataLoaders();
		if (MapUtils.isNotEmpty(dataLoaders)) {
			for (final var entry : dataLoaders.entrySet()) {
				final int argumentIdx = entry.getKey();
				final var dataLoader = entry.getValue();

				final var clDdata = dataLoader.load(openCLExecutionContext, generation, genotypes);

				if (data.put(argumentIdx, clDdata) != null) {
					throw new IllegalArgumentException("Multiple data configured for index " + argumentIdx);
				}
				logger.trace("[{}] Index {} - Loading data of size {}", device.name(), argumentIdx, clDdata.size());

				CL.clSetKernelArg(kernel, argumentIdx, Sizeof.cl_mem, Pointer.to(clDdata.clMem()));
			}
		}
	}

	@Override
	public void beforeAllEvaluations(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService) {
		super.beforeAllEvaluations(openCLExecutionContext, executorService);

		final var device = openCLExecutionContext.device();

		logger.trace("[{}] Loading static data", device.name());
		clearStaticData(device);

		final var staticDataLoaders = multiStageDescriptor.staticDataLoaders();
		for (final var entry : staticDataLoaders.entrySet()) {
			final String argumentName = entry.getKey();
			final var dataSupplier = entry.getValue();

			if (logger.isTraceEnabled()) {
				final var deviceName = openCLExecutionContext.device()
						.name();
				logger.trace("[{}] Loading static data for entry name {}", deviceName, argumentName);
			}
			final CLData clData = dataSupplier.load(openCLExecutionContext);

			final var mapData = staticData.computeIfAbsent(device, k -> new HashMap<>());
			if (mapData.put(argumentName, clData) != null) {
				throw new IllegalArgumentException("Multiple data configured with name " + argumentName);
			}
		}
	}

	public MultiStageFitness(final MultiStageDescriptor _multiStageDescriptor,
			final FitnessExtractor<T> _fitnessExtractor) {
		Validate.notNull(_multiStageDescriptor);
		Validate.notNull(_fitnessExtractor);

		this.multiStageDescriptor = _multiStageDescriptor;
		this.fitnessExtractor = _fitnessExtractor;
	}

	@Override
	public CompletableFuture<List<T>> compute(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService, final long generation, final List<Genotype> genotypes) {
		Validate.notNull(openCLExecutionContext);

		return CompletableFuture.supplyAsync(() -> {

			List<T> finalResults = null;

			final var device = openCLExecutionContext.device();

			final Map<Integer, CLData> data = new ConcurrentHashMap<>();
			Map<Integer, CLData> resultData = new ConcurrentHashMap<>();

			final var stageDescriptors = multiStageDescriptor.stageDescriptors();
			for (int i = 0; i < stageDescriptors.size(); i++) {
				final StageDescriptor stageDescriptor = stageDescriptors.get(i);
				final var kernels = openCLExecutionContext.kernels();
				final var kernelName = stageDescriptor.kernelName();

				final var kernel = kernels.get(kernelName);

				logger.debug("[{}] Executing {}-th stage for kernel {}", device.name(), i, kernelName);

				/**
				 * Compute the Kernel Execution Context
				 */
				final var kernelExecutionContextComputer = stageDescriptor.kernelExecutionContextComputer();
				final var kernelExecutionContext = kernelExecutionContextComputer
						.compute(openCLExecutionContext, kernelName, generation, genotypes);

				/**
				 * Map previous results to new arguments
				 */
				final Map<Integer, CLData> oldResultData = new HashMap<>(resultData);
				resultData = new ConcurrentHashMap<>();
				final Map<Integer, Integer> reusePreviousResultSizeAsArguments = stageDescriptor
						.reusePreviousResultSizeAsArguments();
				final Map<Integer, Integer> reusePreviousResultAsArguments = stageDescriptor
						.reusePreviousResultAsArguments();
				final Set<CLData> reusedArguments = new HashSet<>();
				if (MapUtils.isNotEmpty(reusePreviousResultAsArguments)
						|| MapUtils.isNotEmpty(reusePreviousResultSizeAsArguments)) {

					if (MapUtils.isNotEmpty(reusePreviousResultAsArguments)) {
						for (final Entry<Integer, Integer> entry : reusePreviousResultAsArguments.entrySet()) {
							final var oldKeyArgument = entry.getKey();
							final var newKeyArgument = entry.getValue();

							final var previousResultData = oldResultData.get(oldKeyArgument);
							if (previousResultData == null) {
								logger.error(
										"[{}] Could not find previous argument with index {}. Known previous arguments: {}",
										device.name(),
										oldKeyArgument,
										oldResultData);

								throw new IllegalArgumentException(
										"Could not find previous argument with index " + oldKeyArgument);
							}

							logger.trace("[{}] Index {} - Reuse previous result that had index {}",
									device.name(),
									newKeyArgument,
									oldKeyArgument);
							CL.clSetKernelArg(kernel, newKeyArgument, Sizeof.cl_mem, Pointer.to(previousResultData.clMem()));
							reusedArguments.add(previousResultData);
						}
					}

					if (MapUtils.isNotEmpty(reusePreviousResultSizeAsArguments)) {
						for (final Entry<Integer, Integer> entry : reusePreviousResultSizeAsArguments.entrySet()) {
							final var oldKeyArgument = entry.getKey();
							final var newKeyArgument = entry.getValue();

							final var previousResultData = oldResultData.get(oldKeyArgument);
							if (previousResultData == null) {
								logger.error(
										"[{}] Could not find previous argument with index {}. Known previous arguments: {}",
										device.name(),
										oldKeyArgument,
										oldResultData);

								throw new IllegalArgumentException(
										"Could not find previous argument with index " + oldKeyArgument);
							}

							if (logger.isTraceEnabled()) {
								logger.trace("[{}] Index {} - Setting previous result size of {} of previous argument index {}",
										device.name(),
										newKeyArgument,
										previousResultData.size(),
										oldKeyArgument);
							}

							CL.clSetKernelArg(kernel,
									newKeyArgument,
									Sizeof.cl_int,
									Pointer.to(new int[] { previousResultData.size() }));
						}
					}

					// Clean up unused results
					final var previousResultsToKeep = reusePreviousResultAsArguments.keySet();
					for (Entry<Integer, CLData> entry2 : oldResultData.entrySet()) {
						if (previousResultsToKeep.contains(entry2.getKey()) == false) {
							CL.clReleaseMemObject(entry2.getValue()
									.clMem());
						}
					}
				}

				prepareStaticData(openCLExecutionContext, stageDescriptor);
				loadData(openCLExecutionContext, stageDescriptor, data, generation, genotypes);
				allocateLocalMemory(openCLExecutionContext, stageDescriptor, generation, genotypes, kernelExecutionContext);

				/**
				 * Allocate memory for results
				 */
				final var resultAllocators = stageDescriptor.resultAllocators();
				if (MapUtils.isNotEmpty(resultAllocators)) {
					logger.trace("[{}] Result allocators: {}", device.name(), resultAllocators);

					for (final var entry : resultAllocators.entrySet()) {
						final int argumentIdx = entry.getKey();
						final var resultAllocator = entry.getValue();

						final var clDdata = resultAllocator
								.load(openCLExecutionContext, kernelExecutionContext, generation, genotypes);

						if (resultData.put(argumentIdx, clDdata) != null) {
							throw new IllegalArgumentException(
									"Multiple result allocators configured for index " + argumentIdx);
						}
						if (logger.isTraceEnabled()) {
							logger.trace("[{}] Index {} - Allocate result data memory of type {} and size {}",
									device.name(),
									argumentIdx,
									clDdata.clType(),
									clDdata.size());
						}

						CL.clSetKernelArg(kernel, argumentIdx, Sizeof.cl_mem, Pointer.to(clDdata.clMem()));
					}
				} else {
					logger.trace("[{}] No result allocator found", device.name());
				}

				final var clCommandQueue = openCLExecutionContext.clCommandQueue();
				final var globalWorkDimensions = kernelExecutionContext.globalWorkDimensions();
				final var globalWorkSize = kernelExecutionContext.globalWorkSize();
				final long[] workGroupSize = kernelExecutionContext.workGroupSize()
						.orElse(null);

				logger.trace(
						"[{}] Starting computation on kernel {} for {} genotypes and global work size {} and local work size {}",
						device.name(),
						kernelName,
						genotypes.size(),
						globalWorkSize,
						workGroupSize);
				try {
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

					// CL.clFinish(openCLExecutionContext.clCommandQueue());

					final long endTime = System.nanoTime();
					final long duration = endTime - startTime;
					if (logger.isDebugEnabled()) {
						final var deviceName = openCLExecutionContext.device()
								.name();
						logger.debug("[{}] -  Stage {} - Took {} microsec for {} genotypes",
								deviceName,
								i,
								duration / 1000.,
								genotypes.size());
					}
				} catch (Exception e) {
					logger.error("[{}] Failure to compute", device.name(), e);
					throw e;
				}

				if (i == stageDescriptors.size() - 1) {
					finalResults = fitnessExtractor.compute(openCLExecutionContext,
							kernelExecutionContext,
							executorService,
							generation,
							genotypes,
							new ResultExtractor(Map.of(device, resultData)));

					clearResultData(resultData);
				}

				for (final CLData clData : reusedArguments) {
					CL.clReleaseMemObject(clData.clMem());
				}
				clearData(data);
			}

			if (finalResults == null) {
				throw new IllegalStateException("final results cannot be null");
			}
			return finalResults;
		}, executorService);
	}

	@Override
	public void afterEvaluation(OpenCLExecutionContext openCLExecutionContext, ExecutorService executorService,
			long generation, List<Genotype> genotypes) {
		super.afterEvaluation(openCLExecutionContext, executorService, generation, genotypes);

		final var device = openCLExecutionContext.device();
		logger.trace("[{}] Releasing data", device.name());
	}

	@Override
	public void afterAllEvaluations(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService) {
		super.afterAllEvaluations(openCLExecutionContext, executorService);

		final var device = openCLExecutionContext.device();
		logger.trace("[{}] Releasing static data", device.name());
		clearStaticData(device);
	}

	public static <U extends Comparable<U>> MultiStageFitness<U> of(final MultiStageDescriptor multiStageDescriptor,
			final FitnessExtractor<U> fitnessExtractor) {
		Validate.notNull(multiStageDescriptor);
		Validate.notNull(fitnessExtractor);

		return new MultiStageFitness<>(multiStageDescriptor, fitnessExtractor);
	}
}