package net.bmahe.genetics4j.moo.nsga2.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.factory.BitChromosomeFactory;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.moo.FitnessVector;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableTournamentNSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class TournamentNSGA2SelectorTest {
	private final TournamentNSGA2Selection<FitnessVector<Integer>> SIMPLE_TOURNAMENT_NSGA2_SELECTION_SPEC = ImmutableTournamentNSGA2Selection
			.<FitnessVector<Integer>>of(2,
					(a, b) -> a.compareTo(b),
					(m) -> (a, b) -> Double.compare(a.get(m), b.get(m)),
					(a, b, m) -> b.get(m) - a.get(m),
					3);

	private final GenotypeSpec<FitnessVector<Integer>> SIMPLE_MAXIMIZING_GENOTYPE_SPEC = new GenotypeSpec.Builder<FitnessVector<Integer>>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(SIMPLE_TOURNAMENT_NSGA2_SELECTION_SPEC)
			.survivorSelectionPolicy(SIMPLE_TOURNAMENT_NSGA2_SELECTION_SPEC)
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> new FitnessVector<>(1, genoType.hashCode() / Integer.MAX_VALUE * 10))
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void ctorAllNull() {
		new TournamentNSGA2Selector<>(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void ctorNullRandom() {
		new TournamentNSGA2Selector<>(null, SIMPLE_TOURNAMENT_NSGA2_SELECTION_SPEC);
	}

	@Test(expected = NullPointerException.class)
	public void ctorNullSelectionSpec() {
		new TournamentNSGA2Selector<>(new Random(), null);
	}

	@Test(expected = NullPointerException.class)
	public void selectNullGenotypeSpec() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		nsga2Selector.select(null, 4, new Genotype[] {}, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void selectNullPopulation() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		nsga2Selector.select(mock(GenotypeSpec.class), 4, null, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void selectNullFitness() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		nsga2Selector.select(mock(GenotypeSpec.class), 4, new Genotype[] {}, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectNothing() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		nsga2Selector.select(mock(GenotypeSpec.class), 0, new Genotype[] {}, Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectPopulationFitnessDontMatch() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		nsga2Selector.select(mock(GenotypeSpec.class), 1, new Genotype[] {}, List.of(4));
	}

	@Test
	public void simple() {
		final Random random = mock(Random.class);
		when(random.nextInt(anyInt())).thenReturn(0, 1, 0, 1, 0, 1, 0, 1, 0);

		final TournamentNSGA2Selector<FitnessVector<Integer>> nsga2Selector = new TournamentNSGA2Selector<>(random,
				SIMPLE_TOURNAMENT_NSGA2_SELECTION_SPEC);

		final BitChromosomeFactory chromosomeFactory = new BitChromosomeFactory(random);

		final Genotype[] population = {
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_GENOTYPE_SPEC.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_GENOTYPE_SPEC.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_GENOTYPE_SPEC.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_GENOTYPE_SPEC.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_GENOTYPE_SPEC.getChromosomeSpec(0))) };
		final List<FitnessVector<Integer>> fitnessScore = List.of(new FitnessVector<>(1, 2),
				new FitnessVector<>(12, 12),
				new FitnessVector<>(-5, -5),
				new FitnessVector<>(-10, -10),
				new FitnessVector<>(2, 1));

		final List<Genotype> selectedTopOne = nsga2Selector
				.select(SIMPLE_MAXIMIZING_GENOTYPE_SPEC, 3, population, fitnessScore);
		assertNotNull(selectedTopOne);
		assertEquals(3, selectedTopOne.size());

		final List<Genotype> selectedTopFromNotSoGreat = nsga2Selector.select(SIMPLE_MAXIMIZING_GENOTYPE_SPEC,
				2,
				new Genotype[] { population[2], population[3] },
				List.of(fitnessScore.get(2), fitnessScore.get(3)));
		assertNotNull(selectedTopFromNotSoGreat);
		assertEquals(2, selectedTopFromNotSoGreat.size());
		assertEquals(population[2], selectedTopFromNotSoGreat.get(0));
		assertEquals(population[3], selectedTopFromNotSoGreat.get(1));
	}
}