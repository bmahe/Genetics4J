package net.bmahe.genetics4j.core.chromosomes.factory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;

public class ChromosomeFactoryProviderTest {

	@Test(expected = NullPointerException.class)
	public void nullChromosomeSpec() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.random(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		chromosomeFactoryProvider.provideChromosomeFactory(null);
	}

	@Test(expected = IllegalStateException.class)
	public void unknownChromosomeSpec() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.random(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		chromosomeFactoryProvider.provideChromosomeFactory(new ChromosomeSpec() {
		});
	}

	@Test
	public void bitChromosomeSpec() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.random(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		final ChromosomeFactory<? extends Chromosome> chromosomeFactory = chromosomeFactoryProvider
				.provideChromosomeFactory(ImmutableBitChromosomeSpec.of(10));

		assertTrue(chromosomeFactory instanceof BitChromosomeFactory);
	}

	@Test
	public void intChromosomeSpec() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.random(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		final ChromosomeFactory<? extends Chromosome> chromosomeFactory = chromosomeFactoryProvider
				.provideChromosomeFactory(ImmutableIntChromosomeSpec.of(10, 10, 10));

		assertTrue(chromosomeFactory instanceof IntChromosomeFactory);
	}

	@Test
	public void simple() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.random(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		assertTrue(random == chromosomeFactoryProvider.random());
		assertNotNull(chromosomeFactoryProvider.chromosomeFactories());
	}
}