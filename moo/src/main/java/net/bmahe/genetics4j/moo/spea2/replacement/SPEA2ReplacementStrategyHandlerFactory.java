package net.bmahe.genetics4j.moo.spea2.replacement;

import net.bmahe.genetics4j.core.replacement.ReplacementStrategyHandler;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.ReplacementStrategyHandlerFactory;

public class SPEA2ReplacementStrategyHandlerFactory<T extends Comparable<T>> implements ReplacementStrategyHandlerFactory<T>{

	@Override
	public ReplacementStrategyHandler<T> apply(final AbstractEAExecutionContext<T> abstractEAExecutionContext) {
		return new SPEA2ReplacementStrategyHandler<T>();
	}

}