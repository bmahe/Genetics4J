package net.bmahe.genetics4j.core.mutation;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.PartialMutation;

public class PartialMutationPolicyHandler<T extends Comparable<T>> implements MutationPolicyHandler<T> {

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		if (mutationPolicy instanceof PartialMutation == false) {
			return false;
		}

		final PartialMutation partialMutation = (PartialMutation) mutationPolicy;

		final MutationPolicy childMutationPolicy = partialMutation.mutationPolicy();
		return mutationPolicyHandlerResolver.canHandle(childMutationPolicy);
	}

	@Override
	public Mutator createMutator(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final MutationPolicyHandlerResolver<T> mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(eaExecutionContext);
		Validate.notNull(eaConfiguration);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(PartialMutation.class, mutationPolicy);

		final PartialMutation partialMutation = (PartialMutation) mutationPolicy;

		final int mutatedChromosomeIndex = partialMutation.chromosomeIndex();
		final MutationPolicy childMutationPolicy = partialMutation.mutationPolicy();
		final MutationPolicyHandler<T> mutationPolicyHandler = mutationPolicyHandlerResolver.resolve(childMutationPolicy);

		final Mutator childMutator = mutationPolicyHandler
				.createMutator(eaExecutionContext, eaConfiguration, mutationPolicyHandlerResolver, childMutationPolicy);

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