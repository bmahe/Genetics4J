package net.bmahe.genetics4j.core.mutation.chromosome.randommutation;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;

public class IntChromosomeRandomMutationHandler implements ChromosomeMutationHandler<IntChromosome> {

	private final RandomGenerator randomGenerator;

	public IntChromosomeRandomMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof RandomMutation && chromosome instanceof IntChromosomeSpec;
	}

	@Override
	public IntChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(RandomMutation.class, mutationPolicy);
		Validate.isInstanceOf(IntChromosome.class, chromosome);

		final IntChromosome intChromosome = (IntChromosome) chromosome;

		final int[] newValues = Arrays.copyOf(intChromosome.getValues(), intChromosome.getNumAlleles());

		final int alleleFlipIndex = randomGenerator.nextInt(intChromosome.getNumAlleles());
		newValues[alleleFlipIndex] = randomGenerator.nextInt(intChromosome.getMaxValue() - intChromosome.getMinValue())
				+ intChromosome.getMinValue();

		return new IntChromosome(intChromosome.getSize(), intChromosome.getMinValue(), intChromosome.getMaxValue(),
				newValues);
	}
}