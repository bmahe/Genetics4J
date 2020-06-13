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
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.replacement.ReplacementStrategyHandler;
import net.bmahe.genetics4j.core.replacement.ReplacementStrategyImplementor;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.replacement.ReplacementStrategy;

public class EASystemFactory {

	private EASystemFactory() {
	}

	public static <T extends Comparable<T>> EASystem<T> from(final EAConfiguration<T> eaConfiguration,
			final EAExecutionContext<T> eaExecutionContext) {
		final ExecutorService executorService = ForkJoinPool.commonPool();
		return from(eaConfiguration, eaExecutionContext, executorService);
	}

	public static <T extends Comparable<T>> EASystem<T> from(final EAConfiguration<T> eaConfiguration,
			final EAExecutionContext<T> eaExecutionContext, final ExecutorService executorService) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(eaExecutionContext);
		Validate.notNull(executorService);

		final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final SelectionPolicyHandler<T> parentSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(eaConfiguration.parentSelectionPolicy());
		final Selector<T> parentSelector = parentSelectionPolicyHandler.resolve(eaExecutionContext,
				eaConfiguration,
				selectionPolicyHandlerResolver,
				eaConfiguration.parentSelectionPolicy());

		final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver = new MutationPolicyHandlerResolver<>(
				eaExecutionContext);

		final ChromosomeCombinatorResolver chromosomeCombinatorResolver = new ChromosomeCombinatorResolver(
				eaExecutionContext);
		final CombinationPolicy combinationPolicy = eaConfiguration.combinationPolicy();
		final List<ChromosomeCombinator> chromosomeCombinators = eaConfiguration.chromosomeSpecs()
				.stream()
				.map((chromosome) -> {
					return chromosomeCombinatorResolver.resolve(combinationPolicy, chromosome);
				})
				.collect(Collectors.toList());

		final List<Mutator> mutators = new ArrayList<Mutator>();
		for (int i = 0; i < eaConfiguration.mutationPolicies().size(); i++) {
			final MutationPolicy mutationPolicy = eaConfiguration.mutationPolicies().get(i);

			final MutationPolicyHandler mutationPolicyHandler = mutationPolicyHandlerResolver.resolve(mutationPolicy);
			final Mutator mutator = mutationPolicyHandler
					.createMutator(eaExecutionContext, eaConfiguration, mutationPolicyHandlerResolver, mutationPolicy);

			mutators.add(mutator);

		}

		final List<ReplacementStrategyHandler<T>> replacementStrategyHandlers = eaExecutionContext
				.replacementStrategyHandlers();
		final ReplacementStrategy replacementStrategy = eaConfiguration.replacementStrategy();
		final Optional<ReplacementStrategyHandler<T>> replacementStrategyHandlerOpt = replacementStrategyHandlers
				.stream()
				.filter(replacementStrategyHandler -> replacementStrategyHandler.canHandle(replacementStrategy))
				.findFirst();
		final ReplacementStrategyHandler<T> replacementStrategyHandler = replacementStrategyHandlerOpt
				.orElseThrow(() -> new IllegalStateException(
						"Could not find an implementation to handle the replacement strategy " + replacementStrategy));
		final ReplacementStrategyImplementor<T> replacementStrategyImplementor = replacementStrategyHandler
				.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, replacementStrategy);

		final long populationSize = eaExecutionContext.populationSize();

		return new EASystem<>(eaConfiguration, populationSize, chromosomeCombinators,
				eaConfiguration.offspringGeneratedRatio(), parentSelector, mutators, replacementStrategyImplementor,
				eaExecutionContext, executorService);
	}
}