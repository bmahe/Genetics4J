package net.bmahe.genetics4j.core.mutation.chromosome.randommutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.FloatChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableFloatChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.ImmutableRandomMutation;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;

public class FloatChromosomeRandomMutationHandlerTest {

	private final static float EPSILON = 0.0001f;

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new FloatChromosomeRandomMutationHandler(null));
	}

	@Test
	public void canHandleNullMutationSpec() {
		final var floatChromosomeRandomMutationHandler = new FloatChromosomeRandomMutationHandler(new Random());

		assertThrows(NullPointerException.class,
				() -> floatChromosomeRandomMutationHandler.canHandle(null, ImmutableFloatChromosomeSpec.of(10, 0, 5)));
	}

	@Test
	public void canHandleNullChromosomeSpec() {
		final var floatChromosomeRandomMutationHandler = new FloatChromosomeRandomMutationHandler(new Random());

		assertThrows(NullPointerException.class,
				() -> floatChromosomeRandomMutationHandler.canHandle(ImmutableRandomMutation.of(0.1), null));
	}

	@Test
	public void canHandle() {
		final var floatChromosomeRandomMutationHandler = new FloatChromosomeRandomMutationHandler(new Random());

		assertTrue(
				floatChromosomeRandomMutationHandler.canHandle(RandomMutation.of(0.1), FloatChromosomeSpec.of(10, 0, 100)));
		assertFalse(floatChromosomeRandomMutationHandler.canHandle(RandomMutation.of(0.1), BitChromosomeSpec.of(54)));
	}

	@Test
	public void mutateValidate() {

		final int flippedIndex = 2;
		final float flippedValue = -1.01f;

		final RandomGenerator random = mock(RandomGenerator.class);
		when(random.nextInt(anyInt())).thenReturn(flippedIndex);
		when(random.nextFloat()).thenReturn(flippedValue);

		final var randomMutationHandler = new FloatChromosomeRandomMutationHandler(random);

		final int numInts = 5;
		final float minValue = -10.0f;
		final float maxValue = 10.0f;
		final float[] values = new float[numInts];
		for (int i = 0; i < numInts; i++) {
			values[i] = i;
		}

		final var chromosome = new FloatChromosome(numInts, minValue, maxValue, values);
		final var mutatedChromosome = randomMutationHandler.mutate(RandomMutation.of(0.1), chromosome);

		assertEquals(chromosome.getNumAlleles(), mutatedChromosome.getNumAlleles());
		for (int i = 0; i < numInts; i++) {
			final float expectedValue = i == flippedIndex ? minValue + flippedValue * (maxValue - minValue) : i;
			assertEquals(expectedValue, mutatedChromosome.getValues()[i], EPSILON);
		}
	}
}