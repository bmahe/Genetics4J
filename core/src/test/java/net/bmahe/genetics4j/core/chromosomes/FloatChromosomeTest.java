package net.bmahe.genetics4j.core.chromosomes;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FloatChromosomeTest {

	private final static float EPSILON = 0.0001f;

	@Test
	public void noValues() {
		assertThrows(NullPointerException.class, () -> new FloatChromosome(10, 0.0f, 10.0f, null));
	}

	@Test
	public void zeroSize() {
		assertThrows(IllegalArgumentException.class, () -> new FloatChromosome(0, 0, 10, new float[2]));
	}

	@Test
	public void negativeSize() {
		assertThrows(IllegalArgumentException.class, () -> new FloatChromosome(-10, 0, 10, new float[2]));
	}

	@Test
	public void minGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> new FloatChromosome(10, 100, 10, new float[10]));
	}

	@Test
	public void sizeAndValueLengthDontMatch() {
		assertThrows(IllegalArgumentException.class, () -> new FloatChromosome(10, 100, 1000, new float[11]));
	}

	@Test
	public void simple() {

		final float[] values = { 10.0f, 2, 4, 3, 500, 21 };
		final float minValue = 1.0f;
		final float maxValue = 1000.0f;

		final FloatChromosome floatChromosome1 = new FloatChromosome(values.length, minValue, maxValue, values);
		final FloatChromosome floatChromosome2 = new FloatChromosome(values.length, minValue, maxValue, values);

		assertEquals(floatChromosome1, floatChromosome2);
		assertTrue(floatChromosome1 != floatChromosome2);
		assertTrue(floatChromosome1.getValues() != floatChromosome2.getValues());
		assertArrayEquals(values, floatChromosome1.getValues(), EPSILON);
		assertArrayEquals(values, floatChromosome2.getValues(), EPSILON);
		assertEquals(values.length, floatChromosome1.getNumAlleles());
		assertEquals(values.length, floatChromosome1.getSize());
		assertEquals(minValue, floatChromosome1.getMinValue(), EPSILON);
		assertEquals(maxValue, floatChromosome1.getMaxValue(), EPSILON);

		for (int i = 0; i < values.length; i++) {
			final float value = values[i];
			assertEquals(value, floatChromosome1.getAllele(i), EPSILON);
			assertEquals(value, floatChromosome1.getValues()[i], EPSILON);
		}
	}
}