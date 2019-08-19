package net.bmahe.genetics4j.core.mutation;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;

public class SwapMutationPolicyHandler implements MutationPolicyHandler {

	private final Random random;

	public SwapMutationPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof SwapMutation;
	}

	@Override
	public Genotype mutate(final MutationPolicy mutationPolicy, final Genotype original,
			final List<ChromosomeMutationHandler<? extends Chromosome>> chromosomeMutationHandlers) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(original);
		Validate.notNull(chromosomeMutationHandlers);
		Validate.isInstanceOf(SwapMutation.class, mutationPolicy);
		Validate.isTrue(original.getChromosomes().length == chromosomeMutationHandlers.size());

		final SwapMutation swapMutation = (SwapMutation) mutationPolicy;
		final double populationMutationProbability = swapMutation.populationMutationProbability();

		final Chromosome[] chromosomes = original.getChromosomes();
		final Chromosome[] newChromosomes = new Chromosome[chromosomes.length];

		if (random.nextDouble() < populationMutationProbability) {
			for (int i = 0; i < chromosomes.length; i++) {
				final Chromosome chromosome = chromosomes[i];
				final Chromosome mutatedChromosome = chromosomeMutationHandlers.get(i).mutate(mutationPolicy, chromosome);

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
}