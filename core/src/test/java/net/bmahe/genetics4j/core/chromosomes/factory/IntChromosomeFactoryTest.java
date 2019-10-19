package net.bmahe.genetics4j.core.chromosomes.factory;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;

public class IntChromosomeFactoryTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new IntChromosomeFactory(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleMissingParameter() {
		final IntChromosomeFactory intChromosomeFactory = new IntChromosomeFactory(new Random());
		intChromosomeFactory.canHandle(null);
	}

	@Test
	public void canHandleTest() {
		final Random random = new Random();

		final IntChromosomeFactory intChromosomeFactory = new IntChromosomeFactory(random);

		assertEquals(true, intChromosomeFactory.canHandle(ImmutableIntChromosomeSpec.of(10, 0, 5)));
		assertEquals(true, intChromosomeFactory.canHandle(ImmutableIntChromosomeSpec.of(100, 0, 5)));
		assertEquals(false, intChromosomeFactory.canHandle(ImmutableBitChromosomeSpec.of(10)));
	}

	@Test
	public void generateTest() {
		final Random random = mock(Random.class);
		when(random.nextInt(anyInt())).thenReturn(0, 1, 2, 3, 9);

		final IntChromosomeFactory intChromosomeFactory = new IntChromosomeFactory(random);

		final int intChromosomeSize = 5;
		final ImmutableIntChromosomeSpec intChromosomeSpec = ImmutableIntChromosomeSpec.of(intChromosomeSize, 0, 10);

		final IntChromosome intChromosome = intChromosomeFactory.generate(intChromosomeSpec);
		assertEquals(intChromosomeSize, intChromosome.getNumAlleles());
		assertEquals(0, intChromosome.getValues()[0]);
		assertEquals(1, intChromosome.getValues()[1]);
		assertEquals(2, intChromosome.getValues()[2]);
		assertEquals(3, intChromosome.getValues()[3]);
		assertEquals(9, intChromosome.getValues()[4]);
	}
}