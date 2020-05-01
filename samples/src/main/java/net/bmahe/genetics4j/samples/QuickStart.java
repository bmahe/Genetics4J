package net.bmahe.genetics4j.samples;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.GeneticSystem;
import net.bmahe.genetics4j.core.GeneticSystemFactory;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptors;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.GenotypeSpec.Builder;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;
import net.bmahe.genetics4j.core.termination.Terminations;

public class QuickStart {

	final static public Logger logger = LogManager.getLogger(QuickStart.class);

	public static void main(String[] args) {

		// tag::quickstart_variables[]
		final int numEntries = 10; // <1>
		final int minValue = 0; // <2>
		final int maxValue = 20; // <3>
		// end::quickstart_variables[]

		// tag::quickstart_genotype_spec[]
		final Builder<Integer> genotypeSpecBuilder = new GenotypeSpec.Builder<>();
		genotypeSpecBuilder.chromosomeSpecs(IntChromosomeSpec.of(numEntries, minValue, maxValue))
				.parentSelectionPolicy(TournamentSelection.build(5))
				.survivorSelectionPolicy(RouletteWheelSelection.build())
				.offspringRatio(0.9d)
				.combinationPolicy(MultiPointCrossover.of(2))
				.mutationPolicies(RandomMutation.of(0.15))
				.fitness((genoType) -> {
					final IntChromosome intChromosome = genoType.getChromosome(0, IntChromosome.class);
					int correctCount = 0;
					for (int i = 0; i < intChromosome.getNumAlleles(); i++) {
						if (i == intChromosome.getAllele(i)) {
							correctCount++;
						}
					}

					return correctCount;
				})
				.termination(
						Terminations.or(Terminations.ofFitnessAtLeast(numEntries), Terminations.ofMaxGeneration(50)));
		final GenotypeSpec<Integer> genotypeSpec = genotypeSpecBuilder.build();
		// end::quickstart_genotype_spec[]

		// tag::quickstart_genetic_system_descriptor[]
		final GeneticSystemDescriptor<Integer> geneticSystemDescriptor = GeneticSystemDescriptors
				.<Integer>forScalarFitness()
				.populationSize(100)
				.addEvolutionListeners(EvolutionListeners.ofLogTopN(logger, 3))
				.build();
		// end::quickstart_genetic_system_descriptor[]

		// tag::quickstart_genetic_system[]
		final GeneticSystem<Integer> geneticSystem = GeneticSystemFactory.from(genotypeSpec, geneticSystemDescriptor);
		// end::quickstart_genetic_system[]

		// tag::quickstart_evolve[]
		final EvolutionResult<Integer> evolutionResult = geneticSystem.evolve();
		logger.info("Best genotype: " + evolutionResult.bestGenotype());
		logger.info("  with fitness: {}", evolutionResult.bestFitness());
		logger.info("  at generation: {}", evolutionResult.generation());
		// end::quickstart_evolve[]

		System.exit(0);
	}
}