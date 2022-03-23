package net.bmahe.genetics4j.core.chromosomes.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableFloatChromosomeSpec;

public class FloatChromosomeFactoryTest {

	private final static float EPSILON = 0.0001f;

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new FloatChromosomeFactory(null));
	}

	@Test
	public void canHandleMissingParameter() {
		final var floatChromosomeFactory = new FloatChromosomeFactory(new Random());
		assertThrows(NullPointerException.class, () -> floatChromosomeFactory.canHandle(null));
	}

	@Test
	public void canHandleTest() {
		final Random random = new Random();

		final var floatChromosomeFactory = new FloatChromosomeFactory(random);

		assertEquals(true, floatChromosomeFactory.canHandle(ImmutableFloatChromosomeSpec.of(10, 0f, 5f)));
		assertEquals(true, floatChromosomeFactory.canHandle(ImmutableFloatChromosomeSpec.of(100, 0f, 5f)));
		assertEquals(false, floatChromosomeFactory.canHandle(ImmutableBitChromosomeSpec.of(10)));
	}

	@Test
	public void generateTest() {
		final RandomGenerator random = mock(RandomGenerator.class);
		final float[] expectedValues = new float[] { 0.0f, 1.0f, 2.0f, 3.0f, 9.0f };
		when(random.nextFloat())
				.thenReturn(expectedValues[0], expectedValues[1], expectedValues[2], expectedValues[3], expectedValues[4]);

		final var floatChromosomeFactory = new FloatChromosomeFactory(random);

		final int floatChromosomeSize = 5;
		final float minValue = 2.5f;
		final float valueRange = 8.2f;
		final float maxValue = minValue + valueRange;
		final var floatChromosomeSpec = ImmutableFloatChromosomeSpec.of(floatChromosomeSize, minValue, maxValue);

		final FloatChromosome floatChromosome = floatChromosomeFactory.generate(floatChromosomeSpec);
		assertEquals(floatChromosomeSize, floatChromosome.getNumAlleles());

		for (int i = 0; i < expectedValues.length; i++) {
			assertEquals(minValue + expectedValues[i] * valueRange, floatChromosome.getValues()[i], EPSILON);
		}
	}
}