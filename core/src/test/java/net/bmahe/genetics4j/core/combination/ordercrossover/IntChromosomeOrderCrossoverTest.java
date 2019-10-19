package net.bmahe.genetics4j.core.combination.ordercrossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.ImmutableOrderCrossover;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;

public class IntChromosomeOrderCrossoverTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new IntChromosomeOrderCrossover(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingFirstParameter() {
		final Random random = new Random();
		final IntChromosomeOrderCrossover intChromosomeOrderCrossover = new IntChromosomeOrderCrossover(random);

		intChromosomeOrderCrossover.canHandle(null, ImmutableIntChromosomeSpec.of(10, 0, 5));
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingSecondParameter() {
		final Random random = new Random();
		final IntChromosomeOrderCrossover intChromosomeOrderCrossover = new IntChromosomeOrderCrossover(random);

		intChromosomeOrderCrossover.canHandle(ImmutableOrderCrossover.builder()
				.build(), null);
	}

	@Test
	public void canHandleTest() {
		final Random random = new Random();
		final IntChromosomeOrderCrossover intChromosomeOrderCrossover = new IntChromosomeOrderCrossover(random);

		assertTrue(intChromosomeOrderCrossover.canHandle(ImmutableOrderCrossover.builder()
				.build(), ImmutableIntChromosomeSpec.of(10, 0, 5)));
		assertFalse(intChromosomeOrderCrossover.canHandle(SinglePointCrossover.build(),
				ImmutableIntChromosomeSpec.of(10, 0, 5)));
		assertFalse(intChromosomeOrderCrossover.canHandle(ImmutableOrderCrossover.builder()
				.build(), ImmutableBitChromosomeSpec.of(10)));
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

		final IntChromosome combinedChromosomes = intChromosomeOrderCrossover.combine(ImmutableOrderCrossover.builder()
				.build(), chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(5, combinedChromosomes.getNumAlleles());
		assertEquals(combinedChromosomes.getSize(), combinedChromosomes.getNumAlleles());
		assertEquals(chromosome1.getMinValue(), combinedChromosomes.getMinValue());
		assertEquals(chromosome1.getMaxValue(), combinedChromosomes.getMaxValue());

		assertEquals(chromosome2.getAllele(0), combinedChromosomes.getAllele(0));
		assertEquals(chromosome1.getAllele(1), combinedChromosomes.getAllele(1));
		assertEquals(chromosome1.getAllele(2), combinedChromosomes.getAllele(2));
		assertEquals(chromosome2.getAllele(1), combinedChromosomes.getAllele(3));
		assertEquals(chromosome2.getAllele(4), combinedChromosomes.getAllele(4));
	}
}