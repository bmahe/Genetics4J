package net.bmahe.genetics4j.core.combination.ordercrossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
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

		final List<Chromosome> combinedChromosomes = intChromosomeOrderCrossover.combine(chromosome1, chromosome2);
		final IntChromosome combinedIntChromosome = (IntChromosome) combinedChromosomes.get(0);
		assertNotNull(combinedIntChromosome);
		assertEquals(5, combinedIntChromosome.getNumAlleles());
		assertEquals(combinedIntChromosome.getSize(), combinedIntChromosome.getNumAlleles());
		assertEquals(chromosome1.getMinValue(), combinedIntChromosome.getMinValue());
		assertEquals(chromosome1.getMaxValue(), combinedIntChromosome.getMaxValue());

		assertEquals(chromosome2.getAllele(0), combinedIntChromosome.getAllele(0));
		assertEquals(chromosome1.getAllele(1), combinedIntChromosome.getAllele(1));
		assertEquals(chromosome1.getAllele(2), combinedIntChromosome.getAllele(2));
		assertEquals(chromosome2.getAllele(1), combinedIntChromosome.getAllele(3));
		assertEquals(chromosome2.getAllele(4), combinedIntChromosome.getAllele(4));
	}
}