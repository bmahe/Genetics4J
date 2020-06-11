package net.bmahe.genetics4j.samples;

import java.util.Comparator;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfiguration.Builder;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.PartialMutation;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheel;

public class SimpleTest {
	final static public Logger logger = LogManager.getLogger(SimpleTest.class);

	public static void main(String[] args) {

		final Random random = new Random();

		final Builder<Double> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder.chromosomeSpecs(IntChromosomeSpec.of(10, 0, 10))
				.parentSelectionPolicy(RouletteWheel.build())
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
		final EAConfiguration<Double> eaConfiguration = eaConfigurationBuilder.build();

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.random(random)
				.addEvolutionListeners(EvolutionListeners.ofLogTopN(logger, 3, Comparator.<Double>reverseOrder()))
				.build();

		final EASystem<Double> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<Double> evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: " + evolutionResult.bestGenotype());

		System.exit(0);
	}
}