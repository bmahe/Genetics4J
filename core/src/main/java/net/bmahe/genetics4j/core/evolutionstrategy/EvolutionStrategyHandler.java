package net.bmahe.genetics4j.core.evolutionstrategy;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.evolutionstrategy.EvolutionStrategy;

public interface EvolutionStrategyHandler<T extends Comparable<T>> {

	boolean canHandle(final EvolutionStrategy evolutionStrategy);

	EvolutionStrategyImplementor<T> resolve(final EAExecutionContext<T> eaExecutionContext,
			final EAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final EvolutionStrategy evolutionStrategy);
}