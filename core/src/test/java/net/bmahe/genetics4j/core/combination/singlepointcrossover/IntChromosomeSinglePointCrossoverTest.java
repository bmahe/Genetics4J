package net.bmahe.genetics4j.core.combination.singlepointcrossover;

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

public class IntChromosomeSinglePointCrossoverTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new IntChromosomeSinglePointCrossover(null);
	}

	@Test
	public void combineTest() {
		final Random mockRandom = mock(Random.class);

		final int splitIndex = 2;
		when(mockRandom.nextInt(anyInt())).thenReturn(splitIndex);

		final IntChromosomeSinglePointCrossover intChromosomeSinglePointCrossover = new IntChromosomeSinglePointCrossover(
				mockRandom);

		final IntChromosome chromosome1 = new IntChromosome(4, 0, 10, new int[] { 0, 1, 2, 3 });
		final IntChromosome chromosome2 = new IntChromosome(4, 0, 10, new int[] { 3, 2, 1, 0 });

		final List<Chromosome> combinedChromosomes = intChromosomeSinglePointCrossover.combine(chromosome1, chromosome2);
		final IntChromosome combinedChromosome = (IntChromosome) combinedChromosomes.get(0);
		assertNotNull(combinedChromosome);
		assertEquals(4, combinedChromosome.getNumAlleles());
		assertEquals(combinedChromosome.getSize(), combinedChromosome.getNumAlleles());

		for (int i = 0; i < combinedChromosome.getNumAlleles(); i++) {
			if (i < splitIndex) {
				assertEquals(chromosome1.getAllele(i), combinedChromosome.getAllele(i));
			} else {
				assertEquals(chromosome2.getAllele(i), combinedChromosome.getAllele(i));
			}
		}
	}
}