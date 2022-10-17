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
 * Suite of helper methods to create instances of
 * {@link net.bmahe.genetics4j.core.EASystem}
 *
 */
public class EASystemFactory {

	/**
	 * Prevents instantiation since it's a bunch of static methods
	 */
	private EASystemFactory() {
	}

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
	 * Factory method to create a {@link net.bmahe.genetics4j.core.EASystem} with a
	 * simple fitness computation method
	 * <p>
	 * This is the most common and straight forward approach and ideal when
	 * computing the fitness is fast and straightforward
	 * 
	 * @param <T>
	 * @param eaConfigurationSync
	 * @param eaExecutionContext
	 * @param executorService
	 * @return
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final EAConfiguration<T> eaConfigurationSync,
			final EAExecutionContext<T> eaExecutionContext, final ExecutorService executorService) {

		final var fitnessEvaluator = new FitnessEvaluatorSync<>(eaExecutionContext, eaConfigurationSync, executorService);
		return from(eaConfigurationSync, eaExecutionContext, executorService, fitnessEvaluator);
	}

	/**
	 * Factory method to create a {@link net.bmahe.genetics4j.core.EASystem} with a
	 * simple fitness computation method.
	 * <p>
	 * This is the most common and straight forward approach and ideal when
	 * computing the fitness is fast and straightforward
	 * 
	 * @param <T>
	 * @param eaConfigurationSync
	 * @param eaExecutionContext
	 * @return
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final EAConfiguration<T> eaConfigurationSync,
			final EAExecutionContext<T> eaExecutionContext) {
		final ExecutorService executorService = ForkJoinPool.commonPool();
		return from(eaConfigurationSync, eaExecutionContext, executorService);
	}

	/**
	 * Factory method to create a {@link net.bmahe.genetics4j.core.EASystem} with an
	 * asynchronous fitness computation method
	 * <p>
	 * This is an ideal approach when computing fitnesses requires external requests
	 * or could benefit from bulk processing, such as leveraging GPUs
	 * 
	 * @param <T>
	 * @param eaConfigurationBulkAsync
	 * @param eaExecutionContext
	 * @param executorService
	 * @return
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final EAConfigurationBulkAsync<T> eaConfigurationBulkAsync,
			final EAExecutionContext<T> eaExecutionContext, final ExecutorService executorService) {

		final var fitnessEvaluator = new FitnessEvaluatorBulkAsync<>(eaConfigurationBulkAsync, executorService);
		return from(eaConfigurationBulkAsync, eaExecutionContext, executorService, fitnessEvaluator);
	}

	/**
	 * Factory method to create a {@link net.bmahe.genetics4j.core.EASystem} with an
	 * asynchronous fitness computation method
	 * <p>
	 * This is an ideal approach when computing fitnesses requires external requests
	 * or could benefit from bulk processing, such as leveraging GPUs
	 * 
	 * @param <T>
	 * @param eaConfigurationBulkAsync
	 * @param eaExecutionContext
	 * @return
	 */
	public static <T extends Comparable<T>> EASystem<T> from(final EAConfigurationBulkAsync<T> eaConfigurationBulkAsync,
			final EAExecutionContext<T> eaExecutionContext) {
		final ExecutorService executorService = ForkJoinPool.commonPool();
		return from(eaConfigurationBulkAsync, eaExecutionContext, executorService);
	}
}