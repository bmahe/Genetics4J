package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;

public class IntChromosomeSinglePointCrossoverTest {

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new IntChromosomeSinglePointCrossover<Integer>(null));
	}

	@Test
	public void combineTest() {
		final RandomGenerator mockRandom = mock(RandomGenerator.class);

		final int splitIndex = 2;
		when(mockRandom.nextInt(anyInt())).thenReturn(splitIndex);

		final var intChromosomeSinglePointCrossover = new IntChromosomeSinglePointCrossover<Integer>(mockRandom);

		final IntChromosome chromosome1 = new IntChromosome(4, 0, 10, new int[] { 0, 1, 2, 3 });
		final IntChromosome chromosome2 = new IntChromosome(4, 0, 10, new int[] { 3, 2, 1, 0 });

		final List<Chromosome> combinedChromosomes = intChromosomeSinglePointCrossover
				.combine(null, chromosome1, 1, chromosome2, 1);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());
		final IntChromosome combinedFirstChromosome = (IntChromosome) combinedChromosomes.get(0);
		assertNotNull(combinedFirstChromosome);
		assertEquals(4, combinedFirstChromosome.getNumAlleles());
		assertEquals(combinedFirstChromosome.getSize(), combinedFirstChromosome.getNumAlleles());

		final IntChromosome combinedSecondChromosome = (IntChromosome) combinedChromosomes.get(1);
		assertNotNull(combinedSecondChromosome);
		assertEquals(4, combinedSecondChromosome.getNumAlleles());
		assertEquals(combinedSecondChromosome.getSize(), combinedSecondChromosome.getNumAlleles());

		for (int i = 0; i < combinedFirstChromosome.getNumAlleles(); i++) {
			if (i < splitIndex) {
				assertEquals(chromosome1.getAllele(i), combinedFirstChromosome.getAllele(i));
				assertEquals(chromosome2.getAllele(i), combinedSecondChromosome.getAllele(i));
			} else {
				assertEquals(chromosome2.getAllele(i), combinedFirstChromosome.getAllele(i));
				assertEquals(chromosome1.getAllele(i), combinedSecondChromosome.getAllele(i));
			}
		}
	}
}