package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.BitSet;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.ImmutableMultiPointCrossover;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;

public class BitChromosomeSinglePointCrossoverTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new BitChromosomeSinglePointCrossover(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingFirstParameter() {
		final Random random = new Random();
		final BitChromosomeSinglePointCrossover bitChromosomeSinglePointCrossover = new BitChromosomeSinglePointCrossover(
				random);

		bitChromosomeSinglePointCrossover.canHandle(null, ImmutableBitChromosomeSpec.of(10));
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingSecondParameter() {
		final Random random = new Random();
		final BitChromosomeSinglePointCrossover bitChromosomeSinglePointCrossover = new BitChromosomeSinglePointCrossover(
				random);

		bitChromosomeSinglePointCrossover.canHandle(SinglePointCrossover.build(), null);
	}

	@Test
	public void canHandleTest() {
		final Random random = new Random();
		final BitChromosomeSinglePointCrossover bitChromosomeSinglePointCrossover = new BitChromosomeSinglePointCrossover(
				random);

		assertTrue(bitChromosomeSinglePointCrossover.canHandle(SinglePointCrossover.build(),
				ImmutableBitChromosomeSpec.of(10)));
		assertFalse(bitChromosomeSinglePointCrossover.canHandle(ImmutableMultiPointCrossover.of(3),
				ImmutableBitChromosomeSpec.of(10)));
		assertFalse(bitChromosomeSinglePointCrossover.canHandle(SinglePointCrossover.build(),
				ImmutableIntChromosomeSpec.of(10, 0, 10)));
	}

	@Test
	public void combineTest() {
		final Random mockRandom = mock(Random.class);

		final int splitIndex = 2;
		when(mockRandom.nextInt(anyInt())).thenReturn(splitIndex);

		final BitChromosomeSinglePointCrossover bitChromosomeSinglePointCrossover = new BitChromosomeSinglePointCrossover(
				mockRandom);

		final BitChromosome chromosome1 = new BitChromosome(4, BitSet.valueOf(new byte[] { 5 })); // 0101
		final BitChromosome chromosome2 = new BitChromosome(4, BitSet.valueOf(new byte[] { 10 })); // 1010

		final BitChromosome combinedChromosomes = bitChromosomeSinglePointCrossover.combine(SinglePointCrossover.build(),
				chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(4, combinedChromosomes.getNumAlleles());

		for (int i = 0; i < combinedChromosomes.getNumAlleles(); i++) {
			if (i < splitIndex) {
				assertEquals(chromosome1.getBit(i), combinedChromosomes.getBit(i));
			} else {
				assertEquals(chromosome2.getBit(i), combinedChromosomes.getBit(i));
			}
		}
	}
}