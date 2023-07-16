package net.bmahe.genetics4j.core.spec;

import java.util.function.Function;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;

public interface MutationPolicyHandlerFactory<T extends Comparable<T>>
		extends Function<AbstractEAExecutionContext<T>, MutationPolicyHandler<T>>
{
}