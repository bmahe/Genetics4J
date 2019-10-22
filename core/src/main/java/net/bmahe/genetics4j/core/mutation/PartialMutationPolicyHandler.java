package net.bmahe.genetics4j.core.mutation;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.PartialMutation;

public class PartialMutationPolicyHandler implements MutationPolicyHandler {

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(PartialMutation.class, mutationPolicy);

		final PartialMutation partialMutation = (PartialMutation) mutationPolicy;

		final MutationPolicy childMutationPolicy = partialMutation.mutationPolicy();
		return mutationPolicyHandlerResolver.canHandle(childMutationPolicy);
	}

	@Override
	public Mutator createMutator(final GeneticSystemDescriptor geneticSystemDescriptor, final GenotypeSpec genotypeSpec,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(PartialMutation.class, mutationPolicy);

		final PartialMutation partialMutation = (PartialMutation) mutationPolicy;

		final int mutatedChromosomeIndex = partialMutation.chromosomeIndex();
		final MutationPolicy childMutationPolicy = partialMutation.mutationPolicy();
		final MutationPolicyHandler mutationPolicyHandler = mutationPolicyHandlerResolver.resolve(childMutationPolicy);

		final Mutator childMutator = mutationPolicyHandler.createMutator(geneticSystemDescriptor, genotypeSpec,
				mutationPolicyHandlerResolver, childMutationPolicy);

		return new Mutator() {

			@Override
			public Genotype mutate(final Genotype original) {
				Validate.notNull(original);

				final Chromosome[] chromosomes = Arrays.copyOf(original.getChromosomes(), original.getChromosomes().length);
				final Genotype mutated = childMutator.mutate(original);

				chromosomes[mutatedChromosomeIndex] = mutated.getChromosome(mutatedChromosomeIndex);

				return new Genotype(chromosomes);
			}
		};
	}

}
