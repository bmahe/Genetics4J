package net.bmahe.genetics4j.core.chromosomes;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class DoubleChromosomeTest {

	private final static double EPSILON = 0.0001d;

	@Test
	public void noValues() {
		assertThrows(NullPointerException.class, () -> new DoubleChromosome(10, 0.0, 10.0, null));
	}

	@Test
	public void zeroSize() {
		assertThrows(IllegalArgumentException.class, () -> new DoubleChromosome(0, 0, 10, new double[2]));
	}

	@Test
	public void negativeSize() {
		assertThrows(IllegalArgumentException.class, () -> new DoubleChromosome(-10, 0, 10, new double[2]));
	}

	@Test
	public void minGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> new DoubleChromosome(10, 100, 10, new double[10]));
	}

	@Test
	public void sizeAndValueLengthDontMatch() {
		assertThrows(IllegalArgumentException.class, () -> new DoubleChromosome(10, 100, 1000, new double[11]));
	}

	@Test
	public void simple() {

		final double[] values = { 10.0, 2, 4, 3, 500, 21 };
		final double minValue = 1.0;
		final double maxValue = 1000.0;

		final DoubleChromosome doubleChromosome1 = new DoubleChromosome(values.length, minValue, maxValue, values);
		final DoubleChromosome doubleChromosome2 = new DoubleChromosome(values.length, minValue, maxValue, values);

		assertEquals(doubleChromosome1, doubleChromosome2);
		assertTrue(doubleChromosome1 != doubleChromosome2);
		assertTrue(doubleChromosome1.getValues() != doubleChromosome2.getValues());
		assertArrayEquals(values, doubleChromosome1.getValues(), EPSILON);
		assertArrayEquals(values, doubleChromosome2.getValues(), EPSILON);
		assertEquals(values.length, doubleChromosome1.getNumAlleles());
		assertEquals(values.length, doubleChromosome1.getSize());
		assertEquals(minValue, doubleChromosome1.getMinValue(), EPSILON);
		assertEquals(maxValue, doubleChromosome1.getMaxValue(), EPSILON);

		for (int i = 0; i < values.length; i++) {
			final double value = values[i];
			assertEquals(value, doubleChromosome1.getAllele(i), EPSILON);
			assertEquals(value, doubleChromosome1.getValues()[i], EPSILON);
		}
	}
}