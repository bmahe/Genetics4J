package net.bmahe.genetics4j.core.combination.multipointarithmetic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.spec.combination.MultiPointArithmetic;

public class DoubleChromosomeMultiPointArithmeticTest {

	private final static double EPSILON = 0.0001d;

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new DoubleChromosomeMultiPointArithmetic<Integer>(null, null));
	}

	@Test
	public void combinationPolicyIsRequired() {
		assertThrows(NullPointerException.class,
				() -> new DoubleChromosomeMultiPointArithmetic<Integer>(new Random(), null));
	}

	private double mixAlleles(final double a, final double b, final double alpha) {
		return a * alpha + b * (1 - alpha);
	}

	@Test
	public void combineTest() {
		final RandomGenerator mockRandom = mock(RandomGenerator.class);

		when(mockRandom.ints(anyInt(), anyInt())).thenReturn(IntStream.of(1, 3, 4));

		final double alpha = 0.8;
		final var doubleChromosomeMultiPointArithmetic = new DoubleChromosomeMultiPointArithmetic<Integer>(mockRandom,
				MultiPointArithmetic.of(3, alpha));

		final var chromosome1 = new DoubleChromosome(5, 0, 100, new double[] { 10, 11, 12, 13, 14 });
		final var chromosome2 = new DoubleChromosome(5, 0, 100, new double[] { 20, 21, 22, 23, 24 });

		final List<Chromosome> combinedChromosomes = doubleChromosomeMultiPointArithmetic
				.combine(null, chromosome1, 1, chromosome2, 1);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());

		final var firstCombinedChromosome = (DoubleChromosome) combinedChromosomes.get(0);
		assertNotNull(firstCombinedChromosome);
		assertEquals(5, firstCombinedChromosome.getNumAlleles());

		assertEquals(mixAlleles(chromosome1.getAllele(0), chromosome2.getAllele(0), alpha),
				firstCombinedChromosome.getAllele(0),
				EPSILON);
		assertEquals(mixAlleles(chromosome2.getAllele(1), chromosome1.getAllele(1), alpha),
				firstCombinedChromosome.getAllele(1),
				EPSILON);
		assertEquals(mixAlleles(chromosome2.getAllele(2), chromosome1.getAllele(2), alpha),
				firstCombinedChromosome.getAllele(2),
				EPSILON);
		assertEquals(mixAlleles(chromosome1.getAllele(3), chromosome2.getAllele(3), alpha),
				firstCombinedChromosome.getAllele(3),
				EPSILON);
		assertEquals(mixAlleles(chromosome2.getAllele(4), chromosome1.getAllele(4), alpha),
				firstCombinedChromosome.getAllele(4),
				EPSILON);

		final var secondCombinedChromosome = (DoubleChromosome) combinedChromosomes.get(1);
		assertNotNull(secondCombinedChromosome);
		assertEquals(5, secondCombinedChromosome.getNumAlleles());

		assertEquals(mixAlleles(chromosome2.getAllele(0), chromosome1.getAllele(0), alpha),
				secondCombinedChromosome.getAllele(0),
				EPSILON);
		assertEquals(mixAlleles(chromosome1.getAllele(1), chromosome2.getAllele(1), alpha),
				secondCombinedChromosome.getAllele(1),
				EPSILON);
		assertEquals(mixAlleles(chromosome1.getAllele(2), chromosome2.getAllele(2), alpha),
				secondCombinedChromosome.getAllele(2),
				EPSILON);
		assertEquals(mixAlleles(chromosome2.getAllele(3), chromosome1.getAllele(3), alpha),
				secondCombinedChromosome.getAllele(3),
				EPSILON);
		assertEquals(mixAlleles(chromosome1.getAllele(4), chromosome2.getAllele(4), alpha),
				secondCombinedChromosome.getAllele(4),
				EPSILON);
	}
}