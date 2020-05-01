package net.bmahe.genetics4j.samples;

import java.util.Comparator;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.GeneticSystem;
import net.bmahe.genetics4j.core.GeneticSystemFactory;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptors;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.GenotypeSpec.Builder;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.PartialMutation;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class SimpleTest {
	final static public Logger logger = LogManager.getLogger(SimpleTest.class);

	public static void main(String[] args) {

		final Random random = new Random();

		final Builder<Double> genotypeSpecBuilder = new GenotypeSpec.Builder<>();
		genotypeSpecBuilder.chromosomeSpecs(IntChromosomeSpec.of(10, 0, 10))
				.parentSelectionPolicy(RouletteWheelSelection.build())
				.survivorSelectionPolicy(TournamentSelection.build(30))
				.offspringRatio(0.7d)
				.combinationPolicy(MultiPointCrossover.of(2))
				.mutationPolicies(RandomMutation.of(0.15), PartialMutation.of(0, RandomMutation.of(0.05)))
				.optimization(Optimization.MINIMIZE)
				.fitness((genoType) -> {
					final IntChromosome intChromosome = genoType.getChromosome(0, IntChromosome.class);
					double denominator = 0.0;
					for (int i = 0; i < intChromosome.getNumAlleles(); i++) {
						denominator += Math.abs(i - intChromosome.getAllele(i));
					}

					return denominator;
				})
				.termination((generation, population, fitness) -> {
					return fitness.stream().min(Comparator.naturalOrder()).orElseThrow() < 0.0001;
				});
		final GenotypeSpec<Double> genotypeSpec = genotypeSpecBuilder.build();

		final GeneticSystemDescriptor<Double> geneticSystemDescriptor = GeneticSystemDescriptors
				.<Double>forScalarFitness()
				.populationSize(100)
				.random(random)
				.build();

		final GeneticSystem<Double> geneticSystem = GeneticSystemFactory.from(genotypeSpec, geneticSystemDescriptor);

		final EvolutionResult<Double> evolutionResult = geneticSystem.evolve();
		logger.info("Best genotype: " + evolutionResult.bestGenotype());

		System.exit(0);
	}
}