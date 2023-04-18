package net.bmahe.genetics4j.core.mutation;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

public class GenericMutatorImpl implements Mutator {

	private final RandomGenerator randomGenerator;
	private final ChromosomeMutationHandler<? extends Chromosome>[] chromosomeMutationHandlers;
	private final MutationPolicy mutationPolicy;
	private final double populationMutationProbability;

	public GenericMutatorImpl(final RandomGenerator _randomGenerator,
			final ChromosomeMutationHandler<? extends Chromosome>[] _chromosomeMutationHandlers,
			final MutationPolicy _mutationPolicy, final double _populationMutationProbability) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_chromosomeMutationHandlers);
		Validate.notNull(_mutationPolicy);
		Validate.inclusiveBetween(0.0d, 1.0d, _populationMutationProbability);

		this.randomGenerator = _randomGenerator;
		this.chromosomeMutationHandlers = _chromosomeMutationHandlers;
		this.mutationPolicy = _mutationPolicy;
		this.populationMutationProbability = _populationMutationProbability;
	}

	@Override
	public Genotype mutate(final Genotype original) {
		Validate.notNull(original);

		final Chromosome[] chromosomes = original.getChromosomes();
		final Chromosome[] newChromosomes = new Chromosome[chromosomes.length];

		if (randomGenerator.nextDouble() < populationMutationProbability) {

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
}