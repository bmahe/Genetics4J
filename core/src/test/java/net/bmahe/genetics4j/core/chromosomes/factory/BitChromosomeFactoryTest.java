package net.bmahe.genetics4j.core.chromosomes.factory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;

public class BitChromosomeFactoryTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new BitChromosomeFactory(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingParameter() {
		final BitChromosomeFactory bitChromosomeFactory = new BitChromosomeFactory(new Random());
		bitChromosomeFactory.canHandle(null);
	}

	@Test
	public void canHandleTest() {
		final Random random = new Random();

		final BitChromosomeFactory bitChromosomeFactory = new BitChromosomeFactory(random);

		assertEquals(true, bitChromosomeFactory.canHandle(ImmutableBitChromosomeSpec.of(10)));
		assertEquals(true, bitChromosomeFactory.canHandle(ImmutableBitChromosomeSpec.of(100)));
		assertEquals(false, bitChromosomeFactory.canHandle(ImmutableIntChromosomeSpec.of(10, 0, 100)));
	}

	@Test
	public void generateTest() {
		final Random random = mock(Random.class);
		when(random.nextBoolean()).thenReturn(true, false, true, false);

		final BitChromosomeFactory bitChromosomeFactory = new BitChromosomeFactory(random);

		final int bitChromosomeSize = 4;
		final ImmutableBitChromosomeSpec bitChromosomeSpec = ImmutableBitChromosomeSpec.of(bitChromosomeSize);

		final BitChromosome bitChromosome = bitChromosomeFactory.generate(bitChromosomeSpec);
		assertEquals(bitChromosomeSize, bitChromosome.getNumAlleles());
		assertEquals(true, bitChromosome.getBit(0));
		assertEquals(false, bitChromosome.getBit(1));
		assertEquals(true, bitChromosome.getBit(2));
		assertEquals(false, bitChromosome.getBit(3));
	}
}