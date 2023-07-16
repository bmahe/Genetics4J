package net.bmahe.genetics4j.core.spec;

import java.util.function.Function;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;

public interface ChromosomeCombinatorHandlerFactory<T extends Comparable<T>>
		extends Function<AbstractEAExecutionContext<T>, ChromosomeCombinatorHandler<T>>
{
}