package net.bmahe.genetics4j.core.mutation;

import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public interface MutationPolicyHandler {

	boolean canHandle(MutationPolicyHandlerResolver mutationPolicyHandlerResolver, MutationPolicy mutationPolicy);

	Mutator createMutator(GeneticSystemDescriptor geneticSystemDescriptor, GenotypeSpec genotypeSpec,
			MutationPolicyHandlerResolver mutationPolicyHandlerResolver, MutationPolicy mutationPolicy);
}