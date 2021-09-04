package net.bmahe.genetics4j.core.combination.multipointarithmetic;

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
import net.bmahe.genetics4j.core.spec.combination.MultiPointArithmetic;

public class IntChromosomeMultiPointArithmeticTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new IntChromosomeMultiPointArithmetic(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void combinationPolicyIsRequired() {
		new IntChromosomeMultiPointArithmetic(new Random(), null);
	}

	private int mixAlleles(final int a, final int b, final double alpha) {
		return (int) (a * alpha + b * (1 - alpha));
	}

	@Test
	public void combineTest() {
		final Random mockRandom = mock(Random.class);

		when(mockRandom.ints(anyInt(), anyInt())).thenReturn(IntStream.of(1, 3, 4));

		final double alpha = 0.8;
		final var intChromosomeMultiPointCrossover = new IntChromosomeMultiPointArithmetic(mockRandom,
				MultiPointArithmetic.of(3, alpha));

		final var chromosome1 = new IntChromosome(5, 0, 100, new int[] { 10, 11, 12, 13, 14 });
		final var chromosome2 = new IntChromosome(5, 0, 100, new int[] { 20, 21, 22, 23, 24 });

		final List<Chromosome> combinedChromosomes = intChromosomeMultiPointCrossover.combine(chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());

		final IntChromosome firstCombinedChromosome = (IntChromosome) combinedChromosomes.get(0);
		assertNotNull(firstCombinedChromosome);
		assertEquals(5, firstCombinedChromosome.getNumAlleles());

		assertEquals(mixAlleles(chromosome1.getAllele(0), chromosome2.getAllele(0), alpha),
				firstCombinedChromosome.getAllele(0));
		assertEquals(mixAlleles(chromosome2.getAllele(1), chromosome1.getAllele(1), alpha),
				firstCombinedChromosome.getAllele(1));
		assertEquals(mixAlleles(chromosome2.getAllele(2), chromosome1.getAllele(2), alpha),
				firstCombinedChromosome.getAllele(2));
		assertEquals(mixAlleles(chromosome1.getAllele(3), chromosome2.getAllele(3), alpha),
				firstCombinedChromosome.getAllele(3));
		assertEquals(mixAlleles(chromosome2.getAllele(4), chromosome1.getAllele(4), alpha),
				firstCombinedChromosome.getAllele(4));

		final IntChromosome secondCombinedChromosome = (IntChromosome) combinedChromosomes.get(1);
		assertNotNull(secondCombinedChromosome);
		assertEquals(5, secondCombinedChromosome.getNumAlleles());

		assertEquals(mixAlleles(chromosome2.getAllele(0), chromosome1.getAllele(0), alpha),
				secondCombinedChromosome.getAllele(0));
		assertEquals(mixAlleles(chromosome1.getAllele(1), chromosome2.getAllele(1), alpha),
				secondCombinedChromosome.getAllele(1));
		assertEquals(mixAlleles(chromosome1.getAllele(2), chromosome2.getAllele(2), alpha),
				secondCombinedChromosome.getAllele(2));
		assertEquals(mixAlleles(chromosome2.getAllele(3), chromosome1.getAllele(3), alpha),
				secondCombinedChromosome.getAllele(3));
		assertEquals(mixAlleles(chromosome1.getAllele(4), chromosome2.getAllele(4), alpha),
				secondCombinedChromosome.getAllele(4));
	}
}