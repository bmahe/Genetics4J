package net.bmahe.genetics4j.core.mutation.chromosome.swapmutation;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.FloatChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;

public class FloatChromosomeSwapMutationHandler implements ChromosomeMutationHandler<FloatChromosome> {

	private final RandomGenerator randomGenerator;

	public FloatChromosomeSwapMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof SwapMutation && chromosome instanceof FloatChromosomeSpec;
	}

	@Override
	public FloatChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(SwapMutation.class, mutationPolicy);
		Validate.isInstanceOf(FloatChromosome.class, chromosome);

		final SwapMutation swapMutation = (SwapMutation) mutationPolicy;
		final int numSwap = swapMutation.isNumSwapFixed() ? swapMutation.numSwap()
				: 1 + randomGenerator.nextInt(swapMutation.numSwap());

		final FloatChromosome floatChromosome = (FloatChromosome) chromosome;

		final float[] values = floatChromosome.getValues();
		final float[] newValues = Arrays.copyOf(values, values.length);

		for (int i = 0; i < numSwap; i++) {
			final int value1Index = randomGenerator.nextInt(floatChromosome.getNumAlleles());
			final int value2Index = randomGenerator.nextInt(floatChromosome.getNumAlleles());

			final float value1 = newValues[value1Index];
			final float value2 = newValues[value2Index];

			newValues[value1Index] = value2;
			newValues[value2Index] = value1;
		}

		return new FloatChromosome(floatChromosome.getSize(),
				floatChromosome.getMinValue(),
				floatChromosome.getMaxValue(),
				newValues);
	}
}