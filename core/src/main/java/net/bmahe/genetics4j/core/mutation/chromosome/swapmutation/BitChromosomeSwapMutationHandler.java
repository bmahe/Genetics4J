package net.bmahe.genetics4j.core.mutation.chromosome.swapmutation;

import java.util.BitSet;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;

public class BitChromosomeSwapMutationHandler implements ChromosomeMutationHandler<BitChromosome> {

	private final RandomGenerator randomGenerator;

	public BitChromosomeSwapMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
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
		Validate.isInstanceOf(SwapMutation.class, mutationPolicy);
		Validate.isInstanceOf(BitChromosome.class, chromosome);

		final SwapMutation swapMutation = (SwapMutation) mutationPolicy;
		final int numSwap = swapMutation.isNumSwapFixed() ? swapMutation.numSwap()
				: 1 + randomGenerator.nextInt(swapMutation.numSwap());

		final BitChromosome bitChromosome = (BitChromosome) chromosome;

		final BitSet newBitSet = new BitSet(bitChromosome.getNumAlleles());
		for (int i = 0; i < bitChromosome.getNumAlleles(); i++) {
			newBitSet.or(bitChromosome.getBitSet());
		}

		for (int i = 0; i < numSwap; i++) {
			final int value1Index = randomGenerator.nextInt(bitChromosome.getNumAlleles());
			final int value2Index = randomGenerator.nextInt(bitChromosome.getNumAlleles());

			final boolean value1 = newBitSet.get(value1Index);
			final boolean value2 = newBitSet.get(value2Index);

			newBitSet.set(value1Index, value2);
			newBitSet.set(value2Index, value1);
		}
		final BitChromosome newBitChromosome = new BitChromosome(bitChromosome.getNumAlleles(), newBitSet);

		return newBitChromosome;
	}
}