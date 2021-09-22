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
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.ImmutableRandomMutation;

public class IntChromosomeRandomMutationHandlerTest {

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new IntChromosomeRandomMutationHandler(null));
	}

	@Test
	public void canHandleNullMutationSpec() {
		final IntChromosomeRandomMutationHandler intChromosomeRandomMutationHandler = new IntChromosomeRandomMutationHandler(
				new Random());

		assertThrows(NullPointerException.class,
				() -> intChromosomeRandomMutationHandler.canHandle(null, ImmutableIntChromosomeSpec.of(10, 0, 5)));
	}

	@Test
	public void canHandleNullChromosomeSpec() {
		final IntChromosomeRandomMutationHandler intChromosomeRandomMutationHandler = new IntChromosomeRandomMutationHandler(
				new Random());

		assertThrows(NullPointerException.class,
				() -> intChromosomeRandomMutationHandler.canHandle(ImmutableRandomMutation.of(0.1), null));
	}

	@Test
	public void canHandle() {
		final IntChromosomeRandomMutationHandler intChromosomeRandomMutationHandler = new IntChromosomeRandomMutationHandler(
				new Random());

		assertTrue(intChromosomeRandomMutationHandler.canHandle(ImmutableRandomMutation.of(0.1),
				ImmutableIntChromosomeSpec.of(10, 0, 100)));
		assertFalse(intChromosomeRandomMutationHandler.canHandle(ImmutableRandomMutation.of(0.1),
				ImmutableBitChromosomeSpec.of(54)));
	}

	@Test
	public void mutateValidate() {

		final int flippedIntIndex = 2;
		final int flippedValue = -1;

		final RandomGenerator random = mock(RandomGenerator.class);
		when(random.nextInt(anyInt())).thenReturn(flippedIntIndex, flippedValue);

		final IntChromosomeRandomMutationHandler randomMutationHandler = new IntChromosomeRandomMutationHandler(random);

		final int numInts = 5;
		final IntChromosome intChromosome = new IntChromosome(numInts, 0, 100, IntStream.range(0, numInts).toArray());
		final IntChromosome mutatedIntChromosome = randomMutationHandler.mutate(ImmutableRandomMutation.of(0.1),
				intChromosome);

		assertEquals(intChromosome.getNumAlleles(), mutatedIntChromosome.getNumAlleles());
		for (int i = 0; i < numInts; i++) {
			final int expectedValue = i == flippedIntIndex ? flippedValue : i;
			assertEquals(expectedValue, mutatedIntChromosome.getValues()[i]);
		}
	}
}