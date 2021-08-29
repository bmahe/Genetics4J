package net.bmahe.genetics4j.core.chromosomes.factory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableDoubleChromosomeSpec;

public class DoubleChromosomeFactoryTest {

	private final static double EPSILON = 0.0001d;

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new DoubleChromosomeFactory(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingParameter() {
		final var doubleChromosomeFactory = new DoubleChromosomeFactory(new Random());
		doubleChromosomeFactory.canHandle(null);
	}

	@Test
	public void canHandleTest() {
		final Random random = new Random();

		final var doubleChromosomeFactory = new DoubleChromosomeFactory(random);

		assertEquals(true, doubleChromosomeFactory.canHandle(ImmutableDoubleChromosomeSpec.of(10, 0, 5)));
		assertEquals(true, doubleChromosomeFactory.canHandle(ImmutableDoubleChromosomeSpec.of(100, 0, 5)));
		assertEquals(false, doubleChromosomeFactory.canHandle(ImmutableBitChromosomeSpec.of(10)));
	}

	@Test
	public void generateTest() {
		final Random random = mock(Random.class);
		final double[] expectedValues = new double[] { 0.0d, 1.0d, 2.0d, 3.0d, 9.0d };
		when(random.nextDouble())
				.thenReturn(expectedValues[0], expectedValues[1], expectedValues[2], expectedValues[3], expectedValues[4]);

		final var doubleChromosomeFactory = new DoubleChromosomeFactory(random);

		final int doubleChromosomeSize = 5;
		final double minValue = 2.5;
		final double valueRange = 8.2;
		final double maxValue = minValue + valueRange;
		final var doubleChromosomeSpec = ImmutableDoubleChromosomeSpec.of(doubleChromosomeSize, minValue, maxValue);

		final DoubleChromosome doubleChromosome = doubleChromosomeFactory.generate(doubleChromosomeSpec);
		assertEquals(doubleChromosomeSize, doubleChromosome.getNumAlleles());

		for (int i = 0; i < expectedValues.length; i++) {
			assertEquals(minValue + expectedValues[i] * valueRange, doubleChromosome.getValues()[i], EPSILON);
		}
	}
}