package net.bmahe.genetics4j.moo.nsga2.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.factory.BitChromosomeFactory;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.moo.FitnessVector;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableNSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;

public class NSGA2SelectorTest {

	private final NSGA2Selection<FitnessVector<Integer>> SIMPLE_NSGA2_SELECTION_SPEC = ImmutableNSGA2Selection
			.<FitnessVector<Integer>>of(2,
					(a, b) -> a.compareTo(b),
					(m) -> (a, b) -> Double.compare(a.get(m), b.get(m)),
					(a, b, m) -> b.get(m) - a.get(m));

	private final EAConfiguration<FitnessVector<Integer>> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<FitnessVector<Integer>>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(SIMPLE_NSGA2_SELECTION_SPEC)
			.survivorSelectionPolicy(SIMPLE_NSGA2_SELECTION_SPEC)
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> new FitnessVector<>(1, genoType.hashCode() / Integer.MAX_VALUE * 10))
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void ctorNoSelectionPolicy() {
		new NSGA2Selector<>(null);
	}

	@Test(expected = NullPointerException.class)
	public void selectNullEaConfiguration() {

		final NSGA2Selection<Integer> nsga2Selection = mock(NSGA2Selection.class);

		final NSGA2Selector<Integer> nsga2Selector = new NSGA2Selector<>(nsga2Selection);
		nsga2Selector.select(null, 4, new Genotype[] {}, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void selectNullPopulation() {

		final NSGA2Selection<Integer> nsga2Selection = mock(NSGA2Selection.class);

		final NSGA2Selector<Integer> nsga2Selector = new NSGA2Selector<>(nsga2Selection);
		nsga2Selector.select(mock(EAConfiguration.class), 4, null, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void selectNullFitness() {

		final NSGA2Selection<Integer> nsga2Selection = mock(NSGA2Selection.class);

		final NSGA2Selector<Integer> nsga2Selector = new NSGA2Selector<>(nsga2Selection);
		nsga2Selector.select(mock(EAConfiguration.class), 4, new Genotype[] {}, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectNothing() {

		final NSGA2Selection<Integer> nsga2Selection = mock(NSGA2Selection.class);

		final NSGA2Selector<Integer> nsga2Selector = new NSGA2Selector<>(nsga2Selection);
		nsga2Selector.select(mock(EAConfiguration.class), 0, new Genotype[] {}, Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectPopulationFitnessDontMatch() {

		final NSGA2Selection<Integer> nsga2Selection = mock(NSGA2Selection.class);

		final NSGA2Selector<Integer> nsga2Selector = new NSGA2Selector<>(nsga2Selection);
		nsga2Selector.select(mock(EAConfiguration.class), 1, new Genotype[] {}, List.of(4));
	}

	@Test
	public void simple() {
		final Random random = new Random();

		final NSGA2Selector<FitnessVector<Integer>> nsga2Selector = new NSGA2Selector<>(SIMPLE_NSGA2_SELECTION_SPEC);

		final BitChromosomeFactory chromosomeFactory = new BitChromosomeFactory(random);

		final Genotype[] population = {
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))) };
		final List<FitnessVector<Integer>> fitnessScore = List.of(new FitnessVector<>(1, 2),
				new FitnessVector<>(12, 12),
				new FitnessVector<>(-5, -5),
				new FitnessVector<>(-10, -10),
				new FitnessVector<>(2, 1));

		final List<Genotype> selectedTopOne = nsga2Selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 1, population, fitnessScore);
		assertNotNull(selectedTopOne);
		assertEquals(1, selectedTopOne.size());
		assertEquals(population[1], selectedTopOne.get(0));

		final List<Genotype> selectedTopThree = nsga2Selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 3, population, fitnessScore);
		assertNotNull(selectedTopThree);
		assertEquals(3, selectedTopThree.size());
		assertEquals(population[1], selectedTopThree.get(0));

		// Cannot garantee the order
		assertTrue(population[0].equals(selectedTopThree.get(1)) || population[0].equals(selectedTopThree.get(2)));
		assertTrue(population[4].equals(selectedTopThree.get(1)) || population[4].equals(selectedTopThree.get(2)));
	}
}