package net.bmahe.genetics4j.core.combination.singlepointarithmetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;

public class IntChromosomeSinglePointArithmeticTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new IntChromosomeSinglePointArithmetic(null, 0.5d);
	}

	@Test
	public void combineTest() {
		final Random mockRandom = mock(Random.class);

		final int splitIndex = 2;
		when(mockRandom.nextInt(anyInt())).thenReturn(splitIndex);

		final double alpha = 0.2;
		final var intChromosomeSinglePointArithmetic = new IntChromosomeSinglePointArithmetic(mockRandom, alpha);

		final var chromosome1 = new IntChromosome(4, 0, 10, new int[] { 0, 1, 2, 3 });
		final var chromosome2 = new IntChromosome(4, 0, 10, new int[] { 3, 2, 1, 0 });

		final var combinedChromosomes = intChromosomeSinglePointArithmetic.combine(chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());
		final var combinedFirstChromosome = (IntChromosome) combinedChromosomes.get(0);
		assertNotNull(combinedFirstChromosome);
		assertEquals(4, combinedFirstChromosome.getNumAlleles());
		assertEquals(combinedFirstChromosome.getSize(), combinedFirstChromosome.getNumAlleles());

		final var combinedSecondChromosome = (IntChromosome) combinedChromosomes.get(1);
		assertNotNull(combinedSecondChromosome);
		assertEquals(4, combinedSecondChromosome.getNumAlleles());
		assertEquals(combinedSecondChromosome.getSize(), combinedSecondChromosome.getNumAlleles());

		for (int i = 0; i < combinedFirstChromosome.getNumAlleles(); i++) {
			final int firstAllele = chromosome1.getAllele(i);
			final int secondAllele = chromosome2.getAllele(i);

			if (i < splitIndex) {
				assertEquals((int) (alpha * firstAllele + (1 - alpha) * secondAllele),
						combinedFirstChromosome.getAllele(i));
				assertEquals((int) ((1 - alpha) * firstAllele + alpha * secondAllele),
						combinedSecondChromosome.getAllele(i));
			} else {
				assertEquals((int) ((1 - alpha) * firstAllele + alpha * secondAllele),
						combinedFirstChromosome.getAllele(i));
				assertEquals((int) (alpha * firstAllele + (1 - alpha) * secondAllele),
						combinedSecondChromosome.getAllele(i));
			}
		}
	}
}