package net.bmahe.genetics4j.samples;

import static net.bmahe.genetics4j.core.termination.Terminations.or;

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
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Terminations;

public class QuickStart {

	public static final Logger logger = LogManager.getLogger(QuickStart.class);

	public static void main(String[] args) {

		// tag::quickstart_variables[]
		final int numEntries = 10; // <1>
		final int minValue = 0; // <2>
		final int maxValue = 20; // <3>
		// end::quickstart_variables[]

		// tag::quickstart_genotype_spec[]
		final Builder<Integer> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder.chromosomeSpecs(IntChromosomeSpec.of(numEntries, minValue, maxValue))
				.parentSelectionPolicy(Tournament.of(5))
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
				.termination(or(Terminations.ofFitnessAtLeast(numEntries), Terminations.ofMaxGeneration(50)));
		final EAConfiguration<Integer> eaConfiguration = eaConfigurationBuilder.build();
		// end::quickstart_genotype_spec[]

		// tag::quickstart_genetic_system_descriptor[]
		final EAExecutionContext<Integer> eaExecutionContext = EAExecutionContexts.<Integer>forScalarFitness()
				.populationSize(100)
				.addEvolutionListeners(EvolutionListeners.ofLogTopN(logger, 3))
				.build();
		// end::quickstart_genetic_system_descriptor[]

		// tag::quickstart_genetic_system[]
		final EASystem<Integer> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);
		// end::quickstart_genetic_system[]

		// tag::quickstart_evolve[]
		final EvolutionResult<Integer> evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: {}", evolutionResult.bestGenotype());
		logger.info("  with fitness: {}", evolutionResult.bestFitness());
		logger.info("  at generation: {}", evolutionResult.generation());
		// end::quickstart_evolve[]

		System.exit(0);
	}
}