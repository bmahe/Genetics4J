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
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.spec.combination.MultiPointArithmetic;

public class FloatChromosomeMultiPointArithmeticTest {

	private final static float EPSILON = 0.0001f;

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new FloatChromosomeMultiPointArithmetic<Integer>(null, null));
	}

	@Test
	public void combinationPolicyIsRequired() {
		assertThrows(NullPointerException.class,
				() -> new FloatChromosomeMultiPointArithmetic<Integer>(new Random(), null));
	}

	private float mixAlleles(final float a, final float b, final float alpha) {
		return a * alpha + b * (1 - alpha);
	}

	@Test
	public void combineTest() {
		final RandomGenerator mockRandom = mock(RandomGenerator.class);

		when(mockRandom.ints(anyInt(), anyInt())).thenReturn(IntStream.of(1, 3, 4));

		final float alpha = 0.8f;
		final var floatChromosomeMultiPointArithmetic = new FloatChromosomeMultiPointArithmetic<Integer>(mockRandom,
				MultiPointArithmetic.of(3, alpha));

		final var chromosome1 = new FloatChromosome(5, 0, 100, new float[] { 10, 11, 12, 13, 14 });
		final var chromosome2 = new FloatChromosome(5, 0, 100, new float[] { 20, 21, 22, 23, 24 });

		final List<Chromosome> combinedChromosomes = floatChromosomeMultiPointArithmetic
				.combine(null, chromosome1, 1, chromosome2, 1);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());

		final var firstCombinedChromosome = (FloatChromosome) combinedChromosomes.get(0);
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

		final var secondCombinedChromosome = (FloatChromosome) combinedChromosomes.get(1);
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