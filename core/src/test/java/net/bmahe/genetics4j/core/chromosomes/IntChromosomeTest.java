package net.bmahe.genetics4j.core.chromosomes;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IntChromosomeTest {

	@Test
	public void noValues() {
		assertThrows(NullPointerException.class, () -> new IntChromosome(10, 0, 10, null));
	}

	@Test
	public void zeroSize() {
		assertThrows(IllegalArgumentException.class, () -> new IntChromosome(0, 0, 10, new int[2]));
	}

	@Test
	public void negativeSize() {
		assertThrows(IllegalArgumentException.class, () -> new IntChromosome(-10, 0, 10, new int[2]));
	}

	@Test
	public void minGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> new IntChromosome(10, 100, 10, new int[10]));
	}

	@Test
	public void sizeAndValueLengthDontMatch() {
		assertThrows(IllegalArgumentException.class, () -> new IntChromosome(10, 100, 1000, new int[11]));
	}

	@Test
	public void simple() {

		final int[] values = { 10, 2, 4, 3, 500, 21 };
		final int minValue = 1;
		final int maxValue = 1000;

		final IntChromosome intChromosome1 = new IntChromosome(values.length, minValue, maxValue, values);
		final IntChromosome intChromosome2 = new IntChromosome(values.length, minValue, maxValue, values);

		assertEquals(intChromosome1, intChromosome2);
		assertTrue(intChromosome1 != intChromosome2);
		assertTrue(intChromosome1.getValues() != intChromosome2.getValues());
		assertArrayEquals(values, intChromosome1.getValues());
		assertArrayEquals(values, intChromosome2.getValues());
		assertEquals(values.length, intChromosome1.getNumAlleles());
		assertEquals(values.length, intChromosome1.getSize());
		assertEquals(minValue, intChromosome1.getMinValue());
		assertEquals(maxValue, intChromosome1.getMaxValue());

		for (int i = 0; i < values.length; i++) {
			final int value = values[i];
			assertEquals(value, intChromosome1.getAllele(i));
			assertEquals(value, intChromosome1.getValues()[i]);
		}
	}
}