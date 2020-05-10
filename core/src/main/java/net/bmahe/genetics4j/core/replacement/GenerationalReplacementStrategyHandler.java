package net.bmahe.genetics4j.core.replacement;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.replacement.GenerationalReplacement;
import net.bmahe.genetics4j.core.spec.replacement.ReplacementStrategy;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class GenerationalReplacementStrategyHandler<T extends Comparable<T>>
		implements ReplacementStrategyHandler<T> {

	@Override
	public boolean canHandle(final ReplacementStrategy replacementStrategy) {
		Validate.notNull(replacementStrategy);

		return replacementStrategy instanceof GenerationalReplacement;
	}

	@Override
	public ReplacementStrategyImplementor<T> resolve(final EAExecutionContext<T> eaExecutionContext,
			final EAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final ReplacementStrategy replacementStrategy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(selectionPolicyHandlerResolver);
		Validate.notNull(replacementStrategy);
		Validate.isInstanceOf(GenerationalReplacement.class, replacementStrategy);

		final GenerationalReplacement generationalReplacement = (GenerationalReplacement) replacementStrategy;

		final SelectionPolicy offspringSelectionPolicy = generationalReplacement.offspringSelectionPolicy();
		final SelectionPolicyHandler<T> offspringSelectionPolicyHandler = selectionPolicyHandlerResolver
				.resolve(offspringSelectionPolicy);
		final Selector<T> offspringSelector = offspringSelectionPolicyHandler
				.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, offspringSelectionPolicy);

		return new GenerationalReplacementImpl<T>(generationalReplacement, offspringSelector);
	}

}
