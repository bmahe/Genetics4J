package net.bmahe.genetics4j.core.mutation;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public interface MutationPolicyHandler {

	boolean canHandle(MutationPolicy mutationPolicy);

	Genotype mutate(MutationPolicy mutationPolicy, Genotype original,
			List<ChromosomeMutationHandler<? extends Chromosome>> chromosomeMutationHandlers);
}