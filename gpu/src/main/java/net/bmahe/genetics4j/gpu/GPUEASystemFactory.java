package net.bmahe.genetics4j.gpu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.gpu.spec.GPUEAConfiguration;
import net.bmahe.genetics4j.gpu.spec.GPUEAExecutionContext;

/**
 * Factory class for creating GPU-accelerated evolutionary algorithm systems using OpenCL.
 * 
 * <p>GPUEASystemFactory provides convenient factory methods for creating {@link EASystem} instances
 * that leverage GPU acceleration through OpenCL for fitness evaluation. This factory extends the
 * capabilities of the core EA framework to support high-performance computing on graphics processors
 * and other OpenCL-compatible devices.
 * 
 * <p>The factory handles the integration between GPU-specific configurations and the core EA framework:
 * <ul>
 * <li><strong>GPU Configuration</strong>: Uses {@link GPUEAConfiguration} with OpenCL program specifications</li>
 * <li><strong>Device Selection</strong>: Leverages {@link GPUEAExecutionContext} for platform and device filtering</li>
 * <li><strong>GPU Evaluator</strong>: Creates specialized {@link GPUFitnessEvaluator} for OpenCL fitness computation</li>
 * <li><strong>Resource Management</strong>: Coordinates executor services with OpenCL resource lifecycle</li>
 * </ul>
 * 
 * <p>GPU acceleration benefits:
 * <ul>
 * <li><strong>Massive parallelism</strong>: Evaluate hundreds or thousands of individuals simultaneously</li>
 * <li><strong>Memory bandwidth</strong>: High-throughput data processing for population-based algorithms</li>
 * <li><strong>Specialized hardware</strong>: Leverage dedicated compute units optimized for parallel operations</li>
 * <li><strong>Energy efficiency</strong>: Often better performance-per-watt compared to CPU-only execution</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Define OpenCL kernel for fitness evaluation
 * Program fitnessProgram = Program.ofResource("/kernels/fitness.cl")
 *     .withBuildOption("-DPROBLEM_SIZE=256");
 * 
 * // Configure GPU-specific EA settings
 * GPUEAConfiguration<Double> gpuConfig = GPUEAConfigurationBuilder.<Double>builder()
 *     .chromosomeSpecs(chromosomeSpec)
 *     .parentSelectionPolicy(Tournament.of(3))
 *     .combinationPolicy(SinglePointCrossover.build())
 *     .mutationPolicies(List.of(RandomMutation.of(0.1)))
 *     .replacementStrategy(Elitism.builder().offspringRatio(0.8).build())
 *     .program(fitnessProgram)
 *     .fitness(myGPUFitness)
 *     .build();
 * 
 * // Configure execution context with device preferences
 * GPUEAExecutionContext<Double> gpuContext = GPUEAExecutionContextBuilder.<Double>builder()
 *     .populationSize(1000)
 *     .termination(Generations.of(100))
 *     .platformFilter(platform -> platform.profile() == PlatformProfile.FULL_PROFILE)
 *     .deviceFilter(device -> device.type() == DeviceType.GPU)
 *     .build();
 * 
 * // Create GPU-accelerated EA system
 * EASystem<Double> gpuSystem = GPUEASystemFactory.from(gpuConfig, gpuContext);
 * 
 * // Run evolution on GPU
 * EvolutionResult<Double> result = gpuSystem.evolve();
 * }</pre>
 * 
 * <p>OpenCL integration considerations:
 * <ul>
 * <li><strong>Device compatibility</strong>: Ensure target devices support required OpenCL features</li>
 * <li><strong>Memory management</strong>: GPU memory is typically limited compared to system RAM</li>
 * <li><strong>Kernel optimization</strong>: GPU performance depends heavily on kernel implementation</li>
 * <li><strong>Transfer overhead</strong>: Consider data transfer costs between CPU and GPU memory</li>
 * </ul>
 * 
 * <p>Performance optimization tips:
 * <ul>
 * <li><strong>Large populations</strong>: GPU acceleration benefits increase with population size</li>
 * <li><strong>Complex fitness functions</strong>: More computation per individual improves GPU utilization</li>
 * <li><strong>Minimize transfers</strong>: Keep data on GPU between generations when possible</li>
 * <li><strong>Coalesced memory access</strong>: Design kernels for optimal memory access patterns</li>
 * </ul>
 * 
 * @see GPUEAConfiguration
 * @see GPUEAExecutionContext
 * @see GPUFitnessEvaluator
 * @see net.bmahe.genetics4j.core.EASystemFactory
 * @see net.bmahe.genetics4j.gpu.opencl.model.Program
 */
public class GPUEASystemFactory {

	private GPUEASystemFactory() {
	}

	/**
	 * Creates a GPU-accelerated EA system with explicit thread pool management.
	 * 
	 * <p>This method provides full control over thread pool management while enabling GPU acceleration
	 * for fitness evaluation. The provided executor service is used for coordinating between CPU
	 * and GPU operations, managing asynchronous OpenCL operations, and handling concurrent access
	 * to OpenCL resources.
	 * 
	 * <p>The factory method performs the following operations:
	 * <ol>
	 * <li>Creates a specialized {@link GPUFitnessEvaluator} configured for OpenCL execution</li>
	 * <li>Integrates the GPU evaluator with the core EA framework</li>
	 * <li>Ensures proper resource management and cleanup for OpenCL contexts</li>
	 * </ol>
	 * 
	 * <p>Use this method when:
	 * <ul>
	 * <li>You need explicit control over thread pool configuration and lifecycle</li>
	 * <li>Integration with existing thread management systems is required</li>
	 * <li>Custom executor services are needed for performance tuning</li>
	 * <li>Resource-constrained environments require careful thread pool sizing</li>
	 * </ul>
	 * 
	 * @param <T> the type of fitness values, must be comparable for selection operations
	 * @param gpuEAConfiguration the GPU-specific EA configuration with OpenCL program and fitness function
	 * @param gpuEAExecutionContext the GPU execution context with device selection and population parameters
	 * @param executorService the thread pool for managing CPU-GPU coordination (caller responsible for shutdown)
	 * @return a fully configured {@link EASystem} with GPU acceleration capabilities
	 * @throws IllegalArgumentException if any parameter is null
	 * @throws RuntimeException if OpenCL initialization fails or no compatible devices are found
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final GPUEAConfiguration<T> gpuEAConfiguration,
			final GPUEAExecutionContext<T> gpuEAExecutionContext, final ExecutorService executorService) {

		final var gpuFitnessEvaluator = new GPUFitnessEvaluator<T>(gpuEAExecutionContext,
				gpuEAConfiguration,
				executorService);
		return EASystemFactory.from(gpuEAConfiguration, gpuEAExecutionContext, executorService, gpuFitnessEvaluator);
	}

	/**
	 * Creates a GPU-accelerated EA system using the common thread pool.
	 * 
	 * <p>This convenience method provides GPU acceleration without requiring explicit thread pool
	 * management. It automatically uses {@link ForkJoinPool#commonPool()} for CPU-GPU coordination,
	 * making it ideal for applications where thread pool management is not critical.
	 * 
	 * <p>This method is recommended for:
	 * <ul>
	 * <li>Rapid prototyping and experimentation with GPU acceleration</li>
	 * <li>Applications where default thread pool behavior is sufficient</li>
	 * <li>Educational purposes and demonstration code</li>
	 * <li>Simple GPU-accelerated applications without complex threading requirements</li>
	 * </ul>
	 * 
	 * <p>The common thread pool provides automatic parallelization and reasonable default
	 * behavior for most GPU acceleration scenarios. However, for production systems with
	 * specific performance requirements, consider using {@link #from(GPUEAConfiguration, GPUEAExecutionContext, ExecutorService)}
	 * with a custom thread pool.
	 * 
	 * @param <T> the type of fitness values, must be comparable for selection operations
	 * @param gpuEAConfiguration the GPU-specific EA configuration with OpenCL program and fitness function
	 * @param gpuEAExecutionContext the GPU execution context with device selection and population parameters
	 * @return a fully configured {@link EASystem} with GPU acceleration using the common thread pool
	 * @throws IllegalArgumentException if any parameter is null
	 * @throws RuntimeException if OpenCL initialization fails or no compatible devices are found
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final GPUEAConfiguration<T> gpuEAConfiguration,
			final GPUEAExecutionContext<T> gpuEAExecutionContext) {
		final ExecutorService executorService = ForkJoinPool.commonPool();

		return from(gpuEAConfiguration, gpuEAExecutionContext, executorService);
	}
}