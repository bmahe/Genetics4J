package net.bmahe.genetics4j.core.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.mutation.chromosome.creepmutation.IntChromosomeCreepMutationHandler;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;

public class GenericMutatorImplTest {

	@Test
	public void constructor() {
		final var randomGenerator = RandomGenerator.getDefault();
		final ChromosomeMutationHandler[] chromosomeMutationHandlers = {
				new IntChromosomeCreepMutationHandler(randomGenerator) };
		final MutationPolicy mutationPolicy = RandomMutation.of(0.2);

		assertThrows(NullPointerException.class, () -> new GenericMutatorImpl(null, null, null, 0));
		assertThrows(NullPointerException.class, () -> new GenericMutatorImpl(randomGenerator, null, null, 0));
		assertThrows(NullPointerException.class,
				() -> new GenericMutatorImpl(randomGenerator, chromosomeMutationHandlers, null, 0));

		assertThrows(IllegalArgumentException.class,
				() -> new GenericMutatorImpl(randomGenerator, chromosomeMutationHandlers, mutationPolicy, -0.5));
		assertThrows(IllegalArgumentException.class,
				() -> new GenericMutatorImpl(randomGenerator, chromosomeMutationHandlers, mutationPolicy, 1.5));
	}

	@Test
	public void mutateNull() {
		final var randomGenerator = RandomGenerator.getDefault();
		final var mockChromosomeMutationHandler = mock(ChromosomeMutationHandler.class);

		final ChromosomeMutationHandler[] chromosomeMutationHandlers = { mockChromosomeMutationHandler };
		final MutationPolicy mutationPolicy = RandomMutation.of(0.2);

		final var genericMutatorImplZeroProb = new GenericMutatorImpl(randomGenerator,
				chromosomeMutationHandlers,
				mutationPolicy,
				0);

		assertThrows(NullPointerException.class, () -> genericMutatorImplZeroProb.mutate(null));
	}

	@Test
	public void mutate() {
		final double RANDOM_VALUE = 0.5d;
		final var randomGenerator = RandomGenerator.getDefault();
		final var mockRandom = mock(RandomGenerator.class);
		when(mockRandom.nextDouble()).thenReturn(RANDOM_VALUE);

		final var intChromosomeA = new IntChromosome(5, 0, 10, new int[] { 0, 1, 2, 3, 4 });
		final var intChromosomeB = new IntChromosome(5, 0, 10, new int[] { 1, 2, 3, 4, 5 });
		final var intChromosomeC = new IntChromosome(5, 0, 10, new int[] { 2, 3, 4, 5, 6 });

		final Genotype genotypeA = new Genotype(intChromosomeA);
		final Genotype genotypeB = new Genotype(intChromosomeB);
		final Genotype genotypeC = new Genotype(intChromosomeC);
		final Genotype genotypeAA = new Genotype(intChromosomeA, intChromosomeA);
		final Genotype genotypeAC = new Genotype(intChromosomeA, intChromosomeC);
		final Genotype genotypeBB = new Genotype(intChromosomeB, intChromosomeB);

		final var mockChromosomeMutationHandler = mock(ChromosomeMutationHandler.class);
		when(mockChromosomeMutationHandler.mutate(any(MutationPolicy.class), any(Chromosome.class)))
				.thenReturn(intChromosomeB);

		final ChromosomeMutationHandler[] chromosomeMutationHandlers = { mockChromosomeMutationHandler,
				mockChromosomeMutationHandler };
		final MutationPolicy mutationPolicy = RandomMutation.of(0.2);

		final var genericMutatorImplZeroProb = new GenericMutatorImpl(randomGenerator,
				chromosomeMutationHandlers,
				mutationPolicy,
				0);
		final Genotype mutatedGenotypeAZeroProb = genericMutatorImplZeroProb.mutate(genotypeA);
		assertNotNull(mutatedGenotypeAZeroProb);
		assertEquals(genotypeA, mutatedGenotypeAZeroProb);

		final Genotype mutatedGenotypeAAZeroProb = genericMutatorImplZeroProb.mutate(genotypeAA);
		assertNotNull(mutatedGenotypeAAZeroProb);
		assertEquals(genotypeAA, mutatedGenotypeAAZeroProb);

		final var genericMutatorImplOneProb = new GenericMutatorImpl(randomGenerator,
				chromosomeMutationHandlers,
				mutationPolicy,
				1.0);
		final Genotype mutatedGenotypeAOneProb = genericMutatorImplOneProb.mutate(genotypeA);
		assertNotNull(mutatedGenotypeAOneProb);
		assertEquals(genotypeB, mutatedGenotypeAOneProb);

		final Genotype mutatedGenotypeACOneProb = genericMutatorImplOneProb.mutate(genotypeAC);
		assertNotNull(mutatedGenotypeACOneProb);
		assertEquals(genotypeBB, mutatedGenotypeACOneProb);

	}
}