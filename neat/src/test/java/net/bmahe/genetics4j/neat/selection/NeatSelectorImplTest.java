package net.bmahe.genetics4j.neat.selection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.selection.TournamentSelector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfiguration.Builder;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.neat.Species;
import net.bmahe.genetics4j.neat.SpeciesIdGenerator;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.combination.NeatCombination;
import net.bmahe.genetics4j.neat.spec.selection.NeatSelection;

public class NeatSelectorImplTest {
	public static final Logger logger = LogManager.getLogger(NeatSelectorImplTest.class);

	@Test
	public void constructor() {

		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		final NeatSelection<Integer> neatSelection = NeatSelection.ofDefault();
		final SpeciesIdGenerator speciesIdGenerator = new SpeciesIdGenerator();
		final Selector<Integer> mockSelector = mock(Selector.class);

		assertThrows(NullPointerException.class, () -> new NeatSelectorImpl<>(null, null, null, null));
		assertThrows(NullPointerException.class, () -> new NeatSelectorImpl<>(mockRandomGenerator, null, null, null));
		assertThrows(NullPointerException.class,
				() -> new NeatSelectorImpl<>(mockRandomGenerator, neatSelection, null, null));
		assertThrows(NullPointerException.class,
				() -> new NeatSelectorImpl<>(mockRandomGenerator, neatSelection, speciesIdGenerator, null));
		assertDoesNotThrow(
				() -> new NeatSelectorImpl<>(mockRandomGenerator, neatSelection, speciesIdGenerator, mockSelector));
	}

	private Genotype createGenotype(final int i) {
		return new Genotype(new IntChromosome(1, -10, 10, new int[] { i }));
	}

	public void trimSpecies() {

		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		final SpeciesIdGenerator speciesIdGenerator = new SpeciesIdGenerator();
		final Selector<Integer> mockSelector = mock(Selector.class);
		final Comparator<Individual<Integer>> individualComparator = Comparator.comparing(Individual::fitness);

		final List<Individual<Integer>> individuals = List.of(Individual.of(createGenotype(0), 0),
				Individual.of(createGenotype(1), 1),
				Individual.of(createGenotype(2), 2),
				Individual.of(createGenotype(3), 3));

		final Species<Integer> species = new Species<>(0, List.of());
		species.addAllMembers(individuals);

		final NeatSelection<Integer> neatSelection = NeatSelection.<Integer>builder()
				.from(NeatSelection.ofDefault())
				.build();

		final NeatSelectorImpl<Integer> neatSelectorImpl = new NeatSelectorImpl<>(mockRandomGenerator,
				neatSelection,
				speciesIdGenerator,
				mockSelector);

		final Species<Integer> trimSpeciesZero = neatSelectorImpl.trimSpecies(species, individualComparator, 0, 0);
		assertNotNull(trimSpeciesZero);
		assertEquals(0, trimSpeciesZero.getNumMembers());

		final Species<Integer> trimSpeciesOneMin = neatSelectorImpl.trimSpecies(species, individualComparator, 1, 0);
		assertNotNull(trimSpeciesOneMin);
		assertEquals(1, trimSpeciesOneMin.getNumMembers());

		final Species<Integer> trimSpeciesHalf = neatSelectorImpl.trimSpecies(species, individualComparator, 1, 0.5f);
		assertNotNull(trimSpeciesHalf);
		assertEquals(2, trimSpeciesHalf.getNumMembers());

		final Species<Integer> trimSpeciesAll = neatSelectorImpl.trimSpecies(species, individualComparator, 1, 1.0f);
		assertNotNull(trimSpeciesAll);
		assertEquals(4, trimSpeciesAll.getNumMembers());
	}

	@Test
	public void eliminateLowestPerformers() {
		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		final SpeciesIdGenerator speciesIdGenerator = new SpeciesIdGenerator();
		final Selector<Integer> mockSelector = mock(Selector.class);
		final Comparator<Individual<Integer>> individualComparator = Comparator.comparing(Individual::fitness);

		final List<Individual<Integer>> individuals = List.of(Individual.of(createGenotype(0), 0),
				Individual.of(createGenotype(1), 1),
				Individual.of(createGenotype(2), 2),
				Individual.of(createGenotype(3), 3));

		final Species<Integer> species = new Species<>(0, List.of());
		species.addAllMembers(individuals);

		final Species<Integer> emptySpecies = new Species<>(1, List.of());

		final NeatSelection<Integer> neatSelection = NeatSelection.<Integer>builder()
				.from(NeatSelection.ofDefault())
				.minSpeciesSize(1)
				.perSpeciesKeepRatio(0.25f)
				.build();

		final NeatSelectorImpl<Integer> neatSelectorImpl = new NeatSelectorImpl<>(mockRandomGenerator,
				neatSelection,
				speciesIdGenerator,
				mockSelector);

		final AbstractEAConfiguration<Integer> mockAEAConfiguration = mock(AbstractEAConfiguration.class);
		when(mockAEAConfiguration.optimization()).thenReturn(Optimization.MAXIMIZE)
				.thenReturn(Optimization.MINIMIZE);

		final List<Species<Integer>> maximizedPerformers = neatSelectorImpl
				.eliminateLowestPerformers(mockAEAConfiguration, List.of(species, emptySpecies));
		assertNotNull(maximizedPerformers);
		assertEquals(1, maximizedPerformers.size());
		final Species<Integer> maximizedSpecies = maximizedPerformers.get(0);
		assertEquals(1, maximizedSpecies.getNumMembers());
		assertEquals(individuals.get(3),
				maximizedSpecies.getMembers()
						.get(0));

		final List<Species<Integer>> minimizedPerformers = neatSelectorImpl
				.eliminateLowestPerformers(mockAEAConfiguration, List.of(species, emptySpecies));
		assertNotNull(minimizedPerformers);
		assertEquals(1, minimizedPerformers.size());
		final Species<Integer> minimizedSpecies = minimizedPerformers.get(0);
		assertEquals(1, minimizedSpecies.getNumMembers());
		assertEquals(individuals.get(0),
				minimizedSpecies.getMembers()
						.get(0));
	}

	@Test
	public void select() {

		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		final SpeciesIdGenerator speciesIdGenerator = new SpeciesIdGenerator();
		final Selector<Integer> speciesSelector = new TournamentSelector<>(Tournament.of(3),
				RandomGenerator.getDefault());
		final Comparator<Individual<Integer>> individualComparator = Comparator.comparing(Individual::fitness);

		/**
		 * Species are based on the hundreds in the fitness
		 * <br/>
		 * ex: all the 00X individuals are in the same species
		 */
		final List<Individual<Integer>> individuals = List.of(Individual.of(createGenotype(0), 0),
				Individual.of(createGenotype(1), 1),
				Individual.of(createGenotype(2), 2),
				Individual.of(createGenotype(3), 3),
				Individual.of(createGenotype(0), 100),
				Individual.of(createGenotype(1), 101),
				Individual.of(createGenotype(2), 102),
				Individual.of(createGenotype(3), 103),
				Individual.of(createGenotype(0), 200),
				Individual.of(createGenotype(1), 201));

		final Population<Integer> population = Population.of(individuals);

		final NeatSelection<Integer> neatSelection = NeatSelection.<Integer>builder()
				.from(NeatSelection.ofDefault())
				.minSpeciesSize(1)
				.perSpeciesKeepRatio(0.25f)
				.speciesPredicate((i1, i2) -> (i1.fitness() / 100) == (i2.fitness() / 100))
				.build();

		final AbstractEAConfiguration<Integer> mockAEAConfiguration = mock(AbstractEAConfiguration.class);
		when(mockAEAConfiguration.optimization()).thenReturn(Optimization.MAXIMIZE)
				.thenReturn(Optimization.MINIMIZE);

		final NeatSelectorImpl<Integer> neatSelectorImpl = new NeatSelectorImpl<>(mockRandomGenerator,
				neatSelection,
				speciesIdGenerator,
				speciesSelector);

		final Population<Integer> selected = neatSelectorImpl
				.select(mockAEAConfiguration, 4, population.getAllGenotypes(), population.getAllFitnesses());

		logger.info("Selected: {}", selected);
		assertNotNull(selected);
		assertEquals(4, selected.size());
		assertEquals(201, selected.getFitness(0));
		assertEquals(population.getGenotype(9), selected.getGenotype(0));

		assertEquals(201, selected.getFitness(1));
		assertEquals(population.getGenotype(9), selected.getGenotype(1));

		assertEquals(103, selected.getFitness(2));
		assertEquals(population.getGenotype(7), selected.getGenotype(2));

		assertEquals(201, selected.getFitness(3));
		assertEquals(population.getGenotype(9), selected.getGenotype(3));
	}

	@Test
	public void simple() {

		final var randomGenerator = RandomGenerator.getDefault();
		final var neatSelection = NeatSelection.<Integer>of((i1, i2) -> i1.fitness() < i2.fitness(), Tournament.of(1));
		final var speciesIdGenerator = new SpeciesIdGenerator();
		final Selector<Integer> selector = new TournamentSelector<>(Tournament.of(20), randomGenerator);

		final NeatSelectorImpl<Integer> neatSelectorImpl = new NeatSelectorImpl<>(randomGenerator,
				neatSelection,
				speciesIdGenerator,
				selector);

		final Builder<Integer> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder.chromosomeSpecs(NeatChromosomeSpec.of(3, 2, -1.0f, 1.0f))
				.parentSelectionPolicy(neatSelection)
				.combinationPolicy(NeatCombination.build())
				.mutationPolicies(RandomMutation.of(0.30))
				.fitness(genotype -> 0)
				.optimization(Optimization.MAXIMIZE)
				.termination(Terminations.ofMaxGeneration(1));
		final EAConfiguration<Integer> eaConfiguration = eaConfigurationBuilder.build();

		/**
		 * Validate that null population throws an exception
		 */
		assertThrows(NullPointerException.class, () -> neatSelectorImpl.eliminateLowestPerformers(eaConfiguration, null));

		/**
		 * Validate that there is nothing to eliminate from an empty population of
		 * species
		 */
		final List<Species<Integer>> emptyBestPerformers = neatSelectorImpl.eliminateLowestPerformers(eaConfiguration,
				List.of());
		assertNotNull(emptyBestPerformers);
		assertEquals(0, emptyBestPerformers.size());

		final List<Species<Integer>> species = List
				.of(new Species<>(0, List.of()), new Species<>(1, List.of()), new Species<>(1, List.of()));

		final NeatChromosome emptyNeatChromosome = new NeatChromosome(3, 2, -1.0f, 1.0f, List.of());
		final Genotype genotype = new Genotype(emptyNeatChromosome);

		/**
		 * Create a species with less than the minimum guaranteed number of survivors.
		 * We will expect no individual being removed
		 */
		species.get(0)
				.addAllMembers(List.of(Individual.of(genotype, 0), Individual.of(genotype, 1)));
		assertTrue(species.get(0)
				.getNumMembers() <= neatSelection.minSpeciesSize());

		/**
		 * We are above the minimum number of individual and expect 10% to be removed
		 */
		final Individual<Integer> lowestPerformer = Individual.of(genotype, 0);
		species.get(1)
				.addAllMembers(List.of(lowestPerformer,
						Individual.of(genotype, lowestPerformer.fitness() + 1),
						Individual.of(genotype, lowestPerformer.fitness() + 2),
						Individual.of(genotype, lowestPerformer.fitness() + 3),
						Individual.of(genotype, lowestPerformer.fitness() + 4),
						Individual.of(genotype, lowestPerformer.fitness() + 5),
						Individual.of(genotype, lowestPerformer.fitness() + 6),
						Individual.of(genotype, lowestPerformer.fitness() + 7),
						Individual.of(genotype, lowestPerformer.fitness() + 8),
						Individual.of(genotype, lowestPerformer.fitness() + 9)));

		final List<Species<Integer>> bestPerformers = neatSelectorImpl.eliminateLowestPerformers(eaConfiguration,
				species);
		assertNotNull(bestPerformers);
		assertEquals(2, bestPerformers.size());

		/**
		 * No removal should have occurred
		 */
		assertEquals(species.get(0)
				.getNumMembers(),
				bestPerformers.get(0)
						.getNumMembers());

		assertEquals(neatSelection.perSpeciesKeepRatio() * species.get(1)
				.getNumMembers(),
				bestPerformers.get(1)
						.getNumMembers());

		// Validate the lowest performer did get removed
		assertTrue(bestPerformers.get(1)
				.getMembers()
				.stream()
				.allMatch(i -> i.equals(lowestPerformer) == false));

	}
}