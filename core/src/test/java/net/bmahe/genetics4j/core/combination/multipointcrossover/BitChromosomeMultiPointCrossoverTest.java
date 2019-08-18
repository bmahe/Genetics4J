package net.bmahe.genetics4j.core.combination.multipointcrossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.BitSet;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.ImmutableMultiPointCrossover;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;

public class BitChromosomeMultiPointCrossoverTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new BitChromosomeMultiPointCrossover(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingFirstParameter() {
		final Random random = new Random();
		final BitChromosomeMultiPointCrossover bitChromosomeMultiPointCrossover = new BitChromosomeMultiPointCrossover(
				random);

		bitChromosomeMultiPointCrossover.canHandle(null, ImmutableBitChromosomeSpec.of(10));
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingSecondParameter() {
		final Random random = new Random();
		final BitChromosomeMultiPointCrossover bitChromosomeMultiPointCrossover = new BitChromosomeMultiPointCrossover(
				random);

		bitChromosomeMultiPointCrossover.canHandle(ImmutableMultiPointCrossover.of(3), null);
	}

	@Test
	public void canHandleTest() {
		final Random random = new Random();
		final BitChromosomeMultiPointCrossover bitChromosomeMultiPointCrossover = new BitChromosomeMultiPointCrossover(
				random);

		assertTrue(bitChromosomeMultiPointCrossover.canHandle(ImmutableMultiPointCrossover.of(3),
				ImmutableBitChromosomeSpec.of(10)));
		assertFalse(bitChromosomeMultiPointCrossover.canHandle(SinglePointCrossover.build(),
				ImmutableBitChromosomeSpec.of(10)));
		assertFalse(bitChromosomeMultiPointCrossover.canHandle(ImmutableMultiPointCrossover.of(3),
				ImmutableIntChromosomeSpec.of(10, 0, 10)));
	}

	@Test
	public void combineTest() {
		final Random mockRandom = mock(Random.class);

		when(mockRandom.ints(anyInt(), anyInt())).thenReturn(IntStream.of(1, 3, 4));

		final BitChromosomeMultiPointCrossover bitChromosomeMultiPointCrossover = new BitChromosomeMultiPointCrossover(
				mockRandom);

		final BitSet bitSet1 = new BitSet();
		bitSet1.set(1);
		bitSet1.set(3);
		bitSet1.set(4);

		final BitSet bitSet2 = new BitSet();
		bitSet2.set(0);
		bitSet2.set(2);

		final BitChromosome chromosome1 = new BitChromosome(5, bitSet1);
		final BitChromosome chromosome2 = new BitChromosome(5, bitSet2);

		final BitChromosome combinedChromosomes = bitChromosomeMultiPointCrossover
				.combine(ImmutableMultiPointCrossover.of(3), chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(5, combinedChromosomes.getNumAlleles());

		assertEquals(chromosome1.getBit(0), combinedChromosomes.getBit(0));
		assertEquals(chromosome2.getBit(1), combinedChromosomes.getBit(1));
		assertEquals(chromosome2.getBit(2), combinedChromosomes.getBit(2));
		assertEquals(chromosome1.getBit(3), combinedChromosomes.getBit(3));
		assertEquals(chromosome2.getBit(4), combinedChromosomes.getBit(4));
	}
}