package net.bmahe.genetics4j.core.replacement;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.replacement.Elitism;
import net.bmahe.genetics4j.core.spec.replacement.ReplacementStrategy;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class ElitismReplacementStrategyHandler<T extends Comparable<T>> implements ReplacementStrategyHandler<T> {

	@Override
	public boolean canHandle(final ReplacementStrategy replacementStrategy) {
		Validate.notNull(replacementStrategy);

		return replacementStrategy instanceof Elitism;
	}

	@Override
	public ReplacementStrategyImplementor<T> resolve(final EAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final ReplacementStrategy replacementStrategy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(selectionPolicyHandlerResolver);
		Validate.notNull(replacementStrategy);
		Validate.isInstanceOf(Elitism.class, replacementStrategy);

		final Elitism elitism = (Elitism) replacementStrategy;

		final SelectionPolicy offspringSelectionPolicy = elitism.offspringSelectionPolicy();
		final SelectionPolicyHandler<T> offspringSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(offspringSelectionPolicy);
		final Selector<T> offspringSelector = offspringSelectionPolicyHandler
				.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, offspringSelectionPolicy);

		final SelectionPolicy survivorSelectionPolicy = elitism.survivorSelectionPolicy();
		final SelectionPolicyHandler<T> survivorSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(survivorSelectionPolicy);
		final Selector<T> survivorSelector = survivorSelectionPolicyHandler
				.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, survivorSelectionPolicy);

		return new ElitismImpl<T>(elitism, offspringSelector, survivorSelector);
	}
}