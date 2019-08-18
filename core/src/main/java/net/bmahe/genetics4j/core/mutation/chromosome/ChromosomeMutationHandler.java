package net.bmahe.genetics4j.core.mutation.chromosome;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public interface ChromosomeMutationHandler<T extends Chromosome> {

	boolean canHandle(MutationPolicy mutationPolicy, ChromosomeSpec chromosome);

	T mutate(MutationPolicy mutationPolicy, Chromosome chromosome);
}