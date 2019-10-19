package net.bmahe.genetics4j.core.combination.singlepointcrossover;

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
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;

public class IntChromosomeSinglePointCrossoverTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new IntChromosomeSinglePointCrossover(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingFirstParameter() {
		final Random random = new Random();
		final IntChromosomeSinglePointCrossover intChromosomeSinglePointCrossover = new IntChromosomeSinglePointCrossover(
				random);

		intChromosomeSinglePointCrossover.canHandle(null, ImmutableIntChromosomeSpec.of(10, 0, 5));
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingSecondParameter() {
		final Random random = new Random();
		final IntChromosomeSinglePointCrossover intChromosomeSinglePointCrossover = new IntChromosomeSinglePointCrossover(
				random);

		intChromosomeSinglePointCrossover.canHandle(SinglePointCrossover.build(), null);
	}

	@Test
	public void canHandleTest() {
		final Random random = new Random();
		final IntChromosomeSinglePointCrossover intChromosomeSinglePointCrossover = new IntChromosomeSinglePointCrossover(
				random);

		assertTrue(intChromosomeSinglePointCrossover.canHandle(SinglePointCrossover.build(),
				ImmutableIntChromosomeSpec.of(10, 0, 5)));
		assertFalse(intChromosomeSinglePointCrossover.canHandle(SinglePointCrossover.build(),
				ImmutableBitChromosomeSpec.of(10)));
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

		final IntChromosome combinedChromosomes = intChromosomeSinglePointCrossover.combine(SinglePointCrossover.build(),
				chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(4, combinedChromosomes.getNumAlleles());
		assertEquals(combinedChromosomes.getSize(), combinedChromosomes.getNumAlleles());

		for (int i = 0; i < combinedChromosomes.getNumAlleles(); i++) {
			if (i < splitIndex) {
				assertEquals(chromosome1.getAllele(i), combinedChromosomes.getAllele(i));
			} else {
				assertEquals(chromosome2.getAllele(i), combinedChromosomes.getAllele(i));
			}
		}
	}
}