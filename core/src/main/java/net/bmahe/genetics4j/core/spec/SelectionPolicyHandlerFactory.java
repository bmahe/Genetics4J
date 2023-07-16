package net.bmahe.genetics4j.core.spec;

import java.util.function.Function;

import net.bmahe.genetics4j.core.selection.SelectionPolicyHandler;

public interface SelectionPolicyHandlerFactory<T extends Comparable<T>>
		extends Function<AbstractEAExecutionContext<T>, SelectionPolicyHandler<T>>
{
}