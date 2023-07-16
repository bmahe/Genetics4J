package net.bmahe.genetics4j.core.spec;

import java.util.function.Function;

import net.bmahe.genetics4j.core.replacement.ReplacementStrategyHandler;

public interface ReplacementStrategyHandlerFactory<T extends Comparable<T>>
		extends Function<AbstractEAExecutionContext<T>, ReplacementStrategyHandler<T>>
{

}