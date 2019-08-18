package net.bmahe.genetics4j.core.mutation.chromosome.randommutation;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;

public class IntChromosomeRandomMutationHandler implements ChromosomeMutationHandler<IntChromosome> {

	private final static int MAX_RETRY_FLIP = 100;

	private final Random random;

	public IntChromosomeRandomMutationHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
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
		final int[] originalValues = intChromosome.getValues();

		int[] newValues = Arrays.copyOf(intChromosome.getValues(), intChromosome.getNumAlleles());

		int retryCount = 0;
		do {

			if (retryCount > MAX_RETRY_FLIP) {
				throw new IllegalStateException("Could not mutate chromosome " + chromosome + " after " + MAX_RETRY_FLIP
						+ " attempts. Check configuration");
			}

			final int alleleFlipIndex = random.nextInt(intChromosome.getNumAlleles());
			newValues[alleleFlipIndex] = random.nextInt(intChromosome.getMaxValue() - intChromosome.getMinValue())
					+ intChromosome.getMinValue();

			retryCount++;
		} while (Arrays.equals(originalValues, newValues));

		return new IntChromosome(intChromosome.getSize(), intChromosome.getMinValue(), intChromosome.getMaxValue(),
				newValues);
	}
}