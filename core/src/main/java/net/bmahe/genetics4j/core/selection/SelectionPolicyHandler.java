package net.bmahe.genetics4j.core.selection;

import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public interface SelectionPolicyHandler<T extends Comparable<T>> {

	boolean canHandle(SelectionPolicy selectionPolicy);

	Selector<T> resolve(EAExecutionContext<T> eaExecutionContext, EAConfiguration<T> eaConfiguration,
			SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy);
}