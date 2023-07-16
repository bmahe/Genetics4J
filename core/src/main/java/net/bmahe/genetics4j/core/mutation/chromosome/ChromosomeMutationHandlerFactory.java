package net.bmahe.genetics4j.core.mutation.chromosome;

import java.util.function.Function;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;

public interface ChromosomeMutationHandlerFactory<T extends Comparable<T>>
		extends Function<AbstractEAExecutionContext<T>, ChromosomeMutationHandler<? extends Chromosome>>
{

}