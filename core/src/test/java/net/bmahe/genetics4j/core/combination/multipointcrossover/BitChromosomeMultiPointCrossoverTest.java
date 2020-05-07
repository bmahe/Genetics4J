package net.bmahe.genetics4j.core.combination.multipointcrossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.combination.ImmutableMultiPointCrossover;

public class BitChromosomeMultiPointCrossoverTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new BitChromosomeMultiPointCrossover(null, null);
	}

	@Test
	public void combineTest() {
		final Random mockRandom = mock(Random.class);

		when(mockRandom.ints(anyInt(), anyInt())).thenReturn(IntStream.of(1, 3, 4));

		final BitChromosomeMultiPointCrossover bitChromosomeMultiPointCrossover = new BitChromosomeMultiPointCrossover(
				mockRandom, ImmutableMultiPointCrossover.of(3));

		final BitSet bitSet1 = new BitSet();
		bitSet1.set(1);
		bitSet1.set(3);
		bitSet1.set(4);

		final BitSet bitSet2 = new BitSet();
		bitSet2.set(0);
		bitSet2.set(2);

		final BitChromosome chromosome1 = new BitChromosome(5, bitSet1);
		final BitChromosome chromosome2 = new BitChromosome(5, bitSet2);

		final List<Chromosome> combinedChromosomes = bitChromosomeMultiPointCrossover.combine(chromosome1, chromosome2);
		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());

		final BitChromosome firstCombinedChromosome = (BitChromosome) combinedChromosomes.get(0);
		assertNotNull(firstCombinedChromosome);
		assertEquals(5, firstCombinedChromosome.getNumAlleles());

		assertEquals(chromosome1.getBit(0), firstCombinedChromosome.getBit(0));
		assertEquals(chromosome2.getBit(1), firstCombinedChromosome.getBit(1));
		assertEquals(chromosome2.getBit(2), firstCombinedChromosome.getBit(2));
		assertEquals(chromosome1.getBit(3), firstCombinedChromosome.getBit(3));
		assertEquals(chromosome2.getBit(4), firstCombinedChromosome.getBit(4));

		final BitChromosome secondCombinedChromosome = (BitChromosome) combinedChromosomes.get(1);
		assertNotNull(secondCombinedChromosome);
		assertEquals(5, secondCombinedChromosome.getNumAlleles());

		assertEquals(chromosome2.getBit(0), secondCombinedChromosome.getBit(0));
		assertEquals(chromosome1.getBit(1), secondCombinedChromosome.getBit(1));
		assertEquals(chromosome1.getBit(2), secondCombinedChromosome.getBit(2));
		assertEquals(chromosome2.getBit(3), secondCombinedChromosome.getBit(3));
		assertEquals(chromosome1.getBit(4), secondCombinedChromosome.getBit(4));
	}
}