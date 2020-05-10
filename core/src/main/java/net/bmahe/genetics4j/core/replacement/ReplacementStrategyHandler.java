package net.bmahe.genetics4j.core.replacement;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandlerResolver;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.replacement.ReplacementStrategy;

public interface ReplacementStrategyHandler<T extends Comparable<T>> {

	boolean canHandle(final ReplacementStrategy replacementStrategy);

	ReplacementStrategyImplementor<T> resolve(final EAExecutionContext<T> eaExecutionContext,
			final EAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final ReplacementStrategy replacementStrategy);
}