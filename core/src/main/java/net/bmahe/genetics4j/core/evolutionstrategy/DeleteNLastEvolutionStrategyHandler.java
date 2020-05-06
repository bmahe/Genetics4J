package net.bmahe.genetics4j.core.evolutionstrategy;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.evolutionstrategy.EvolutionStrategy;
import net.bmahe.genetics4j.core.spec.evolutionstrategy.DeleteNLast;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class DeleteNLastEvolutionStrategyHandler<T extends Comparable<T>> implements EvolutionStrategyHandler<T> {

	@Override
	public boolean canHandle(final EvolutionStrategy evolutionStrategy) {
		Validate.notNull(evolutionStrategy);

		return evolutionStrategy instanceof DeleteNLast;
	}

	@Override
	public EvolutionStrategyImplementor<T> resolve(final EAExecutionContext<T> eaExecutionContext,
			final EAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final EvolutionStrategy evolutionStrategy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(selectionPolicyHandlerResolver);
		Validate.notNull(evolutionStrategy);
		Validate.isInstanceOf(DeleteNLast.class, evolutionStrategy);

		final DeleteNLast deleteNLast = (DeleteNLast) evolutionStrategy;

		final SelectionPolicy offspringSelectionPolicy = deleteNLast.offspringSelectionPolicy();
		final SelectionPolicyHandler<T> offspringSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(offspringSelectionPolicy);
		final Selector<T> offspringSelector = offspringSelectionPolicyHandler
				.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, offspringSelectionPolicy);

		return new DeleteNLastImpl<T>(deleteNLast, offspringSelector);
	}
}