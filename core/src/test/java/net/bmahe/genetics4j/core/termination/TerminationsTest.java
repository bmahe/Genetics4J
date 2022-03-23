package net.bmahe.genetics4j.core.termination;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;

public class TerminationsTest {

	private final int CHROMOSOME_SIZE = 3;

	private final EAConfiguration<Double> MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Double>()
			.addChromosomeSpecs(ImmutableIntChromosomeSpec.of(CHROMOSOME_SIZE, 0, 10))
			.parentSelectionPolicy(RandomSelection.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	private final EAConfiguration<Double> MINIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Double>()
			.addChromosomeSpecs(ImmutableIntChromosomeSpec.of(CHROMOSOME_SIZE, 0, 10))
			.parentSelectionPolicy(RandomSelection.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.optimization(Optimization.MINIMIZE)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	protected List<Genotype> generatePopulation(final int size) {
		Validate.isTrue(size > 0);

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final var population = new ArrayList<Genotype>();

		for (int i = 0; i < size; i++) {

			final int[] values = randomGenerator.ints(CHROMOSOME_SIZE)
					.toArray();
			final var chromosome = new IntChromosome(CHROMOSOME_SIZE, 0, 10, values);
			final var genotype = new Genotype(chromosome);
			population.add(genotype);
		}

		return population;
	}

	protected List<Double> generateFitness(final int size, final double offset) {
		Validate.isTrue(size > 0);

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		return randomGenerator.doubles(size)
				.map(d -> d + offset)
				.boxed()
				.toList();
	}

	protected List<Double> generateFitness(final int size) {
		return generateFitness(size, 0.0d);
	}

	@Test
	@DisplayName("Max generation must be strictly positive")
	public void ofMaxGenerationInvalid() {
		assertThrows(IllegalArgumentException.class, () -> Terminations.ofMaxGeneration(-10));
		assertThrows(IllegalArgumentException.class, () -> Terminations.ofMaxGeneration(0));
	}

	@Test
	public void ofMaxGeneration() {
		final var maxGenerationValue = 10;

		final int populationSize = 20;
		final var population = generatePopulation(populationSize);
		final var fitness = generateFitness(populationSize);

		final var maxGeneration = Terminations.<Double>ofMaxGeneration(maxGenerationValue);

		for (int generation = 0; generation < maxGenerationValue; generation++) {
			assertFalse(maxGeneration.isDone(MAXIMIZING_EA_CONFIGURATION, generation, population, fitness));
			assertFalse(maxGeneration.isDone(MINIMIZING_EA_CONFIGURATION, generation, population, fitness));
		}

		for (int generation = maxGenerationValue; generation < maxGenerationValue + 10; generation++) {
			assertTrue(maxGeneration.isDone(MAXIMIZING_EA_CONFIGURATION, generation, population, fitness));
			assertTrue(maxGeneration.isDone(MINIMIZING_EA_CONFIGURATION, generation, population, fitness));
		}
	}

	@Test
	@DisplayName("Stable fitness - Max generation must be strictly positive")
	public void ofStableFitnessInvalid() {

		assertThrows(IllegalArgumentException.class, () -> Terminations.ofStableFitness(-10));
		assertThrows(IllegalArgumentException.class, () -> Terminations.ofStableFitness(0));
	}

	@Test
	public void ofStableFitnessNoImprovement() {
		final int stableGenerationCount = 10;

		final int populationSize = 20;
		final var population = generatePopulation(populationSize);
		final var fitness = generateFitness(populationSize);

		final var stableFitnessMaximize = Terminations.<Double>ofStableFitness(stableGenerationCount);
		final var stableFitnessMinimize = Terminations.<Double>ofStableFitness(stableGenerationCount);

		int generation = 0;
		while (generation <= stableGenerationCount) {
			assertFalse(stableFitnessMaximize.isDone(MAXIMIZING_EA_CONFIGURATION, generation, population, fitness));
			assertFalse(stableFitnessMinimize.isDone(MINIMIZING_EA_CONFIGURATION, generation, population, fitness));
			generation++;
		}
		assertTrue(stableFitnessMaximize.isDone(MAXIMIZING_EA_CONFIGURATION, generation, population, fitness));
		assertTrue(stableFitnessMinimize.isDone(MINIMIZING_EA_CONFIGURATION, generation, population, fitness));
	}

	@Test
	public void ofStableFitnessWithImprovement() {
		final int stableGenerationCount = 10;

		final int populationSize = 20;
		final var population = generatePopulation(populationSize);

		final var stableFitnessMaximize = Terminations.<Double>ofStableFitness(stableGenerationCount);
		final var stableFitnessMinimize = Terminations.<Double>ofStableFitness(stableGenerationCount);

		/**
		 * We start by getting better over time
		 */
		int generation = 0;
		var fitnessUp = generateFitness(populationSize, generation);
		var fitnessDown = generateFitness(populationSize, -generation);
		while (generation <= stableGenerationCount * 2) {
			assertFalse(stableFitnessMaximize.isDone(MAXIMIZING_EA_CONFIGURATION, generation, population, fitnessUp));
			assertFalse(stableFitnessMinimize.isDone(MINIMIZING_EA_CONFIGURATION, generation, population, fitnessDown));
			generation++;

			fitnessUp = generateFitness(populationSize, generation);
			fitnessDown = generateFitness(populationSize, -generation);
		}

		/**
		 * We stop improving
		 * 
		 * This means we will stop after stableGenerationCount
		 */
		while (generation <= stableGenerationCount * 3 + 1) {
			assertFalse(stableFitnessMaximize.isDone(MAXIMIZING_EA_CONFIGURATION, generation, population, fitnessUp));
			assertFalse(stableFitnessMinimize.isDone(MINIMIZING_EA_CONFIGURATION, generation, population, fitnessDown));
			generation++;
		}
		assertTrue(stableFitnessMaximize.isDone(MAXIMIZING_EA_CONFIGURATION, generation, population, fitnessUp));
		assertTrue(stableFitnessMinimize.isDone(MINIMIZING_EA_CONFIGURATION, generation, population, fitnessDown));
	}
}