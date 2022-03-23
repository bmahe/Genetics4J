package net.bmahe.genetics4j.core.mutation.chromosome.creepmutation;

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
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.statistics.distributions.UniformDistribution;

public class FloatChromosomeCreepMutationHandlerTest {

	private final static float EPSILON = 0.0001f;

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new FloatChromosomeCreepMutationHandler(null));
	}

	@Test
	public void canHandleNullMutationSpec() {
		final var floatChromosomeCreepMutationHandler = new FloatChromosomeCreepMutationHandler(new Random());

		assertThrows(NullPointerException.class,
				() -> floatChromosomeCreepMutationHandler.canHandle(null, ImmutableFloatChromosomeSpec.of(10, 0, 5)));
	}

	@Test
	public void canHandleNullChromosomeSpec() {
		final var floatChromosomeCreepMutationHandler = new FloatChromosomeCreepMutationHandler(new Random());

		assertThrows(NullPointerException.class,
				() -> floatChromosomeCreepMutationHandler.canHandle(CreepMutation.ofNormal(0.1, 0.0d, 1.0d), null));
	}

	@Test
	public void canHandle() {
		final var floatChromosomeCreepMutationHandler = new FloatChromosomeCreepMutationHandler(new Random());

		assertTrue(floatChromosomeCreepMutationHandler.canHandle(CreepMutation.ofNormal(0.1, 0.0d, 1.0d),
				FloatChromosomeSpec.of(10, 0, 100)));
		assertFalse(floatChromosomeCreepMutationHandler.canHandle(CreepMutation.ofNormal(0.1, 0.0d, 1.0d),
				BitChromosomeSpec.of(54)));
		assertFalse(floatChromosomeCreepMutationHandler.canHandle(RandomMutation.of(0.1), BitChromosomeSpec.of(54)));
	}

	public void mutateValidate(final int flippedIndex, final float flippedValue, final float expectedFlippedValue,
			final float minValue, final float maxValue) {

		final RandomGenerator random = mock(RandomGenerator.class);
		when(random.nextInt(anyInt())).thenReturn(flippedIndex);
		when(random.nextFloat()).thenReturn(flippedValue);

		final var creepMutationHandler = new FloatChromosomeCreepMutationHandler(random);

		final int numInts = 5;
		final float offset = -2.5f;
		final float[] values = new float[numInts];
		for (int i = 0; i < numInts; i++) {
			values[i] = i + offset;
		}

		final var chromosome = new FloatChromosome(numInts, minValue, maxValue, values);
		final var mutatedChromosome = creepMutationHandler.mutate(CreepMutation.of(0.1, UniformDistribution.build()),
				chromosome);

		assertEquals(chromosome.getNumAlleles(), mutatedChromosome.getNumAlleles());
		for (int i = 0; i < numInts; i++) {
			final float expectedValue = i == flippedIndex ? expectedFlippedValue : i + offset;
			assertEquals(expectedValue, mutatedChromosome.getValues()[i], EPSILON, String.format("at index %d", i));
		}
	}

	@Test
	public void mutateValidate() {

		final int flippedIndex = 2;
		final float minValue = -10.0f;
		final float maxValue = 10.0f;
		final float flippedValue = 0.05f;

		final float expectedFlippedValue = minValue + flippedValue * (maxValue - minValue) + flippedIndex - 2.5f;
		mutateValidate(flippedIndex, flippedValue, expectedFlippedValue, minValue, maxValue);
	}

	@Test
	public void mutateValidateBelowMin() {

		final int flippedIndex = 0;
		final float minValue = -10.0f;
		final float maxValue = 10.0f;
		final float flippedValue = 0.0f;

		/**
		 * Will end up -12.5 which is less than the minValue
		 */
		final float expectedFlippedValue = minValue + flippedValue * (maxValue - minValue) + flippedIndex - 2.5f;
		mutateValidate(flippedIndex, flippedValue, minValue, minValue, maxValue);
	}

	@Test
	public void mutateValidateAboveMax() {

		final int flippedIndex = 4;
		final float minValue = -10.0f;
		final float maxValue = 10.0f;
		final float flippedValue = 1.0f;

		/**
		 * Will end up 11 which is more than the maxValue
		 */
		final float expectedFlippedValue = minValue + flippedValue * (maxValue - minValue) + flippedIndex - 2.5f;
		mutateValidate(flippedIndex, flippedValue, maxValue, minValue, maxValue);
	}
}