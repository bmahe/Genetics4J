package net.bmahe.genetics4j.core.combination.singlepointarithmetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;

public class DoubleChromosomeSinglePointArithmeticTest {

	private final static double EPSILON = 0.0001d;

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new DoubleChromosomeSinglePointArithmetic(null, 0.5d);
	}

	@Test
	public void combineTest() {
		final Random mockRandom = mock(Random.class);

		final int splitIndex = 2;
		when(mockRandom.nextInt(anyInt())).thenReturn(splitIndex);

		final double alpha = 0.2d;
		final var doubleChromosomeSinglePointArithmetic = new DoubleChromosomeSinglePointArithmetic(mockRandom, alpha);

		final var chromosome1 = new DoubleChromosome(4, 0, 10, new double[] { 0, 1, 2, 3 });
		final var chromosome2 = new DoubleChromosome(4, 0, 10, new double[] { 3, 2, 1, 0 });

		final var combinedChromosomes = doubleChromosomeSinglePointArithmetic.combine(chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());

		final var combinedFirstChromosome = (DoubleChromosome) combinedChromosomes.get(0);
		assertNotNull(combinedFirstChromosome);
		assertEquals(4, combinedFirstChromosome.getNumAlleles());
		assertEquals(combinedFirstChromosome.getSize(), combinedFirstChromosome.getNumAlleles());

		final var combinedSecondChromosome = (DoubleChromosome) combinedChromosomes.get(1);
		assertNotNull(combinedSecondChromosome);
		assertEquals(4, combinedSecondChromosome.getNumAlleles());
		assertEquals(combinedSecondChromosome.getSize(), combinedSecondChromosome.getNumAlleles());

		for (int i = 0; i < combinedFirstChromosome.getNumAlleles(); i++) {
			final double firstAllele = chromosome1.getAllele(i);
			final double secondAllele = chromosome2.getAllele(i);

			if (i < splitIndex) {
				assertEquals(alpha * firstAllele + (1 - alpha) * secondAllele,
						combinedFirstChromosome.getAllele(i),
						EPSILON);
				assertEquals((1 - alpha) * firstAllele + alpha * secondAllele,
						combinedSecondChromosome.getAllele(i),
						EPSILON);
			} else {
				assertEquals((1 - alpha) * firstAllele + alpha * secondAllele,
						combinedFirstChromosome.getAllele(i),
						EPSILON);
				assertEquals(alpha * firstAllele + (1 - alpha) * secondAllele,
						combinedSecondChromosome.getAllele(i),
						EPSILON);
			}
		}
	}
}