package net.bmahe.genetics4j.moo.nsga2.impl;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.SelectionPolicyHandlerFactory;

public class NSGA2SelectionPolicyHandlerFactory<T extends Comparable<T>> implements SelectionPolicyHandlerFactory<T> {

	@Override
	public SelectionPolicyHandler<T> apply(final AbstractEAExecutionContext<T> abstractEAExecutionContext) {
		return new NSGA2SelectionPolicyHandler<T>();
	}
}