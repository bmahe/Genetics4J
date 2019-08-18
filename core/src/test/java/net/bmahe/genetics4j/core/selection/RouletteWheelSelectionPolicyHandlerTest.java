package net.bmahe.genetics4j.core.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class RouletteWheelSelectionPolicyHandlerTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new RouletteWheelSelectionPolicyHandler(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireSelection() {
		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				new Random());

		assertTrue(selectionPolicyHandler.canHandle(RouletteWheelSelection.build()));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelectionPolicy.build()));
		assertFalse(selectionPolicyHandler.canHandle(TournamentSelection.build(2)));
	}

	@Test(expected = NullPointerException.class)
	public void selectRequirePolicy() {
		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.select(null, 10, new Genotype[1], new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectRequireNonZeroParent() {
		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.select(RouletteWheelSelection.build(), 0, new Genotype[1], new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectRequirePositiveNumberParent() {
		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.select(RouletteWheelSelection.build(), -10, new Genotype[1], new double[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectRequireMatchingPopulationFitnessSizes() {
		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.select(RouletteWheelSelection.build(), 10, new Genotype[1], new double[10]);
	}

	@Test
	public void select() {

		final Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.1, 0.6, 0.8);

		final int populationSize = 5;
		final Genotype[] population = new Genotype[populationSize];
		final double[] fitnessScore = new double[populationSize];

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population[i] = genotype;
			fitnessScore[i] =  i * 10;
		}

		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				random);
		final List<Genotype> selected = selectionPolicyHandler.select(RouletteWheelSelection.build(), 3, population,
				fitnessScore);

		assertNotNull(selected);
		assertEquals(3, selected.size());
		assertEquals(population[1], selected.get(0));
		assertEquals(population[3], selected.get(1));
		assertEquals(population[4], selected.get(2));
	}
}