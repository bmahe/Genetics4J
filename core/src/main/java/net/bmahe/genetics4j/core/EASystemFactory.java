package net.bmahe.genetics4j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.evaluation.FitnessEvaluator;
import net.bmahe.genetics4j.core.evaluation.FitnessEvaluatorBulkAsync;
import net.bmahe.genetics4j.core.evaluation.FitnessEvaluatorSync;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.replacement.ReplacementStrategyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfigurationBulkAsync;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

/**
 * Factory class providing convenient methods for creating properly configured {@link EASystem} instances.
 * 
 * <p>EASystemFactory simplifies the complex process of assembling all the components required for
 * evolutionary algorithms by providing pre-configured factory methods that handle the initialization
 * and wiring of selection strategies, mutation operators, combination policies, and fitness evaluators.
 * 
 * <p>The factory abstracts away the complexity of manually creating and configuring:
 * <ul>
 * <li><strong>Selection policy resolvers</strong>: Components that match selection strategies to implementations</li>
 * <li><strong>Mutation policy resolvers</strong>: Components that handle different mutation strategies</li>
 * <li><strong>Chromosome combinators</strong>: Components responsible for crossover operations</li>
 * <li><strong>Replacement strategy handlers</strong>: Components managing population replacement</li>
 * <li><strong>Fitness evaluators</strong>: Components handling fitness computation strategies</li>
 * </ul>
 * 
 * <p>Factory methods are organized by evaluation strategy:
 * <ul>
 * <li><strong>Synchronous evaluation</strong>: Traditional single-threaded or parallel evaluation using {@link EAConfiguration}</li>
 * <li><strong>Bulk asynchronous evaluation</strong>: Batch processing for external services or GPU acceleration using {@link EAConfigurationBulkAsync}</li>
 * <li><strong>Custom evaluation</strong>: User-provided {@link FitnessEvaluator} implementations</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Simple synchronous evaluation with default thread pool
 * EAConfiguration<Double> config = EAConfigurationBuilder.<Double>builder()
 *     .chromosomeSpecs(chromosomeSpec)
 *     .parentSelectionPolicy(Tournament.of(3))
 *     .combinationPolicy(SinglePointCrossover.build())
 *     .mutationPolicies(List.of(RandomMutation.of(0.1)))
 *     .replacementStrategy(Elitism.builder().offspringRatio(0.8).build())
 *     .build();
 * 
 * EAExecutionContext<Double> context = EAExecutionContextBuilder.<Double>builder()
 *     .populationSize(100)
 *     .fitness(genotype -> computeFitness(genotype))
 *     .termination(Generations.of(100))
 *     .build();
 * 
 * EASystem<Double> eaSystem = EASystemFactory.from(config, context);
 * 
 * // Asynchronous evaluation for expensive fitness functions
 * EAConfigurationBulkAsync<Double> asyncConfig = EAConfigurationBulkAsyncBuilder.<Double>builder()
 *     .chromosomeSpecs(chromosomeSpec)
 *     .parentSelectionPolicy(Tournament.of(3))
 *     .combinationPolicy(SinglePointCrossover.build())
 *     .mutationPolicies(List.of(RandomMutation.of(0.1)))
 *     .replacementStrategy(Elitism.builder().offspringRatio(0.8).build())
 *     .fitnessBulkAsync(genotypes -> evaluateBatch(genotypes))
 *     .build();
 * 
 * EASystem<Double> asyncSystem = EASystemFactory.from(asyncConfig, context);
 * 
 * // Custom evaluation strategy
 * FitnessEvaluator<Double> customEvaluator = new CachedFitnessEvaluator<>(baseFitness, cacheSize);
 * EASystem<Double> customSystem = EASystemFactory.from(config, context, executorService, customEvaluator);
 * }</pre>
 * 
 * <p>Factory method selection guide:
 * <ul>
 * <li><strong>Fast fitness functions</strong>: Use synchronous methods with {@link EAConfiguration}</li>
 * <li><strong>Expensive fitness functions</strong>: Use asynchronous methods with {@link EAConfigurationBulkAsync}</li>
 * <li><strong>External fitness computation</strong>: Use custom evaluator methods</li>
 * <li><strong>GPU acceleration</strong>: Use bulk async methods for batch processing</li>
 * </ul>
 * 
 * <p>Thread pool considerations:
 * <ul>
 * <li><strong>Default behavior</strong>: Uses {@link ForkJoinPool#commonPool()} for automatic parallelization</li>
 * <li><strong>Custom thread pools</strong>: Provide specific {@link ExecutorService} for resource control</li>
 * <li><strong>Single-threaded</strong>: Use {@link java.util.concurrent.Executors#newSingleThreadExecutor()}</li>
 * <li><strong>Resource management</strong>: Caller responsible for shutdown of custom thread pools</li>
 * </ul>
 * 
 * @see EASystem
 * @see EAConfiguration
 * @see EAConfigurationBulkAsync
 * @see EAExecutionContext
 * @see FitnessEvaluator
 */
public class EASystemFactory {

	/**
	 * Prevents instantiation since it's a bunch of static methods
	 */
	private EASystemFactory() {
	}

	/**
	 * Creates an {@link EASystem} with a custom fitness evaluator and explicit thread pool.
	 * 
	 * <p>This is the most flexible factory method that allows complete control over all components
	 * of the evolutionary algorithm system. It assembles and wires all necessary components including
	 * selection strategies, mutation operators, chromosome combinators, and replacement strategies.
	 * 
	 * <p>This method is primarily used internally by other factory methods, but can be used directly
	 * when you need to provide a custom {@link FitnessEvaluator} implementation such as cached,
	 * distributed, or specialized evaluation strategies.
	 * 
	 * @param <T> the type of fitness values, must be comparable for selection operations
	 * @param eaConfiguration the evolutionary algorithm configuration specifying genetic operators and strategies
	 * @param eaExecutionContext the execution context containing population parameters and fitness functions
	 * @param executorService the thread pool for parallel operations (caller responsible for shutdown)
	 * @param fitnessEvaluator the fitness evaluator implementation to use for population evaluation
	 * @return a fully configured {@link EASystem} ready for evolution execution
	 * @throws IllegalArgumentException if any parameter is null
	 * @throws IllegalStateException if no suitable replacement strategy handler can be found
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final AbstractEAConfiguration<T> eaConfiguration,
			final AbstractEAExecutionContext<T> eaExecutionContext, final ExecutorService executorService,
			final FitnessEvaluator<T> fitnessEvaluator) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(eaExecutionContext);
		Validate.notNull(executorService);
		Validate.notNull(fitnessEvaluator);

		final var selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<T>(eaExecutionContext);

		final var parentSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(eaConfiguration.parentSelectionPolicy());

		final var parentSelector = parentSelectionPolicyHandler.resolve(eaExecutionContext,
				eaConfiguration,
				selectionPolicyHandlerResolver,
				eaConfiguration.parentSelectionPolicy());

		final var mutationPolicyHandlerResolver = new MutationPolicyHandlerResolver<T>(eaExecutionContext);

		final var chromosomeCombinatorResolver = new ChromosomeCombinatorResolver<T>(eaExecutionContext);

		final CombinationPolicy combinationPolicy = eaConfiguration.combinationPolicy();
		final List<ChromosomeCombinator<T>> chromosomeCombinators = eaConfiguration.chromosomeSpecs()
				.stream()
				.map((chromosome) -> {
					return chromosomeCombinatorResolver.resolve(combinationPolicy, chromosome);
				})
				.collect(Collectors.toList());

		final List<Mutator> mutators = new ArrayList<>();
		final List<MutationPolicy> mutationPolicies = eaConfiguration.mutationPolicies();
		for (int i = 0; i < mutationPolicies.size(); i++) {
			final var mutationPolicy = mutationPolicies.get(i);

			final var mutationPolicyHandler = mutationPolicyHandlerResolver.resolve(mutationPolicy);
			final var mutator = mutationPolicyHandler
					.createMutator(eaExecutionContext, eaConfiguration, mutationPolicyHandlerResolver, mutationPolicy);

			mutators.add(mutator);

		}

		final var replacementStrategyHandlers = eaExecutionContext.replacementStrategyHandlers();
		final var replacementStrategy = eaConfiguration.replacementStrategy();

		final Optional<ReplacementStrategyHandler<T>> replacementStrategyHandlerOpt = replacementStrategyHandlers.stream()
				.filter(replacementStrategyHandler -> replacementStrategyHandler.canHandle(replacementStrategy))
				.findFirst();

		final ReplacementStrategyHandler<T> replacementStrategyHandler = replacementStrategyHandlerOpt
				.orElseThrow(() -> new IllegalStateException(
						"Could not find an implementation to handle the replacement strategy " + replacementStrategy));
		final var replacementStrategyImplementor = replacementStrategyHandler
				.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, replacementStrategy);

		final long populationSize = eaExecutionContext.populationSize();

		return new EASystem<>(eaConfiguration,
				populationSize,
				chromosomeCombinators,
				eaConfiguration.offspringGeneratedRatio(),
				parentSelector,
				mutators,
				replacementStrategyImplementor,
				eaExecutionContext,
				fitnessEvaluator);
	}

	/**
	 * Creates an {@link EASystem} with synchronous fitness evaluation and explicit thread pool.
	 * 
	 * <p>This method is ideal for scenarios where fitness computation is relatively fast and can benefit
	 * from parallel evaluation across multiple threads. The fitness function defined in the configuration
	 * will be called synchronously for each individual or in parallel depending on the evaluator implementation.
	 * 
	 * <p>Use this method when:
	 * <ul>
	 * <li>Fitness computation is CPU-bound and relatively fast (&lt; 100ms per evaluation)</li>
	 * <li>You want control over the thread pool used for parallel evaluation</li>
	 * <li>You need deterministic thread pool behavior for testing or resource management</li>
	 * </ul>
	 * 
	 * @param <T> the type of fitness values, must be comparable for selection operations
	 * @param eaConfigurationSync the synchronous EA configuration with a simple fitness function
	 * @param eaExecutionContext the execution context containing population and termination parameters
	 * @param executorService the thread pool for parallel fitness evaluation (caller responsible for shutdown)
	 * @return a configured {@link EASystem} using synchronous fitness evaluation
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final EAConfiguration<T> eaConfigurationSync,
			final EAExecutionContext<T> eaExecutionContext, final ExecutorService executorService) {

		final var fitnessEvaluator = new FitnessEvaluatorSync<>(eaExecutionContext, eaConfigurationSync, executorService);
		return from(eaConfigurationSync, eaExecutionContext, executorService, fitnessEvaluator);
	}

	/**
	 * Creates an {@link EASystem} with synchronous fitness evaluation using the common thread pool.
	 * 
	 * <p>This is the most convenient method for creating evolutionary algorithms with simple fitness functions.
	 * It uses {@link ForkJoinPool#commonPool()} for automatic parallelization without requiring explicit
	 * thread pool management.
	 * 
	 * <p>This method is ideal for:
	 * <ul>
	 * <li>Quick prototyping and experimentation</li>
	 * <li>Applications where thread pool management is not critical</li>
	 * <li>Fast fitness functions that can benefit from automatic parallelization</li>
	 * <li>Educational purposes and simple examples</li>
	 * </ul>
	 * 
	 * @param <T> the type of fitness values, must be comparable for selection operations
	 * @param eaConfigurationSync the synchronous EA configuration with a simple fitness function
	 * @param eaExecutionContext the execution context containing population and termination parameters
	 * @return a configured {@link EASystem} using synchronous fitness evaluation with common thread pool
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final EAConfiguration<T> eaConfigurationSync,
			final EAExecutionContext<T> eaExecutionContext) {
		final ExecutorService executorService = ForkJoinPool.commonPool();
		return from(eaConfigurationSync, eaExecutionContext, executorService);
	}

	/**
	 * Creates an {@link EASystem} with bulk asynchronous fitness evaluation and explicit thread pool.
	 * 
	 * <p>This method is designed for expensive fitness functions that can benefit from batch processing
	 * or asynchronous evaluation. The bulk async evaluator can process entire populations at once,
	 * enabling optimization strategies like GPU acceleration, external service calls, or database batch operations.
	 * 
	 * <p>Use this method when:
	 * <ul>
	 * <li>Fitness computation involves external resources (databases, web services, files)</li>
	 * <li>Evaluation can be accelerated through batch processing (GPU, vectorized operations)</li>
	 * <li>Fitness computation is expensive (&gt; 100ms per evaluation)</li>
	 * <li>You need to optimize I/O operations through batching</li>
	 * </ul>
	 * 
	 * @param <T> the type of fitness values, must be comparable for selection operations
	 * @param eaConfigurationBulkAsync the bulk async EA configuration with batch fitness evaluation
	 * @param eaExecutionContext the execution context containing population and termination parameters
	 * @param executorService the thread pool for managing asynchronous operations (caller responsible for shutdown)
	 * @return a configured {@link EASystem} using bulk asynchronous fitness evaluation
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final EAConfigurationBulkAsync<T> eaConfigurationBulkAsync,
			final EAExecutionContext<T> eaExecutionContext, final ExecutorService executorService) {

		final var fitnessEvaluator = new FitnessEvaluatorBulkAsync<>(eaConfigurationBulkAsync, executorService);
		return from(eaConfigurationBulkAsync, eaExecutionContext, executorService, fitnessEvaluator);
	}

	/**
	 * Creates an {@link EASystem} with bulk asynchronous fitness evaluation using the common thread pool.
	 * 
	 * <p>This convenience method provides the benefits of bulk asynchronous evaluation without requiring
	 * explicit thread pool management. It automatically uses {@link ForkJoinPool#commonPool()} for
	 * managing asynchronous operations.
	 * 
	 * <p>This method combines the convenience of automatic thread pool management with the power of
	 * bulk asynchronous evaluation, making it ideal for:
	 * <ul>
	 * <li>Expensive fitness functions in research and experimentation</li>
	 * <li>Applications requiring external resource access without complex thread management</li>
	 * <li>GPU-accelerated fitness evaluation with simple setup</li>
	 * <li>Prototype development with advanced evaluation strategies</li>
	 * </ul>
	 * 
	 * @param <T> the type of fitness values, must be comparable for selection operations
	 * @param eaConfigurationBulkAsync the bulk async EA configuration with batch fitness evaluation
	 * @param eaExecutionContext the execution context containing population and termination parameters
	 * @return a configured {@link EASystem} using bulk asynchronous fitness evaluation with common thread pool
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final EAConfigurationBulkAsync<T> eaConfigurationBulkAsync,
			final EAExecutionContext<T> eaExecutionContext) {
		final ExecutorService executorService = ForkJoinPool.commonPool();
		return from(eaConfigurationBulkAsync, eaExecutionContext, executorService);
	}
}