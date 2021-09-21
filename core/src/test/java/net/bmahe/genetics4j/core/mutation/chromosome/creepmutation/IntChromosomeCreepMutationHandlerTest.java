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
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.statistics.distributions.UniformDistribution;

public class IntChromosomeCreepMutationHandlerTest {

	private final static double EPSILON = 0.0001d;

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new IntChromosomeCreepMutationHandler(null));
	}

	@Test
	public void canHandleNullMutationSpec() {
		final var intChromosomeCreepMutationHandler = new IntChromosomeCreepMutationHandler(new Random());

		assertThrows(NullPointerException.class,
				() -> intChromosomeCreepMutationHandler.canHandle(null, ImmutableIntChromosomeSpec.of(10, 0, 5)));
	}

	@Test
	public void canHandleNullChromosomeSpec() {
		final var intChromosomeCreepMutationHandler = new IntChromosomeCreepMutationHandler(new Random());

		assertThrows(NullPointerException.class,
				() -> intChromosomeCreepMutationHandler.canHandle(CreepMutation.ofNormal(0.1, 0.0d, 1.0d), null));
	}

	@Test
	public void canHandle() {
		final var intChromosomeCreepMutationHandler = new IntChromosomeCreepMutationHandler(new Random());

		assertTrue(intChromosomeCreepMutationHandler.canHandle(CreepMutation.ofNormal(0.1, 0.0d, 1.0d),
				IntChromosomeSpec.of(10, 0, 100)));
		assertFalse(intChromosomeCreepMutationHandler.canHandle(CreepMutation.ofNormal(0.1, 0.0d, 1.0d),
				BitChromosomeSpec.of(54)));
		assertFalse(intChromosomeCreepMutationHandler.canHandle(RandomMutation.of(0.1), BitChromosomeSpec.of(54)));
	}

	public void mutateValidate(final int flippedIndex, final double flippedValue, final int expectedFlippedValue,
			final int minValue, final int maxValue) {

		final RandomGenerator random = mock(RandomGenerator.class);
		when(random.nextInt(anyInt())).thenReturn(flippedIndex);
		when(random.nextDouble()).thenReturn(flippedValue);

		final var creepMutationHandler = new IntChromosomeCreepMutationHandler(random);

		final int numInts = 5;
		final int offset = -2;
		final int[] values = IntStream.range(0, numInts).map(d -> d + offset).toArray();
		final var chromosome = new IntChromosome(numInts, minValue, maxValue, values);
		final var mutatedChromosome = creepMutationHandler.mutate(CreepMutation.of(0.1, UniformDistribution.build()),
				chromosome);

		assertEquals(chromosome.getNumAlleles(), mutatedChromosome.getNumAlleles());
		for (int i = 0; i < numInts; i++) {
			final double expectedValue = i == flippedIndex ? expectedFlippedValue : i + offset;
			assertEquals(expectedValue, mutatedChromosome.getValues()[i], EPSILON, String.format("at index %d", i));
		}
	}

	@Test
	public void mutateValidate() {

		final int flippedIndex = 2;
		final int minValue = -10;
		final int maxValue = 10;
		final double flippedValue = 0.05d;

		final int expectedFlippedValue = (int) (minValue + flippedValue * (maxValue - minValue) + flippedIndex - 2);
		mutateValidate(flippedIndex, flippedValue, expectedFlippedValue, minValue, maxValue);
	}

	@Test
	public void mutateValidateBelowMin() {

		final int flippedIndex = 0;
		final int minValue = -10;
		final int maxValue = 10;
		final double flippedValue = 0.0d;

		/**
		 * Will end up -12.5 which is less than the minValue
		 */
		mutateValidate(flippedIndex, flippedValue, minValue, minValue, maxValue);
	}

	@Test
	public void mutateValidateAboveMax() {

		final int flippedIndex = 4;
		final int minValue = -10;
		final int maxValue = 10;
		final double flippedValue = 1.0d;

		/**
		 * Will end up 11 which is more than the maxValue
		 */
		mutateValidate(flippedIndex, flippedValue, maxValue, minValue, maxValue);
	}
}