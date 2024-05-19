package net.bmahe.genetics4j.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;

public class GenotypeGeneratorTest {
	public static final Logger logger = LogManager.getLogger(GenotypeGeneratorTest.class);

	@Test
	public void constructorArgsPresent() {

		final ChromosomeFactoryProvider chromosomeFactoryProvider = ChromosomeFactoryProvider.builder()
				.randomGenerator(RandomGenerator.getDefault())
				.build();

		@SuppressWarnings("unchecked")
		final AbstractEAConfiguration<Integer> mockAEAConfiguration = mock(AbstractEAConfiguration.class);

		assertThrows(NullPointerException.class, () -> new GenotypeGenerator<>(null, null));
		assertThrows(NullPointerException.class, () -> new GenotypeGenerator<>(chromosomeFactoryProvider, null));

		final var genotypeGenerator = new GenotypeGenerator<>(chromosomeFactoryProvider, mockAEAConfiguration);
	}

	@Test
	public void usingDefaultGenerator() {

		final ChromosomeFactoryProvider chromosomeFactoryProvider = ChromosomeFactoryProvider.builder()
				.randomGenerator(RandomGenerator.getDefault())
				.build();

		@SuppressWarnings("unchecked")
		final AbstractEAConfiguration<Integer> mockAEAConfiguration = mock(AbstractEAConfiguration.class);
		when(mockAEAConfiguration.genotypeGenerator()).thenReturn(Optional.empty());
		when(mockAEAConfiguration.numChromosomes()).thenReturn(2);
		when(mockAEAConfiguration.getChromosomeSpec(eq(0))).thenReturn(IntChromosomeSpec.of(10, 0, 10));
		when(mockAEAConfiguration.getChromosomeSpec(eq(1))).thenReturn(BitChromosomeSpec.of(8));

		final var genotypeGenerator = new GenotypeGenerator<>(chromosomeFactoryProvider, mockAEAConfiguration);

		// Invalid number
		assertThrows(IllegalArgumentException.class, () -> genotypeGenerator.generateGenotypes(-5));

		// Valid number
		final List<Genotype> genotypes = genotypeGenerator.generateGenotypes(10);

		assertNotNull(genotypes);
		assertEquals(10, genotypes.size());
		for (final Genotype genotype : genotypes) {
			assertEquals(2, genotype.getSize());

			final Chromosome chromosome0 = genotype.getChromosome(0);
			assertNotNull(chromosome0);
			assertInstanceOf(IntChromosome.class, chromosome0);
			assertEquals(10, chromosome0.getNumAlleles());

			final Chromosome chromosome1 = genotype.getChromosome(1);
			assertNotNull(chromosome1);
			assertInstanceOf(BitChromosome.class, chromosome1);
			assertEquals(8, chromosome1.getNumAlleles());
		}
	}

	@Test
	public void usingCustomGenerator() {

		final int SIZE = 10;

		final ChromosomeFactoryProvider chromosomeFactoryProvider = ChromosomeFactoryProvider.builder()
				.randomGenerator(RandomGenerator.getDefault())
				.build();

		final Supplier<Genotype> customGenotypeSupplier = new Supplier<Genotype>() {

			private AtomicInteger counter = new AtomicInteger(0);

			@Override
			public Genotype get() {
				final int[] intArr = new int[SIZE];
				Arrays.fill(intArr, counter.get());
				var genotype = new Genotype(new IntChromosome(SIZE, 0, 10, intArr));

				counter.incrementAndGet();
				return genotype;
			}
		};

		@SuppressWarnings("unchecked")
		final AbstractEAConfiguration<Integer> mockAEAConfiguration = mock(AbstractEAConfiguration.class);
		when(mockAEAConfiguration.genotypeGenerator()).thenReturn(Optional.empty());
		when(mockAEAConfiguration.numChromosomes()).thenReturn(2);
		when(mockAEAConfiguration.getChromosomeSpec(eq(0))).thenReturn(IntChromosomeSpec.of(10, 0, 10));
		when(mockAEAConfiguration.getChromosomeSpec(eq(1))).thenReturn(BitChromosomeSpec.of(8));
		when(mockAEAConfiguration.genotypeGenerator()).thenReturn(Optional.of(customGenotypeSupplier));

		final var genotypeGenerator = new GenotypeGenerator<>(chromosomeFactoryProvider, mockAEAConfiguration);

		// Invalid number
		assertThrows(IllegalArgumentException.class, () -> genotypeGenerator.generateGenotypes(-5));

		// Valid number
		final List<Genotype> genotypes = genotypeGenerator.generateGenotypes(10);

		assertNotNull(genotypes);
		assertEquals(10, genotypes.size());
		int counter = 0;
		for (final Genotype genotype : genotypes) {
			assertEquals(1, genotype.getSize());

			final Chromosome chromosome = genotype.getChromosome(0);
			assertNotNull(chromosome);
			assertInstanceOf(IntChromosome.class, chromosome);
			assertEquals(10, chromosome.getNumAlleles());
			final IntChromosome intChromosome = genotype.getChromosome(0, IntChromosome.class);
			for (int i = 0; i < 10; i++) {
				assertEquals(counter, intChromosome.getAllele(i));
			}
			counter++;
		}
	}
}