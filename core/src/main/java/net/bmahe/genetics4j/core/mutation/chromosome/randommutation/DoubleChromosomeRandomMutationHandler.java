package net.bmahe.genetics4j.core.mutation.chromosome.randommutation;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;

public class DoubleChromosomeRandomMutationHandler implements ChromosomeMutationHandler<DoubleChromosome> {

	private final RandomGenerator randomGenerator;

	public DoubleChromosomeRandomMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof RandomMutation && chromosome instanceof DoubleChromosomeSpec;
	}

	@Override
	public DoubleChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(RandomMutation.class, mutationPolicy);
		Validate.isInstanceOf(DoubleChromosome.class, chromosome);

		final DoubleChromosome doubleChromosome = (DoubleChromosome) chromosome;
		final double minValue = doubleChromosome.getMinValue();
		final double maxValue = doubleChromosome.getMaxValue();

		final double[] newValues = Arrays.copyOf(doubleChromosome.getValues(), doubleChromosome.getNumAlleles());

		final int alleleFlipIndex = randomGenerator.nextInt(doubleChromosome.getNumAlleles());
		newValues[alleleFlipIndex] = minValue + randomGenerator.nextDouble() * (maxValue - minValue);

		return new DoubleChromosome(doubleChromosome.getSize(), minValue, maxValue, newValues);
	}
}