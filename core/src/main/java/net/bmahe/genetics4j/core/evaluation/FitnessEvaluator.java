package net.bmahe.genetics4j.core.evaluation;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;

/**
 * Facade interface for abstracting different fitness evaluation strategies in evolutionary algorithms.
 * 
 * <p>FitnessEvaluator provides a unified interface for computing fitness values regardless of the
 * underlying evaluation mechanism. This abstraction allows the evolutionary algorithm to work with
 * various evaluation strategies including synchronous, asynchronous, parallel, and distributed
 * evaluation approaches.
 * 
 * <p>The evaluator supports different execution models:
 * <ul>
 * <li><strong>Synchronous evaluation</strong>: Sequential evaluation of individuals</li>
 * <li><strong>Parallel evaluation</strong>: Concurrent evaluation using thread pools</li>
 * <li><strong>Asynchronous evaluation</strong>: Non-blocking evaluation with future-based results</li>
 * <li><strong>Distributed evaluation</strong>: Evaluation across multiple machines or processes</li>
 * <li><strong>Cached evaluation</strong>: Memoized results for previously evaluated genotypes</li>
 * </ul>
 * 
 * <p>Key responsibilities include:
 * <ul>
 * <li><strong>Fitness computation</strong>: Converting genotypes into comparable fitness values</li>
 * <li><strong>Resource management</strong>: Handling computational resources and external dependencies</li>
 * <li><strong>Performance optimization</strong>: Efficient evaluation strategies for large populations</li>
 * <li><strong>Error handling</strong>: Managing evaluation failures and providing fallback mechanisms</li>
 * </ul>
 * 
 * <p>Lifecycle methods provide hooks for:
 * <ul>
 * <li><strong>Setup</strong>: Initialize resources before evaluation begins</li>
 * <li><strong>Cleanup</strong>: Release resources after evaluation completes</li>
 * <li><strong>Batch processing</strong>: Optimize evaluation of entire populations</li>
 * </ul>
 * 
 * <p>Common implementation patterns:
 * <ul>
 * <li><strong>Wrapper pattern</strong>: Adapting {@link net.bmahe.genetics4j.core.Fitness} functions</li>
 * <li><strong>Decorator pattern</strong>: Adding caching, logging, or monitoring capabilities</li>
 * <li><strong>Strategy pattern</strong>: Switching between different evaluation approaches</li>
 * <li><strong>Template method</strong>: Providing common evaluation infrastructure</li>
 * </ul>
 * 
 * <p>Example implementations:
 * <pre>{@code
 * // Simple synchronous evaluator
 * FitnessEvaluator<Double> syncEvaluator = (generation, genotypes) -> {
 *     return genotypes.stream()
 *         .map(genotype -> computeFitness(genotype))
 *         .collect(toList());
 * };
 * 
 * // Parallel evaluator with thread pool
 * FitnessEvaluator<Double> parallelEvaluator = (generation, genotypes) -> {
 *     return genotypes.parallelStream()
 *         .map(genotype -> computeFitness(genotype))
 *         .collect(toList());
 * };
 * 
 * // Cached evaluator for expensive fitness functions
 * FitnessEvaluator<Double> cachedEvaluator = new CachedFitnessEvaluator<>(
 *     baseFitnessFunction,
 *     maxCacheSize: 10000
 * );
 * }</pre>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Evaluation cost</strong>: Balance accuracy with computational efficiency</li>
 * <li><strong>Parallelization</strong>: Utilize multiple cores for independent evaluations</li>
 * <li><strong>Memory usage</strong>: Manage memory for large populations and complex fitness functions</li>
 * <li><strong>External resources</strong>: Handle databases, web services, or file system access efficiently</li>
 * </ul>
 * 
 * @param <T> the type of fitness values produced, must be comparable for selection operations
 * @see net.bmahe.genetics4j.core.Fitness
 * @see FitnessEvaluatorSync
 * @see FitnessEvaluatorBulkAsync
 * @see net.bmahe.genetics4j.core.EASystem
 */
public interface FitnessEvaluator<T extends Comparable<T>> {

	/**
	 * Called before fitness evaluation begins for a generation.
	 * 
	 * <p>This lifecycle method allows implementations to perform setup operations
	 * such as initializing resources, establishing connections, or preparing
	 * computational environments before the evaluation process starts.
	 * 
	 * <p>Common setup activities include:
	 * <ul>
	 * <li>Starting thread pools or worker processes</li>
	 * <li>Establishing database or network connections</li>
	 * <li>Loading configuration or reference data</li>
	 * <li>Initializing caches or temporary storage</li>
	 * </ul>
	 * 
	 * <p>The default implementation does nothing, making this method optional
	 * for implementations that don't require setup.
	 * 
	 * @throws RuntimeException if setup fails and evaluation cannot proceed
	 */
	default void preEvaluation() {
	}

	/**
	 * Called after fitness evaluation completes for a generation.
	 * 
	 * <p>This lifecycle method allows implementations to perform cleanup operations
	 * such as releasing resources, closing connections, or persisting results
	 * after the evaluation process completes.
	 * 
	 * <p>Common cleanup activities include:
	 * <ul>
	 * <li>Shutting down thread pools or worker processes</li>
	 * <li>Closing database or network connections</li>
	 * <li>Flushing caches or saving state</li>
	 * <li>Logging performance metrics or statistics</li>
	 * </ul>
	 * 
	 * <p>The default implementation does nothing, making this method optional
	 * for implementations that don't require cleanup.
	 * 
	 * @throws RuntimeException if cleanup fails (should not prevent evolution from continuing)
	 */
	default void postEvaluation() {
	}

	/**
	 * Evaluates the fitness of all genotypes in the given population.
	 * 
	 * <p>This is the core method that computes fitness values for an entire population
	 * of genotypes. The implementation strategy (synchronous, parallel, asynchronous)
	 * is determined by the specific evaluator implementation.
	 * 
	 * <p>Requirements:
	 * <ul>
	 * <li>Return fitness values in the same order as input genotypes</li>
	 * <li>Return exactly one fitness value per input genotype</li>
	 * <li>Ensure fitness values are comparable for selection operations</li>
	 * <li>Handle empty populations gracefully (return empty list)</li>
	 * </ul>
	 * 
	 * <p>The generation parameter can be used for:
	 * <ul>
	 * <li>Adaptive fitness functions that change over time</li>
	 * <li>Logging and monitoring evaluation progress</li>
	 * <li>Implementing generation-specific evaluation strategies</li>
	 * <li>Debugging and performance analysis</li>
	 * </ul>
	 * 
	 * @param generation the current generation number (0-based)
	 * @param genotypes the population of genotypes to evaluate
	 * @return a list of fitness values corresponding to each genotype
	 * @throws IllegalArgumentException if genotypes list is null
	 * @throws RuntimeException if evaluation fails due to computational errors or resource issues
	 */
	List<T> evaluate(final long generation, final List<Genotype> genotypes);
}