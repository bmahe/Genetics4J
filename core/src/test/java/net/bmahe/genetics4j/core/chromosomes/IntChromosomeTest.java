package net.bmahe.genetics4j.core.chromosomes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;

public class IntChromosomeTest {

	@Test(expected = NullPointerException.class)
	public void noValues() {
		new IntChromosome(10, 0, 10, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void zeroSize() {
		new IntChromosome(0, 0, 10, new int[2]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void negativeSize() {
		new IntChromosome(-10, 0, 10, new int[2]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void minGreaterThanMax() {
		new IntChromosome(10, 100, 10, new int[10]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void sizeAndValueLengthDontMatch() {
		new IntChromosome(10, 100, 1000, new int[11]);
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