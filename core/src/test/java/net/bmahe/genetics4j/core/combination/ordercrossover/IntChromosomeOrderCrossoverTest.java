package net.bmahe.genetics4j.core.combination.ordercrossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;

public class IntChromosomeOrderCrossoverTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new IntChromosomeOrderCrossover(null);
	}

	@Test
	public void combineTest() {
		final Random mockRandom = mock(Random.class);

		final int rangeStart = 1;
		final int rangeEnd = 3;
		when(mockRandom.nextInt(anyInt())).thenReturn(rangeStart, rangeEnd);

		final IntChromosomeOrderCrossover intChromosomeOrderCrossover = new IntChromosomeOrderCrossover(mockRandom);

		final IntChromosome chromosome1 = new IntChromosome(5, 0, 10, new int[] { 0, 1, 2, 3, 4 });
		final IntChromosome chromosome2 = new IntChromosome(5, 0, 10, new int[] { 4, 3, 2, 1, 0 });

		final IntChromosome combinedChromosomes = intChromosomeOrderCrossover.combine(chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(5, combinedChromosomes.getNumAlleles());
		assertEquals(combinedChromosomes.getSize(), combinedChromosomes.getNumAlleles());
		assertEquals(chromosome1.getMinValue(), combinedChromosomes.getMinValue());
		assertEquals(chromosome1.getMaxValue(), combinedChromosomes.getMaxValue());

		assertEquals(chromosome2.getAllele(0), combinedChromosomes.getAllele(0));
		assertEquals(chromosome1.getAllele(1), combinedChromosomes.getAllele(1));
		assertEquals(chromosome1.getAllele(2), combinedChromosomes.getAllele(2));
		assertEquals(chromosome2.getAllele(1), combinedChromosomes.getAllele(3));
		assertEquals(chromosome2.getAllele(4), combinedChromosomes.getAllele(4));
	}
}