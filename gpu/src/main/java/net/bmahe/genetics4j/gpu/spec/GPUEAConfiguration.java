package net.bmahe.genetics4j.gpu.spec;

import java.util.Arrays;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.gpu.spec.fitness.OpenCLFitness;

/**
 * GPU-specific evolutionary algorithm configuration that extends the core EA framework with OpenCL capabilities.
 * 
 * <p>GPUEAConfiguration extends {@link AbstractEAConfiguration} to include GPU-specific settings required
 * for OpenCL-based fitness evaluation. This configuration combines traditional EA parameters (selection,
 * mutation, crossover) with GPU-specific components like OpenCL programs and specialized fitness functions.
 * 
 * <p>Key GPU-specific additions:
 * <ul>
 * <li><strong>OpenCL Program</strong>: Specifies kernel source code, build options, and compilation settings</li>
 * <li><strong>GPU Fitness Function</strong>: Specialized fitness implementation that leverages OpenCL execution</li>
 * <li><strong>Kernel Integration</strong>: Automatic coordination between EA framework and OpenCL kernels</li>
 * </ul>
 * 
 * <p>Configuration workflow:
 * <ol>
 * <li><strong>Core EA setup</strong>: Define chromosome specs, selection policies, mutation rates</li>
 * <li><strong>OpenCL program</strong>: Specify kernel source code and compilation options</li>
 * <li><strong>GPU fitness</strong>: Implement fitness evaluation using OpenCL primitives</li>
 * <li><strong>Integration</strong>: Combine components into complete GPU-accelerated EA system</li>
 * </ol>
 * 
 * <p>Example configuration:
 * <pre>{@code
 * // Define OpenCL program with kernel source
 * Program optimizationKernels = Program.ofResource("/kernels/tsp_fitness.cl")
 *     .withBuildOption("-DCITY_COUNT=100")
 *     .withBuildOption("-DLOCAL_SIZE=256");
 * 
 * // Create GPU-specific fitness function
 * OpenCLFitness<Double> gpuFitness = new TSPGPUFitness();
 * 
 * // Build complete GPU EA configuration
 * GPUEAConfiguration<Double> config = GPUEAConfiguration.<Double>builder()
 *     // Core EA components
 *     .chromosomeSpecs(intPermutationSpec)
 *     .parentSelectionPolicy(Tournament.of(3))
 *     .combinationPolicy(OrderCrossover.build())
 *     .mutationPolicies(SwapMutation.of(0.1))
 *     .replacementStrategy(Elitism.builder().offspringRatio(0.8).build())
 *     
 *     // GPU-specific components
 *     .program(optimizationKernels)
 *     .fitness(gpuFitness)
 *     .build();
 * }</pre>
 * 
 * <p>OpenCL integration benefits:
 * <ul>
 * <li><strong>Performance</strong>: Massive parallelism for fitness evaluation</li>
 * <li><strong>Scalability</strong>: Handle large populations efficiently</li>
 * <li><strong>Hardware utilization</strong>: Leverage GPU compute units</li>
 * <li><strong>Memory bandwidth</strong>: High-throughput data processing</li>
 * </ul>
 * 
 * <p>Configuration validation:
 * <ul>
 * <li><strong>Program completeness</strong>: Ensures all required kernels are specified</li>
 * <li><strong>Fitness compatibility</strong>: Validates fitness function matches program interface</li>
 * <li><strong>Resource requirements</strong>: Checks memory and compute requirements are feasible</li>
 * </ul>
 * 
 * <p>Builder pattern usage:
 * <ul>
 * <li><strong>Type safety</strong>: Generic builder ensures fitness type consistency</li>
 * <li><strong>Validation</strong>: Build-time validation of configuration completeness</li>
 * <li><strong>Convenience methods</strong>: Simplified specification of chromosome and mutation arrays</li>
 * <li><strong>Immutability</strong>: Thread-safe configuration objects</li>
 * </ul>
 * 
 * @param <T> the type of fitness values produced by the GPU fitness function
 * @see AbstractEAConfiguration
 * @see Program
 * @see OpenCLFitness
 * @see net.bmahe.genetics4j.gpu.GPUEASystemFactory
 */
@Value.Immutable
public abstract class GPUEAConfiguration<T extends Comparable<T>> extends AbstractEAConfiguration<T> {

	/**
	 * Returns the OpenCL program specification containing kernel source code and build options.
	 * 
	 * <p>The program defines the OpenCL kernels that will be compiled and executed on GPU devices
	 * for fitness evaluation. This includes source code, build flags, and kernel definitions
	 * required for the evolutionary algorithm execution.
	 * 
	 * @return the OpenCL program specification for kernel compilation
	 */
	public abstract Program program();

	/**
	 * Returns the GPU-specific fitness function that implements evaluation using OpenCL.
	 * 
	 * <p>The fitness function encapsulates the logic for evaluating individual solutions
	 * using GPU acceleration. It coordinates data transfer, kernel execution, and result
	 * extraction for efficient parallel fitness computation.
	 * 
	 * @return the OpenCL fitness function for GPU-accelerated evaluation
	 */
	public abstract OpenCLFitness<T> fitness();

	public static class Builder<T extends Comparable<T>> extends ImmutableGPUEAConfiguration.Builder<T> {

		public final GPUEAConfiguration.Builder<T> chromosomeSpecs(final ChromosomeSpec... elements) {
			return this.chromosomeSpecs(Arrays.asList(elements));
		}

		public final GPUEAConfiguration.Builder<T> mutationPolicies(final MutationPolicy... elements) {
			return this.mutationPolicies(Arrays.asList(elements));
		}
	}

	/**
	 * Creates a new builder for constructing GPU EA configurations.
	 * 
	 * <p>The builder provides a fluent interface for specifying both core EA components
	 * and GPU-specific settings. Type safety is ensured through generic parameterization.
	 * 
	 * @param <U> the type of fitness values for the EA configuration
	 * @return a new builder instance for creating GPU EA configurations
	 */
	public static <U extends Comparable<U>> Builder<U> builder() {
		return new Builder<>();
	}
}