package net.bmahe.genetics4j.core.combination.multipointcrossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.combination.ImmutableMultiPointCrossover;

public class IntChromosomeMultiPointCrossoverTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new IntChromosomeMultiPointCrossover(null, null);
	}

	@Test
	public void combineTest() {
		final Random mockRandom = mock(Random.class);

		when(mockRandom.ints(anyInt(), anyInt())).thenReturn(IntStream.of(1, 3, 4));

		final IntChromosomeMultiPointCrossover intChromosomeMultiPointCrossover = new IntChromosomeMultiPointCrossover(
				mockRandom, ImmutableMultiPointCrossover.of(3));

		final IntChromosome chromosome1 = new IntChromosome(5, 0, 100, new int[] { 10, 11, 12, 13, 14 });
		final IntChromosome chromosome2 = new IntChromosome(5, 0, 100, new int[] { 20, 21, 22, 23, 24 });

		final List<Chromosome> combinedChromosomes = intChromosomeMultiPointCrossover.combine(chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());

		final IntChromosome firstCombinedChromosome = (IntChromosome) combinedChromosomes.get(0);
		assertNotNull(firstCombinedChromosome);
		assertEquals(5, firstCombinedChromosome.getNumAlleles());

		assertEquals(chromosome1.getAllele(0), firstCombinedChromosome.getAllele(0));
		assertEquals(chromosome2.getAllele(1), firstCombinedChromosome.getAllele(1));
		assertEquals(chromosome2.getAllele(2), firstCombinedChromosome.getAllele(2));
		assertEquals(chromosome1.getAllele(3), firstCombinedChromosome.getAllele(3));
		assertEquals(chromosome2.getAllele(4), firstCombinedChromosome.getAllele(4));

		final IntChromosome secondCombinedChromosome = (IntChromosome) combinedChromosomes.get(1);
		assertNotNull(secondCombinedChromosome);
		assertEquals(5, secondCombinedChromosome.getNumAlleles());

		assertEquals(chromosome2.getAllele(0), secondCombinedChromosome.getAllele(0));
		assertEquals(chromosome1.getAllele(1), secondCombinedChromosome.getAllele(1));
		assertEquals(chromosome1.getAllele(2), secondCombinedChromosome.getAllele(2));
		assertEquals(chromosome2.getAllele(3), secondCombinedChromosome.getAllele(3));
		assertEquals(chromosome1.getAllele(4), secondCombinedChromosome.getAllele(4));
	}
}