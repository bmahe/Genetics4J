package net.bmahe.genetics4j.neat.selection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.selection.TournamentSelector;
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

public class NeatSelectionImplTest {
	public static final Logger logger = LogManager.getLogger(NeatSelectionImplTest.class);

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
				.optimization(Optimization.MAXIMZE)
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