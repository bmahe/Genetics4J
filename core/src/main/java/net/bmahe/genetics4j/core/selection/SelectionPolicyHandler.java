package net.bmahe.genetics4j.core.selection;

import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public interface SelectionPolicyHandler<T extends Comparable<T>> {

	boolean canHandle(SelectionPolicy selectionPolicy);

	Selector<T> resolve(AbstractEAExecutionContext<T> eaExecutionContext, AbstractEAConfiguration<T> eaConfiguration,
			SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy);
}