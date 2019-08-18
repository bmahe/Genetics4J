package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.BitSet;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

public class BitChromosomeFactory implements ChromosomeFactory<BitChromosome> {

	private final Random random;

	public BitChromosomeFactory(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		return chromosomeSpec instanceof BitChromosomeSpec;
	}

	@Override
	public BitChromosome generate(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);
		Validate.isInstanceOf(BitChromosomeSpec.class, chromosomeSpec);

		final BitChromosomeSpec bitChromosomeSpec = (BitChromosomeSpec) chromosomeSpec;
		final int numBits = bitChromosomeSpec.numBits();

		final BitSet bitSet = new BitSet(numBits);
		for (int i = 0; i < numBits; i++) {
			bitSet.set(i, random.nextBoolean());
		}

		return new BitChromosome(numBits, bitSet);
	}
}