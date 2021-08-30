package net.bmahe.genetics4j.core.mutation.chromosome.randommutation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableDoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.ImmutableRandomMutation;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;

public class DoubleChromosomeRandomMutationHandlerTest {

	private final static double EPSILON = 0.0001d;

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new DoubleChromosomeRandomMutationHandler(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleNullMutationSpec() {
		final var doubleChromosomeRandomMutationHandler = new DoubleChromosomeRandomMutationHandler(new Random());

		doubleChromosomeRandomMutationHandler.canHandle(null, ImmutableDoubleChromosomeSpec.of(10, 0, 5));
	}

	@Test(expected = NullPointerException.class)
	public void canHandleNullChromosomeSpec() {
		final var doubleChromosomeRandomMutationHandler = new DoubleChromosomeRandomMutationHandler(new Random());

		doubleChromosomeRandomMutationHandler.canHandle(ImmutableRandomMutation.of(0.1), null);
	}

	@Test
	public void canHandle() {
		final var doubleChromosomeRandomMutationHandler = new DoubleChromosomeRandomMutationHandler(new Random());

		assertTrue(doubleChromosomeRandomMutationHandler.canHandle(RandomMutation.of(0.1),
				DoubleChromosomeSpec.of(10, 0, 100)));
		assertFalse(doubleChromosomeRandomMutationHandler.canHandle(RandomMutation.of(0.1), BitChromosomeSpec.of(54)));
	}

	@Test
	public void mutateValidate() {

		final int flippedIndex = 2;
		final double flippedValue = -1.01d;

		final Random random = mock(Random.class);
		when(random.nextInt(anyInt())).thenReturn(flippedIndex);
		when(random.nextDouble()).thenReturn(flippedValue);

		final var randomMutationHandler = new DoubleChromosomeRandomMutationHandler(random);

		final int numInts = 5;
		final double minValue = -10.0;
		final double maxValue = 10.0;
		final double[] values = IntStream.range(0, numInts).asDoubleStream().toArray();
		final var chromosome = new DoubleChromosome(numInts, minValue, maxValue, values);
		final var mutatedChromosome = randomMutationHandler.mutate(RandomMutation.of(0.1), chromosome);

		assertEquals(chromosome.getNumAlleles(), mutatedChromosome.getNumAlleles());
		for (int i = 0; i < numInts; i++) {
			final double expectedValue = i == flippedIndex ? minValue + flippedValue * (maxValue - minValue) : i;
			assertEquals(expectedValue, mutatedChromosome.getValues()[i], EPSILON);
		}
	}
}