package net.bmahe.genetics4j.moo.spea2.replacement;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.replacement.ReplacementStrategyHandler;
import net.bmahe.genetics4j.core.replacement.ReplacementStrategyImplementor;
import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.replacement.ReplacementStrategy;
import net.bmahe.genetics4j.moo.spea2.spec.replacement.SPEA2Replacement;

public class SPEA2ReplacementStrategyHandler<T extends Comparable<T>> implements ReplacementStrategyHandler<T> {

	@Override
	public boolean canHandle(final ReplacementStrategy replacementStrategy) {
		Validate.notNull(replacementStrategy);

		return replacementStrategy instanceof SPEA2Replacement;
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
		Validate.isInstanceOf(SPEA2Replacement.class, replacementStrategy);

		@SuppressWarnings("unchecked")
		final SPEA2Replacement<T> spea2Replacement = (SPEA2Replacement<T>) replacementStrategy;

		return new SPEA2ReplacementStrategyImplementor<T>(spea2Replacement);
	}

}