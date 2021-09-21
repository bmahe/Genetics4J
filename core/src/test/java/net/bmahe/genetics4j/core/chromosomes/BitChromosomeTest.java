package net.bmahe.genetics4j.core.chromosomes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.BitSet;

import org.junit.jupiter.api.Test;

public class BitChromosomeTest {

	@Test
	public void noNegativeSize() {
		assertThrows(IllegalArgumentException.class, () -> new BitChromosome(-1, new BitSet()));
	}

	@Test
	public void noZeroSize() {
		assertThrows(IllegalArgumentException.class, () -> new BitChromosome(0, new BitSet()));
	}

	@Test
	public void noNullBitSet() {
		assertThrows(NullPointerException.class, () -> new BitChromosome(10, null));
	}

	@Test
	public void sizeAndBitSetMustMatch() {
		assertThrows(IllegalArgumentException.class, () -> new BitChromosome(6005, new BitSet(5)));
	}

	@Test
	public void simple() {
		final int numBits = 10;

		final BitSet bitSet = new BitSet();
		for (int i = 0; i < numBits; i++) {
			bitSet.set(i, i % 2 == 1);
		}
		final BitChromosome bitChromosome1 = new BitChromosome(numBits, bitSet);
		final BitChromosome bitChromosome2 = new BitChromosome(numBits, bitSet);

		assertEquals(numBits, bitChromosome1.getNumAlleles());
		assertEquals(numBits, bitChromosome1.getNumAlleles());
		assertNotNull(bitChromosome1.getBitSet());
		assertEquals(bitSet, bitChromosome1.getBitSet());
		assertEquals(bitChromosome1, bitChromosome2);
		assertEquals(bitChromosome1.getBitSet(), bitChromosome2.getBitSet());

		/**
		 * Bit values are copied and not pointing to the same reference
		 */
		assertTrue(bitChromosome1.getBitSet() != bitSet);
		assertTrue(bitChromosome2.getBitSet() != bitSet);
		assertTrue(bitChromosome1.getBitSet() != bitChromosome2.getBitSet());

		for (int i = 0; i < numBits; i++) {
			final boolean expectedValue = i % 2 == 1;
			assertEquals(expectedValue, bitChromosome1.getBit(i));
			assertEquals(expectedValue, bitChromosome2.getBit(i));
		}

	}
}