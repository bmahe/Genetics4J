package net.bmahe.genetics4j.core.combination.multipointcrossover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.spec.combination.ImmutableMultiPointCrossover;

public class DoubleChromosomeMultiPointCrossoverTest {

	private final static double EPSILON = 0.0001d;

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new DoubleChromosomeMultiPointCrossover<Integer>(null, null));
	}

	@Test
	public void combineTest() {
		final RandomGenerator mockRandom = mock(RandomGenerator.class);

		when(mockRandom.ints(anyInt(), anyInt())).thenReturn(IntStream.of(1, 3, 4));

		final var doubleChromosomeMultiPointCrossover = new DoubleChromosomeMultiPointCrossover<Integer>(mockRandom,
				ImmutableMultiPointCrossover.of(3));

		final var chromosome1 = new DoubleChromosome(5, 0, 100, new double[] { 10, 11, 12, 13, 14 });
		final var chromosome2 = new DoubleChromosome(5, 0, 100, new double[] { 20, 21, 22, 23, 24 });

		final List<Chromosome> combinedChromosomes = doubleChromosomeMultiPointCrossover
				.combine(null, chromosome1, 1, chromosome2, 1);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());

		final var firstCombinedChromosome = (DoubleChromosome) combinedChromosomes.get(0);
		assertNotNull(firstCombinedChromosome);
		assertEquals(5, firstCombinedChromosome.getNumAlleles());

		assertEquals(chromosome1.getAllele(0), firstCombinedChromosome.getAllele(0), EPSILON);
		assertEquals(chromosome2.getAllele(1), firstCombinedChromosome.getAllele(1), EPSILON);
		assertEquals(chromosome2.getAllele(2), firstCombinedChromosome.getAllele(2), EPSILON);
		assertEquals(chromosome1.getAllele(3), firstCombinedChromosome.getAllele(3), EPSILON);
		assertEquals(chromosome2.getAllele(4), firstCombinedChromosome.getAllele(4), EPSILON);

		final var secondCombinedChromosome = (DoubleChromosome) combinedChromosomes.get(1);
		assertNotNull(secondCombinedChromosome);
		assertEquals(5, secondCombinedChromosome.getNumAlleles());

		assertEquals(chromosome2.getAllele(0), secondCombinedChromosome.getAllele(0), EPSILON);
		assertEquals(chromosome1.getAllele(1), secondCombinedChromosome.getAllele(1), EPSILON);
		assertEquals(chromosome1.getAllele(2), secondCombinedChromosome.getAllele(2), EPSILON);
		assertEquals(chromosome2.getAllele(3), secondCombinedChromosome.getAllele(3), EPSILON);
		assertEquals(chromosome1.getAllele(4), secondCombinedChromosome.getAllele(4), EPSILON);
	}
}