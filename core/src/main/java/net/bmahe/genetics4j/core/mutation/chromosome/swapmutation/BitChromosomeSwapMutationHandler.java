package net.bmahe.genetics4j.core.mutation.chromosome.swapmutation;

import java.util.BitSet;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;

public class BitChromosomeSwapMutationHandler implements ChromosomeMutationHandler<BitChromosome> {

	private final Random random;

	public BitChromosomeSwapMutationHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof SwapMutation && chromosome instanceof BitChromosomeSpec;
	}

	@Override
	public BitChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(RandomMutation.class, mutationPolicy);
		Validate.isInstanceOf(BitChromosome.class, chromosome);

		final SwapMutation swapMutation = (SwapMutation) mutationPolicy;
		final int numSwap = swapMutation.numSwap();

		final BitChromosome bitChromosome = (BitChromosome) chromosome;

		final BitSet newBitSet = new BitSet(bitChromosome.getNumAlleles());
		for (int i = 0; i < bitChromosome.getNumAlleles(); i++) {
			newBitSet.or(bitChromosome.getBitSet());
		}

		for (int i = 0; i < numSwap; i++) {
			final int value1Index = random.nextInt(bitChromosome.getNumAlleles());
			final int value2Index = random.nextInt(bitChromosome.getNumAlleles());

			final boolean value1 = newBitSet.get(value1Index);
			final boolean value2 = newBitSet.get(value2Index);

			newBitSet.set(value1Index, value2);
			newBitSet.set(value2Index, value1);
		}
		final BitChromosome newBitChromosome = new BitChromosome(bitChromosome.getNumAlleles(), newBitSet);

		return newBitChromosome;
	}
}