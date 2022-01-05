package net.bmahe.genetics4j.core.mutation.chromosome.randommutation;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.FloatChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;

public class FloatChromosomeRandomMutationHandler implements ChromosomeMutationHandler<FloatChromosome> {

	private final RandomGenerator randomGenerator;

	public FloatChromosomeRandomMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof RandomMutation && chromosome instanceof FloatChromosomeSpec;
	}

	@Override
	public FloatChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(RandomMutation.class, mutationPolicy);
		Validate.isInstanceOf(FloatChromosome.class, chromosome);

		final FloatChromosome floatChromosome = (FloatChromosome) chromosome;
		final float minValue = floatChromosome.getMinValue();
		final float maxValue = floatChromosome.getMaxValue();

		final float[] newValues = Arrays.copyOf(floatChromosome.getValues(), floatChromosome.getNumAlleles());

		final int alleleFlipIndex = randomGenerator.nextInt(floatChromosome.getNumAlleles());
		newValues[alleleFlipIndex] = minValue + randomGenerator.nextFloat() * (maxValue - minValue);

		return new FloatChromosome(floatChromosome.getSize(), minValue, maxValue, newValues);
	}
}