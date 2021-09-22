package net.bmahe.genetics4j.moo.nsga2.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.factory.BitChromosomeFactory;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.moo.FitnessVector;
import net.bmahe.genetics4j.moo.nsga2.spec.ImmutableTournamentNSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class TournamentNSGA2SelectorTest {
	private final TournamentNSGA2Selection<FitnessVector<Integer>> SIMPLE_TOURNAMENT_NSGA2_SELECTION_SPEC = ImmutableTournamentNSGA2Selection
			.<FitnessVector<Integer>>of(2,
					(m) -> (a, b) -> Double.compare(a.get(m), b.get(m)),
					(a, b, m) -> b.get(m) - a.get(m),
					3);

	private final EAConfiguration<FitnessVector<Integer>> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<FitnessVector<Integer>>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(SIMPLE_TOURNAMENT_NSGA2_SELECTION_SPEC)
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> new FitnessVector<>(1, genoType.hashCode() / Integer.MAX_VALUE * 10))
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test
	public void ctorAllNull() {
		assertThrows(NullPointerException.class, () -> new TournamentNSGA2Selector<>(null, null));
	}

	@Test
	public void ctorNullRandom() {
		assertThrows(NullPointerException.class,
				() -> new TournamentNSGA2Selector<>(null, SIMPLE_TOURNAMENT_NSGA2_SELECTION_SPEC));
	}

	@Test
	public void ctorNullSelectionSpec() {
		assertThrows(NullPointerException.class, () -> new TournamentNSGA2Selector<>(new Random(), null));
	}

	@Test
	public void selectNullEaConfiguration() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		assertThrows(NullPointerException.class,
				() -> nsga2Selector.select(null, 4, Collections.emptyList(), Collections.emptyList()));
	}

	@Test
	public void selectNullPopulation() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		assertThrows(NullPointerException.class,
				() -> nsga2Selector.select(mock(EAConfiguration.class), 4, null, Collections.emptyList()));
	}

	@Test
	public void selectNullFitness() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		assertThrows(NullPointerException.class,
				() -> nsga2Selector.select(mock(EAConfiguration.class), 4, Collections.emptyList(), null));
	}

	@Test
	public void selectNothing() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		assertThrows(IllegalArgumentException.class,
				() -> nsga2Selector
						.select(mock(EAConfiguration.class), 0, Collections.emptyList(), Collections.emptyList()));
	}

	@Test
	public void selectPopulationFitnessDontMatch() {
		final Random random = new Random();
		final TournamentNSGA2Selection<Integer> nsga2Selection = mock(TournamentNSGA2Selection.class);

		final TournamentNSGA2Selector<Integer> nsga2Selector = new TournamentNSGA2Selector<>(random, nsga2Selection);
		assertThrows(IllegalArgumentException.class,
				() -> nsga2Selector.select(mock(EAConfiguration.class), 1, Collections.emptyList(), List.of(4)));
	}

	@Test
	public void simple() {
		final Random random = mock(Random.class, withSettings().withoutAnnotations());
		when(random.nextInt(anyInt())).thenReturn(0, 1, 0, 1, 0, 1, 0, 1, 0);

		final TournamentNSGA2Selector<FitnessVector<Integer>> nsga2Selector = new TournamentNSGA2Selector<>(random,
				SIMPLE_TOURNAMENT_NSGA2_SELECTION_SPEC);

		final BitChromosomeFactory chromosomeFactory = new BitChromosomeFactory(random);

		final List<Genotype> population = List.of(
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))),
				new Genotype(chromosomeFactory.generate(SIMPLE_MAXIMIZING_EA_CONFIGURATION.getChromosomeSpec(0))));
		final List<FitnessVector<Integer>> fitnessScore = List.of(new FitnessVector<>(1, 2),
				new FitnessVector<>(12, 12),
				new FitnessVector<>(-5, -5),
				new FitnessVector<>(-10, -10),
				new FitnessVector<>(2, 1));

		final Population<FitnessVector<Integer>> selectedTopOne = nsga2Selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 3, population, fitnessScore);
		assertNotNull(selectedTopOne);
		assertEquals(3, selectedTopOne.size());

		final Population<FitnessVector<Integer>> selectedTopFromNotSoGreat = nsga2Selector.select(
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				2,
				List.of(population.get(2), population.get(3)),
				List.of(fitnessScore.get(2), fitnessScore.get(3)));
		assertNotNull(selectedTopFromNotSoGreat);
		assertEquals(2, selectedTopFromNotSoGreat.size());
		assertEquals(population.get(2), selectedTopFromNotSoGreat.getGenotype(0));
		assertEquals(population.get(3), selectedTopFromNotSoGreat.getGenotype(1));
	}
}