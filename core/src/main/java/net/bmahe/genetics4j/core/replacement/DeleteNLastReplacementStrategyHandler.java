package net.bmahe.genetics4j.core.replacement;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.replacement.DeleteNLast;
import net.bmahe.genetics4j.core.spec.replacement.ReplacementStrategy;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class DeleteNLastReplacementStrategyHandler<T extends Comparable<T>> implements ReplacementStrategyHandler<T> {

	@Override
	public boolean canHandle(final ReplacementStrategy replacementStrategy) {
		Validate.notNull(replacementStrategy);

		return replacementStrategy instanceof DeleteNLast;
	}

	@Override
	public ReplacementStrategyImplementor<T> resolve(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final ReplacementStrategy replacementStrategy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(selectionPolicyHandlerResolver);
		Validate.notNull(replacementStrategy);
		Validate.isInstanceOf(DeleteNLast.class, replacementStrategy);

		final DeleteNLast deleteNLast = (DeleteNLast) replacementStrategy;

		final SelectionPolicy offspringSelectionPolicy = deleteNLast.offspringSelectionPolicy();
		final SelectionPolicyHandler<T> offspringSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(offspringSelectionPolicy);
		final Selector<T> offspringSelector = offspringSelectionPolicyHandler
				.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, offspringSelectionPolicy);

		return new DeleteNLastImpl<T>(deleteNLast, offspringSelector);
	}
}