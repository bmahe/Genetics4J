package net.bmahe.genetics4j.core.chromosomes.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;

public class ChromosomeFactoryProviderTest {

	@Test
	public void nullChromosomeSpec() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.randomGenerator(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		assertThrows(NullPointerException.class, () -> chromosomeFactoryProvider.provideChromosomeFactory(null));
	}

	@Test
	public void unknownChromosomeSpec() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.randomGenerator(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		assertThrows(IllegalStateException.class,
				() -> chromosomeFactoryProvider.provideChromosomeFactory(new ChromosomeSpec() {
				}));
	}

	@Test
	public void bitChromosomeSpec() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.randomGenerator(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		final ChromosomeFactory<? extends Chromosome> chromosomeFactory = chromosomeFactoryProvider
				.provideChromosomeFactory(ImmutableBitChromosomeSpec.of(10));

		assertTrue(chromosomeFactory instanceof BitChromosomeFactory);
	}

	@Test
	public void intChromosomeSpec() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.randomGenerator(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		final ChromosomeFactory<? extends Chromosome> chromosomeFactory = chromosomeFactoryProvider
				.provideChromosomeFactory(ImmutableIntChromosomeSpec.of(10, 10, 10));

		assertTrue(chromosomeFactory instanceof IntChromosomeFactory);
	}

	@Test
	public void simple() {

		final Random random = new Random();

		final Builder builder = ImmutableChromosomeFactoryProvider.builder();
		builder.randomGenerator(random);
		final ImmutableChromosomeFactoryProvider chromosomeFactoryProvider = builder.build();

		assertTrue(random == chromosomeFactoryProvider.randomGenerator());
		assertNotNull(chromosomeFactoryProvider.chromosomeFactories());
	}
}