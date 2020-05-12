package net.bmahe.genetics4j.samples;

import java.util.BitSet;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.postevaluationprocess.FitnessSharing;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfiguration.Builder;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.core.util.BitChromosomeUtils;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;

public class FitnessSharingExample {

	final static public Logger logger = LogManager.getLogger(FitnessSharingExample.class);

	public static int toPhenotype(final Genotype genotype) {
		Validate.notNull(genotype);

		final BitChromosome bitChromosome = genotype.getChromosome(0, BitChromosome.class);
		final BitSet individualBitSet = bitChromosome.getBitSet();

		final long[] longArray = individualBitSet.toLongArray();

		return longArray.length > 0 ? (int) longArray[0] : 0;
	}

	public static void main(String[] args) {
		final Builder<Double> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder.chromosomeSpecs(BitChromosomeSpec.of(7))
				.parentSelectionPolicy(TournamentSelection.of(2))
				.combinationPolicy(MultiPointCrossover.of(2))
				.mutationPolicies(RandomMutation.of(0.05))
				.fitness((genotype) -> {
					final int x = toPhenotype(genotype);
					return x < 0 || x > 100 ? 0.0 : Math.abs(30 * Math.sin(x / 10));
				})
				.postEvaluationProcessor(FitnessSharing.ofStandard((i1, i2) -> {

					final BitChromosome bitChromosome1 = i1.getChromosome(0, BitChromosome.class);
					final BitChromosome bitChromosome2 = i2.getChromosome(0, BitChromosome.class);

					return (double) BitChromosomeUtils.hammingDistance(bitChromosome1, bitChromosome2);
				}, 7.0))
				.termination(Terminations.ofMaxGeneration(100));
		final EAConfiguration<Double> eaConfiguration = eaConfigurationBuilder.build();

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(30)
				.addEvolutionListeners(
						EvolutionListeners.ofLogTopN(logger, 10, genotype -> Integer.toString(toPhenotype(genotype))),
						CSVEvolutionListener.<Double, Void>of("fitnessSharing.csv",
								List.of(ColumnExtractor.of("generation", es -> es.generation()),
										ColumnExtractor.of("fitness", es -> es.fitness()),
										ColumnExtractor.of("x", es -> toPhenotype(es.individual())))))
				.build();

		final EASystem<Double> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<Double> evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: " + evolutionResult.bestGenotype());
		logger.info("  with fitness: {}", evolutionResult.bestFitness());
		logger.info("  at generation: {}", evolutionResult.generation());

		System.exit(0);
	}
}