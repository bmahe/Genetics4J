package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.BitSet;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

public class BitChromosomeFactory implements ChromosomeFactory<BitChromosome> {

	private final RandomGenerator randomGenerator;

	public BitChromosomeFactory(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
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
			bitSet.set(i, randomGenerator.nextBoolean());
		}

		return new BitChromosome(numBits, bitSet);
	}
}