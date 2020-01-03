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

		final List<Chromosome> combinedChromosomes = bitChromosomeSinglePointCrossover.combine(chromosome1, chromosome2);
		final BitChromosome combinedChromosome = (BitChromosome) combinedChromosomes.get(0);
		assertNotNull(combinedChromosome);
		assertEquals(4, combinedChromosome.getNumAlleles());

		for (int i = 0; i < combinedChromosome.getNumAlleles(); i++) {
			if (i < splitIndex) {
				assertEquals(chromosome1.getBit(i), combinedChromosome.getBit(i));
			} else {
				assertEquals(chromosome2.getBit(i), combinedChromosome.getBit(i));
			}
		}
	}
}