package net.bmahe.genetics4j.samples;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
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
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.core.util.BitChromosomeUtils;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;

public class FitnessSharingExample {

	final static public Logger logger = LogManager.getLogger(FitnessSharingExample.class);

	final static public String PARAM_DEST_CSV_WITHOUT_SHARING = "w";
	final static public String LONG_PARAM_DEST_CSV_WITHOUT_SHARING = "without-sharing-dest";

	final static public String PARAM_DEST_CSV_WITH_SHARING = "s";
	final static public String LONG_PARAM_DEST_CSV_WITH_SHARING = "with-sharing-dest";

	final static public String PARAM_POPULATION_SIZE = "p";
	final static public String LONG_PARAM_POPULATION_SIZE = "population-size";

	final static public String DEFAULT_DEST_CSV_WITHOUT_SHARING = "withoutFitnessSharing.csv";
	final static public String DEFAULT_DEST_CSV_WITH_SHARING = "withFitnessSharing.csv";
	final static public int DEFAULT_POPULATION_SIZE = 50;

	public static void cliError(final Options options, final String errorMessage) {
		final HelpFormatter formatter = new HelpFormatter();
		logger.error(errorMessage);
		formatter.printHelp(FitnessSharingExample.class.getSimpleName(), options);
		System.exit(-1);
	}

	public static int toPhenotype(final Genotype genotype) {
		Validate.notNull(genotype);

		final BitChromosome bitChromosome = genotype.getChromosome(0, BitChromosome.class);
		final BitSet individualBitSet = bitChromosome.getBitSet();

		final long[] longArray = individualBitSet.toLongArray();

		return longArray.length > 0 ? (int) longArray[0] : 0;
	}

	public static void main(String[] args) throws IOException {

		/**
		 * Parse CLI
		 */

		final CommandLineParser parser = new DefaultParser();

		final Options options = new Options();
		options.addOption(PARAM_DEST_CSV_WITHOUT_SHARING,
				LONG_PARAM_DEST_CSV_WITHOUT_SHARING,
				true,
				"destination csv file for the case without fitness sharing");

		options.addOption(PARAM_DEST_CSV_WITH_SHARING,
				LONG_PARAM_DEST_CSV_WITH_SHARING,
				true,
				"destination csv file for the case with fitness sharing");

		options.addOption(PARAM_POPULATION_SIZE, LONG_PARAM_POPULATION_SIZE, true, "Population size");

		String csvFilenameWithoutSharing = DEFAULT_DEST_CSV_WITHOUT_SHARING;
		String csvFilenameWithSharing = DEFAULT_DEST_CSV_WITH_SHARING;
		int populationSize = DEFAULT_POPULATION_SIZE;
		try {
			final CommandLine line = parser.parse(options, args);

			if (line.hasOption(PARAM_DEST_CSV_WITHOUT_SHARING)) {
				csvFilenameWithoutSharing = line.getOptionValue(PARAM_DEST_CSV_WITHOUT_SHARING);
			}

			if (line.hasOption(PARAM_DEST_CSV_WITH_SHARING)) {
				csvFilenameWithSharing = line.getOptionValue(PARAM_DEST_CSV_WITH_SHARING);
			}

			if (line.hasOption(PARAM_POPULATION_SIZE)) {
				populationSize = Integer.parseInt(line.getOptionValue(PARAM_POPULATION_SIZE));
			}

		} catch (ParseException exp) {
			cliError(options, "Unexpected exception:" + exp.getMessage());
		}

		logger.info("Population size: {}", populationSize);

		logger.info("Evolution without fitness sharing. CSV output located at {}", csvFilenameWithoutSharing);
		FileUtils.forceMkdirParent(new File(csvFilenameWithoutSharing));

		// tag::eaConfigurationBuilder[]
		final Builder<Double> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder.chromosomeSpecs(BitChromosomeSpec.of(7))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(MultiPointCrossover.of(2))
				.mutationPolicies(RandomMutation.of(0.05))
				.fitness((genotype) -> {
					final int x = toPhenotype(genotype);
					return x < 0 || x > 100 ? 0.0 : Math.abs(30 * Math.sin(x / 10));
				})
				.termination(Terminations.ofMaxGeneration(5));
		final EAConfiguration<Double> eaConfiguration = eaConfigurationBuilder.build();
		// end::eaConfigurationBuilder[]

		// tag::eaExecutionContext[]
		final var csvEvolutionListener = CSVEvolutionListener.<Double, Void>of(csvFilenameWithoutSharing,
				List.of(ColumnExtractor.of("generation", es -> es.generation()),
						ColumnExtractor.of("fitness", es -> es.fitness()),
						ColumnExtractor.of("x", es -> toPhenotype(es.individual()))));

		final var logTop5EvolutionListener = EvolutionListeners
				.<Double>ofLogTopN(logger, 5, genotype -> Integer.toString(toPhenotype(genotype)));

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(populationSize)
				.addEvolutionListeners(logTop5EvolutionListener, csvEvolutionListener)
				.build();
		// end::eaExecutionContext[]

		// tag::eaSystem[]
		final EASystem<Double> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<Double> evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: {}", evolutionResult.bestGenotype());
		logger.info("  with fitness: {}", evolutionResult.bestFitness());
		logger.info("  at generation: {}", evolutionResult.generation());
		// end::eaSystem[]

		/////// With Fitness Sharing

		logger.info("Evolution with fitness sharing. CSV output located at {}", csvFilenameWithSharing);
		FileUtils.forceMkdirParent(new File(csvFilenameWithSharing));

		// tag::eaConfigurationWithFS[]
		var eaConfigurationWithFitnessSharing = new EAConfiguration.Builder<Double>().from(eaConfiguration)
				.postEvaluationProcessor(FitnessSharing.ofStandard((i1, i2) -> {

					final BitChromosome bitChromosome1 = i1.getChromosome(0, BitChromosome.class);
					final BitChromosome bitChromosome2 = i2.getChromosome(0, BitChromosome.class);

					return (double) BitChromosomeUtils.hammingDistance(bitChromosome1, bitChromosome2);
				}, 5.0))
				.build();
		// end::eaConfigurationWithFS[]

		// tag::eaExecutionContextWithFS[]
		final var csvEvolutionListenerWithFitnessSharing = new CSVEvolutionListener.Builder<Double, Void>()
				.from(csvEvolutionListener)
				.filename(csvFilenameWithSharing)
				.build();

		final var eaExecutionContextWithFitnessSharing = EAExecutionContext.<Double>builder()
				.from(eaExecutionContext)
				.evolutionListeners(List.of(logTop5EvolutionListener, csvEvolutionListenerWithFitnessSharing))
				.build();
		// end::eaExecutionContextWithFS[]

		// tag::eaSystemWithFS[]
		final EASystem<Double> eaSystemWithFitnessSharing = EASystemFactory.from(eaConfigurationWithFitnessSharing,
				eaExecutionContextWithFitnessSharing);

		final EvolutionResult<Double> evolutionResultWithFitnessSharing = eaSystemWithFitnessSharing.evolve();
		logger.info("Best genotype: {}", evolutionResultWithFitnessSharing.bestGenotype());
		logger.info("  with fitness: {}", evolutionResultWithFitnessSharing.bestFitness());
		logger.info("  at generation: {}", evolutionResultWithFitnessSharing.generation());
		// end::eaSystemWithFS[]
	}
}