package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;

public class BitChromosomeSinglePointCrossoverTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new BitChromosomeSinglePointCrossover(null);
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

		final List<Chromosome> combinedChromosomes = bitChromosomeSinglePointCrossover.combine(chromosome1,
				chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());

		final BitChromosome firstCombinedChromosome = (BitChromosome) combinedChromosomes.get(0);
		assertNotNull(firstCombinedChromosome);
		assertEquals(4, firstCombinedChromosome.getNumAlleles());

		final BitChromosome secondCombinedChromosome = (BitChromosome) combinedChromosomes.get(1);
		assertNotNull(secondCombinedChromosome);
		assertEquals(4, secondCombinedChromosome.getNumAlleles());

		for (int i = 0; i < firstCombinedChromosome.getNumAlleles(); i++) {
			if (i < splitIndex) {
				assertEquals(chromosome1.getBit(i), firstCombinedChromosome.getBit(i));
				assertEquals(chromosome2.getBit(i), secondCombinedChromosome.getBit(i));
			} else {
				assertEquals(chromosome2.getBit(i), firstCombinedChromosome.getBit(i));
				assertEquals(chromosome1.getBit(i), secondCombinedChromosome.getBit(i));
			}
		}
	}
}