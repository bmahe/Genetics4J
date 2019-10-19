package net.bmahe.genetics4j.core.mutation;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.core.util.ChromosomeResolverUtils;

public class SwapMutationPolicyHandler implements MutationPolicyHandler {

	private final Random random;

	public SwapMutationPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof SwapMutation;
	}

	@Override
	public Mutator createMutator(GeneticSystemDescriptor geneticSystemDescriptor, GenotypeSpec genotypeSpec,
			MutationPolicyHandlerResolver mutationPolicyHandlerResolver, MutationPolicy mutationPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(SwapMutation.class, mutationPolicy);

		final SwapMutation swapMutation = (SwapMutation) mutationPolicy;
		final double populationMutationProbability = swapMutation.populationMutationProbability();

		@SuppressWarnings("rawtypes")
		final ChromosomeMutationHandler[] chromosomeMutationHandlers = ChromosomeResolverUtils
				.resolveChromosomeMutationHandlers(geneticSystemDescriptor, genotypeSpec, mutationPolicy);

		return new Mutator() {

			@Override
			public Genotype mutate(final Genotype original) {
				Validate.notNull(original);

				final Chromosome[] chromosomes = original.getChromosomes();
				final Chromosome[] newChromosomes = new Chromosome[chromosomes.length];

				if (random.nextDouble() < populationMutationProbability) {

					for (int i = 0; i < chromosomes.length; i++) {
						final Chromosome chromosome = chromosomes[i];
						final Chromosome mutatedChromosome = chromosomeMutationHandlers[i].mutate(mutationPolicy, chromosome);

						newChromosomes[i] = mutatedChromosome;
					}
				} else {
					for (int i = 0; i < chromosomes.length; i++) {
						final Chromosome chromosome = chromosomes[i];
						newChromosomes[i] = chromosome;
					}
				}

				return new Genotype(newChromosomes);
			}
		};
	}
}