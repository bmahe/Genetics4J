package net.bmahe.genetics4j.core.util;

import java.util.BitSet;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;

public class BitChromosomeUtils {

	public static int hammingDistance(final BitChromosome bc1, final BitChromosome bc2) {
		Objects.requireNonNull(bc1);
		Objects.requireNonNull(bc2);
		Validate.isTrue(bc1.getNumAlleles() == bc2.getNumAlleles());

		final BitSet bitSet1 = bc1.getBitSet();
		final BitSet bitSet2 = bc2.getBitSet();

		int distance = 0;
		for (int i = 0; i < bc1.getNumAlleles(); i++) {
			if (bitSet1.get(i) != bitSet2.get(i)) {
				distance += 1;
			}
		}

		return distance;
	}
}