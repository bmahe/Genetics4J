package net.bmahe.genetics4j.core.mutation.chromosome.randommutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.BitSet;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.ImmutableRandomMutation;

public class BitChromosomeRandomMutationHandlerTest {

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new BitChromosomeRandomMutationHandler(null));
	}

	@Test
	public void canHandleNullMutationSpec() {
		final BitChromosomeRandomMutationHandler bitChromosomeRandomMutationHandler = new BitChromosomeRandomMutationHandler(
				new Random());

		assertThrows(NullPointerException.class,
				() -> bitChromosomeRandomMutationHandler.canHandle(null, ImmutableBitChromosomeSpec.of(10)));
	}

	@Test
	public void canHandleNullChromosomeSpec() {
		final BitChromosomeRandomMutationHandler bitChromosomeRandomMutationHandler = new BitChromosomeRandomMutationHandler(
				new Random());

		assertThrows(NullPointerException.class,
				() -> bitChromosomeRandomMutationHandler.canHandle(ImmutableRandomMutation.of(0.1), null));
	}

	@Test
	public void canHandle() {
		final BitChromosomeRandomMutationHandler bitChromosomeRandomMutationHandler = new BitChromosomeRandomMutationHandler(
				new Random());

		assertTrue(bitChromosomeRandomMutationHandler.canHandle(ImmutableRandomMutation.of(0.1),
				ImmutableBitChromosomeSpec.of(54)));
		assertFalse(bitChromosomeRandomMutationHandler.canHandle(ImmutableRandomMutation.of(0.1),
				ImmutableIntChromosomeSpec.of(10, 0, 100)));
	}

	@Test
	public void mutateValidate() {

		final int flippedBitIndex = 2;

		final RandomGenerator random = mock(RandomGenerator.class);
		when(random.nextInt(anyInt())).thenReturn(flippedBitIndex);

		final BitChromosomeRandomMutationHandler randomMutationHandler = new BitChromosomeRandomMutationHandler(random);

		final int numBits = 5;
		final BitSet bitSet = new BitSet();
		bitSet.set(0, numBits, true);
		final BitChromosome bitChromosome = new BitChromosome(numBits, bitSet);
		final BitChromosome mutatedBitChromosome = randomMutationHandler.mutate(ImmutableRandomMutation.of(0.1),
				bitChromosome);

		assertEquals(bitChromosome.getNumAlleles(), mutatedBitChromosome.getNumAlleles());
		for (int i = 0; i < numBits; i++) {
			final boolean expectedValue = i == flippedBitIndex ? false : true;
			assertEquals(expectedValue, mutatedBitChromosome.getBit(i));
		}
	}
}