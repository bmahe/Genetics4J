package net.bmahe.genetics4j.core.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Terminations;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class RandomSelectionPolicyHandlerTest {
	private final GenotypeSpec SIMPLE_MAXIMIZING_GENOTYPE_SPEC = new GenotypeSpec.Builder()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelectionPolicy.build())
			.survivorSelectionPolicy(RandomSelectionPolicy.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new RandomSelectionPolicyHandler(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireSelection() {
		final RandomSelectionPolicyHandler selectionPolicyHandler = new RandomSelectionPolicyHandler(new Random());

		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final RandomSelectionPolicyHandler selectionPolicyHandler = new RandomSelectionPolicyHandler(new Random());

		assertTrue(selectionPolicyHandler.canHandle(RandomSelectionPolicy.build()));
		assertFalse(selectionPolicyHandler.canHandle(TournamentSelection.build(2)));
	}

	@Test(expected = NullPointerException.class)
	public void selectRequirePolicy() {
		final RandomSelectionPolicyHandler selectionPolicyHandler = new RandomSelectionPolicyHandler(new Random());

		selectionPolicyHandler.select(SIMPLE_MAXIMIZING_GENOTYPE_SPEC, null, 10, new Genotype[1], new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectRequireNonZeroParent() {
		final RandomSelectionPolicyHandler selectionPolicyHandler = new RandomSelectionPolicyHandler(new Random());

		selectionPolicyHandler.select(SIMPLE_MAXIMIZING_GENOTYPE_SPEC, RandomSelectionPolicy.build(), 0, new Genotype[1],
				new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectRequirePositiveNumberParent() {
		final RandomSelectionPolicyHandler selectionPolicyHandler = new RandomSelectionPolicyHandler(new Random());

		selectionPolicyHandler.select(SIMPLE_MAXIMIZING_GENOTYPE_SPEC, RandomSelectionPolicy.build(), -10,
				new Genotype[1], new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectRequireMatchingPopulationFitnessSizes() {
		final RandomSelectionPolicyHandler selectionPolicyHandler = new RandomSelectionPolicyHandler(new Random());

		selectionPolicyHandler.select(SIMPLE_MAXIMIZING_GENOTYPE_SPEC, RandomSelectionPolicy.build(), 10, new Genotype[1],
				new double[10]);
	}

	@Test
	public void select() {

		final int numRequestedSelection = 100;
		final int evenIndex = 2;
		final int oddIndex = 3;

		final Random random = mock(Random.class);
		when(random.nextInt(anyInt())).thenReturn(2, Stream.iterate(1, i -> i + 1)
				.limit(numRequestedSelection)
				.map(i -> i % 2 == 0 ? evenIndex : oddIndex)
				.toArray((s) -> new Integer[s]));

		final int populationSize = 5;
		final Genotype[] population = new Genotype[populationSize];
		final double[] fitnessScore = new double[populationSize];

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population[i] = genotype;
			fitnessScore[i] = i;
		}

		final RandomSelectionPolicyHandler selectionPolicyHandler = new RandomSelectionPolicyHandler(random);
		final List<Genotype> selected = selectionPolicyHandler.select(SIMPLE_MAXIMIZING_GENOTYPE_SPEC,
				RandomSelectionPolicy.build(), 100, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(100, selected.size());
		for (int i = 0; i < selected.size(); i++) {
			final int expectedIndex = i % 2 == 0 ? evenIndex : oddIndex; // See values returned by the mocked random
			assertEquals(population[expectedIndex], selected.get(i));
		}
	}
}