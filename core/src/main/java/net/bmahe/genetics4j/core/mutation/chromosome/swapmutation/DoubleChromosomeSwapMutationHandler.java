package net.bmahe.genetics4j.core.mutation.chromosome.swapmutation;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;

public class DoubleChromosomeSwapMutationHandler implements ChromosomeMutationHandler<DoubleChromosome> {

	private final Random random;

	public DoubleChromosomeSwapMutationHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof SwapMutation && chromosome instanceof DoubleChromosomeSpec;
	}

	@Override
	public DoubleChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(SwapMutation.class, mutationPolicy);
		Validate.isInstanceOf(DoubleChromosome.class, chromosome);

		final SwapMutation swapMutation = (SwapMutation) mutationPolicy;
		final int numSwap = swapMutation.isNumSwapFixed() ? swapMutation.numSwap()
				: 1 + random.nextInt(swapMutation.numSwap());

		final DoubleChromosome doubleChromosome = (DoubleChromosome) chromosome;

		final double[] values = doubleChromosome.getValues();
		final double[] newValues = Arrays.copyOf(values, values.length);

		for (int i = 0; i < numSwap; i++) {
			final int value1Index = random.nextInt(doubleChromosome.getNumAlleles());
			final int value2Index = random.nextInt(doubleChromosome.getNumAlleles());

			final double value1 = newValues[value1Index];
			final double value2 = newValues[value2Index];

			newValues[value1Index] = value2;
			newValues[value2Index] = value1;
		}

		return new DoubleChromosome(doubleChromosome.getSize(), doubleChromosome.getMinValue(),
				doubleChromosome.getMaxValue(), newValues);
	}
}