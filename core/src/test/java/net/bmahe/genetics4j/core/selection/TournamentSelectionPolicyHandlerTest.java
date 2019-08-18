package net.bmahe.genetics4j.core.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class TournamentSelectionPolicyHandlerTest {
	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new TournamentSelectionPolicyHandler(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireSelection() {
		final TournamentSelectionPolicyHandler selectionPolicyHandler = new TournamentSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final TournamentSelectionPolicyHandler selectionPolicyHandler = new TournamentSelectionPolicyHandler(
				new Random());

		assertTrue(selectionPolicyHandler.canHandle(TournamentSelection.build(2)));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelectionPolicy.build()));
		assertFalse(selectionPolicyHandler.canHandle(RouletteWheelSelection.build()));
	}

	@Test(expected = NullPointerException.class)
	public void selectRequirePolicy() {
		final TournamentSelectionPolicyHandler selectionPolicyHandler = new TournamentSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.select(null, 10, new Genotype[1], new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectRequireNonZeroParent() {
		final TournamentSelectionPolicyHandler selectionPolicyHandler = new TournamentSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.select(TournamentSelection.build(2), 0, new Genotype[1], new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectRequirePositiveNumberParent() {
		final TournamentSelectionPolicyHandler selectionPolicyHandler = new TournamentSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.select(TournamentSelection.build(2), -10, new Genotype[1], new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectRequireMatchingPopulationFitnessSizes() {
		final TournamentSelectionPolicyHandler selectionPolicyHandler = new TournamentSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.select(TournamentSelection.build(2), 10, new Genotype[1], new double[10]);
	}

	@Test
	public void select() {

		final Random random = mock(Random.class);
		when(random.nextInt(anyInt())).thenReturn(2, 1, 0, 3, 4); // We will select 2 genotypes with each tournament of 2
																						// candidates

		final int populationSize = 5;
		final Genotype[] population = new Genotype[populationSize];
		final double[] fitnessScore = new double[populationSize];

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population[i] = genotype;
			fitnessScore[i] = i * 10;
		}

		final TournamentSelectionPolicyHandler selectionPolicyHandler = new TournamentSelectionPolicyHandler(random);
		final List<Genotype> selected = selectionPolicyHandler.select(TournamentSelection.build(2), 2, population,
				fitnessScore);

		assertNotNull(selected);
		assertEquals(2, selected.size());
		assertEquals(population[2], selected.get(0));
		assertEquals(population[3], selected.get(1));
	}
}