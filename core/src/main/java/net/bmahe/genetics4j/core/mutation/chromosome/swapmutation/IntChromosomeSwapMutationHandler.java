package net.bmahe.genetics4j.core.mutation.chromosome.swapmutation;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;

public class IntChromosomeSwapMutationHandler implements ChromosomeMutationHandler<IntChromosome> {

	private final RandomGenerator randomGenerator;

	public IntChromosomeSwapMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof SwapMutation && chromosome instanceof IntChromosomeSpec;
	}

	@Override
	public IntChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(SwapMutation.class, mutationPolicy);
		Validate.isInstanceOf(IntChromosome.class, chromosome);

		final SwapMutation swapMutation = (SwapMutation) mutationPolicy;
		final int numSwap = swapMutation.isNumSwapFixed() ? swapMutation.numSwap()
				: 1 + randomGenerator.nextInt(swapMutation.numSwap());

		final IntChromosome intChromosome = (IntChromosome) chromosome;

		final int[] values = intChromosome.getValues();
		final int[] newValues = Arrays.copyOf(values, values.length);

		for (int i = 0; i < numSwap; i++) {
			final int value1Index = randomGenerator.nextInt(intChromosome.getNumAlleles());
			final int value2Index = randomGenerator.nextInt(intChromosome.getNumAlleles());

			final int value1 = newValues[value1Index];
			final int value2 = newValues[value2Index];

			newValues[value1Index] = value2;
			newValues[value2Index] = value1;
		}

		final IntChromosome newIntChromosome = new IntChromosome(intChromosome.getSize(), intChromosome.getMinValue(),
				intChromosome.getMaxValue(), newValues);

		return newIntChromosome;
	}
}