package net.bmahe.genetics4j.core.mutation.chromosome.randommutation;

import java.util.BitSet;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;

public class BitChromosomeRandomMutationHandler implements ChromosomeMutationHandler<BitChromosome> {

	private final RandomGenerator randomGenerator;

	public BitChromosomeRandomMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof RandomMutation && chromosome instanceof BitChromosomeSpec;
	}

	@Override
	public BitChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(RandomMutation.class, mutationPolicy);
		Validate.isInstanceOf(BitChromosome.class, chromosome);

		final BitChromosome bitChromosome = (BitChromosome) chromosome;

		final BitSet newBitSet = new BitSet(bitChromosome.getNumAlleles());
		for (int i = 0; i < bitChromosome.getNumAlleles(); i++) {
			newBitSet.or(bitChromosome.getBitSet());
		}

		final int bitFlipIndex = randomGenerator.nextInt(bitChromosome.getNumAlleles());
		newBitSet.flip(bitFlipIndex);

		final BitChromosome newBitChromosome = new BitChromosome(bitChromosome.getNumAlleles(), newBitSet);

		return newBitChromosome;
	}
}