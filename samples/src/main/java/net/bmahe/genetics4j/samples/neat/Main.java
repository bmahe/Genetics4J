package net.bmahe.genetics4j.samples.neat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.core.Fitness;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListener;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfiguration.Builder;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.spec.statistics.distributions.NormalDistribution;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;
import net.bmahe.genetics4j.neat.Activations;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.FeedForwardNetwork;
import net.bmahe.genetics4j.neat.NeatEAExecutionContexts;
import net.bmahe.genetics4j.neat.NeatUtils;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.combination.NeatCombination;
import net.bmahe.genetics4j.neat.spec.mutation.AddConnection;
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;
import net.bmahe.genetics4j.neat.spec.mutation.DeleteConnection;
import net.bmahe.genetics4j.neat.spec.mutation.DeleteNode;
import net.bmahe.genetics4j.neat.spec.mutation.NeatConnectionWeight;
import net.bmahe.genetics4j.neat.spec.mutation.SwitchStateMutation;
import net.bmahe.genetics4j.neat.spec.selection.NeatSelection;
import net.bmahe.genetics4j.neat.util.GraphvizFormatter;
import net.bmahe.genetics4j.samples.CLIUtils;

public class Main {
	public static final Logger logger = LogManager.getLogger(Main.class);

	final static public String PARAM_FILENAME_BEST_NETWORK = "s";
	final static public String LONG_PARAM_FILENAME_BEST_NETWORK = "save";

	final static public String PARAM_DEST_CSV = "d";
	final static public String LONG_PARAM_DEST_CSV = "csv-dest";

	final static public String PARAM_POPULATION_SIZE = "p";
	final static public String LONG_PARAM_POPULATION_SIZE = "population-size";

	final static public String DEFAULT_DEST_CSV = "neat-xor.csv";

	final static public int DEFAULT_POPULATION_SIZE = 500;

	public static Fitness<Float> fitnessNEAT(final boolean print) {
		return (genotype) -> {
			final NeatChromosome neatChromosome = genotype.getChromosome(0, NeatChromosome.class);

			if (print) {
				logger.info("Evaluating chromosome: {}", neatChromosome);
			}

			final Set<Integer> inputNodeIndices = neatChromosome.getInputNodeIndices();
			final Set<Integer> outputNodeIndices = neatChromosome.getOutputNodeIndices();
			final List<Connection> connections = neatChromosome.getConnections();

			if (connections.size() == 0) {
				return 0f;
			}

			float errorDistance = 0;

			// tag::network_definition[]
			final var feedForwardNetwork = new FeedForwardNetwork(inputNodeIndices,
					outputNodeIndices,
					connections,
					Activations.tanhFloat);
			// end::network_definition[]

			// tag::fitness_definition[]
			final int outputNodeIndex = outputNodeIndices.iterator()
					.next();
			for (final boolean a : Set.of(false, true)) {
				final float aValue = a ? 1 : 0;
				for (final boolean b : Set.of(false, true)) {
					final float bValue = b ? 1 : 0;
					final float expectedOutput = a ^ b ? 1 : 0;

					// node 2 is bias
					final var inputValues = Map.of(0, aValue, 1, bValue, 2, 1.0f);
					final Map<Integer, Float> computedOutputValues = feedForwardNetwork.compute(inputValues);

					final float computedOutput = computedOutputValues.get(outputNodeIndex);

					if (print) {
						logger.info("a: {}, b: {} ===> {} ({}) / {}",
								aValue,
								bValue,
								computedOutput,
								outputNodeIndex,
								expectedOutput);
					}

					errorDistance += Math.sqrt((expectedOutput - computedOutput) * (expectedOutput - computedOutput));
				}
			}

			return 4 - errorDistance;
			// end::fitness_definition[]
		};
	}

	public static void executeNEATXor(final Optional<String> bestNetworkFilename, final int populationSize,
			final String csvFilename) throws IOException {

		// tag::neatSelection[]
		final SelectionPolicy neatSelection = NeatSelection.builder()
				.minSpeciesSize(1)
				.perSpeciesKeepRatio(.20f)
				.speciesSelection(Tournament.of(3))
				.speciesPredicate(
						(i1, i2) -> NeatUtils.compatibilityDistance(i1.genotype(), i2.genotype(), 0, 2, 2, 1f) < 4.5)
				.build();
		// end::neatSelection[]

		// tag::neatMutations[]
		final List<MutationPolicy> neatMutations = List.of(
				MultiMutations.of(CreepMutation.of(0.85f, NormalDistribution.of(0.0, 0.333)),
						RandomMutation.of(0.85f),
						NeatConnectionWeight.builder()
								.populationMutationProbability(0.85f)
								.build()),
				MultiMutations.of(SwitchStateMutation.of(
						0.01f), AddNode.of(0.20f), DeleteNode.of(0.20f), AddConnection.of(0.5f), DeleteConnection.of(0.5f)));
		// end::neatMutations[]

		// tag::eaConfiguration[]
		final Builder<Float> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder.chromosomeSpecs(NeatChromosomeSpec.of(3, 1, -5, 5))
				.parentSelectionPolicy(neatSelection)
				.combinationPolicy(NeatCombination.build())
				.mutationPolicies(neatMutations)
				.fitness(fitnessNEAT(false))
				.optimization(Optimization.MAXIMIZE)
				.termination(
						Terminations.<Float>or(Terminations.ofStableFitness(300), Terminations.ofFitnessAtLeast(3.95f)));
		// end::eaConfiguration[]

		final EAConfiguration<Float> eaConfiguration = eaConfigurationBuilder.build();

		final List<EvolutionListener<Float>> evolutionListeners = List.of(
				EvolutionListeners.<Float>ofLogTopN(logger, 3, Comparator.naturalOrder()),
				CSVEvolutionListener.<Float, Void>of(csvFilename,
						List.of(ColumnExtractor.of("generation", evolutionStep -> evolutionStep.generation()),
								ColumnExtractor.of("individual_index", evolutionStep -> evolutionStep.individualIndex()),
								ColumnExtractor.of("fitness", evolutionStep -> evolutionStep.fitness()),
								ColumnExtractor.of("individual_num_connections",
										evolutionStep -> evolutionStep.individual()
												.getChromosome(0, NeatChromosome.class)
												.getConnections()
												.size()),
								ColumnExtractor.of("individual_num_connections_enabled",
										evolutionStep -> evolutionStep.individual()
												.getChromosome(0, NeatChromosome.class)
												.getConnections()
												.stream()
												.filter(connection -> connection.isEnabled())
												.count()),
								ColumnExtractor.of("individual_num_connections_disabled",
										evolutionStep -> evolutionStep.individual()
												.getChromosome(0, NeatChromosome.class)
												.getConnections()
												.stream()
												.filter(connection -> connection.isEnabled() == false)
												.count()),
								ColumnExtractor.of("individual_num_nodes",
										evolutionStep -> evolutionStep.individual()
												.getChromosome(0, NeatChromosome.class)
												.getConnections()
												.stream()
												.flatMapToInt(connection -> IntStream.of(connection.fromNodeIndex(),
														connection.toNodeIndex()))
												.distinct()
												.count()))));

		// tag::eaExecutionContext[]
		final var eaExecutionContextBuilder = NeatEAExecutionContexts.<Float>standard();
		eaExecutionContextBuilder.populationSize(populationSize)
				.evolutionListeners(evolutionListeners);

		final var eaExecutionContext = eaExecutionContextBuilder.build();
		// end::eaExecutionContext[]

		// tag::evolve[]
		final EASystem<Float> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);
		final EvolutionResult<Float> evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: {}", evolutionResult.bestGenotype());
		logger.info("\twith fitness: {}", evolutionResult.bestFitness());
		logger.info("\tat generation: {}", evolutionResult.generation());
		// end::evolve[]

		final var bestOfTheBest = evolutionResult.bestGenotype();
		logger.info("Best of the best: {}", bestOfTheBest);

		/**
		 * We recompute its fitness with debug print activated
		 */
		final var bestFitnessRaw = fitnessNEAT(true).compute(bestOfTheBest);
		logger.info("\tand raw fitness: {} / {}", bestFitnessRaw, evolutionResult.bestFitness());

		final var bestOfTheBestChromosome = bestOfTheBest.getChromosome(0, NeatChromosome.class);
		final List<List<Integer>> layersNodes = NeatUtils.partitionLayersNodes(
				bestOfTheBestChromosome.getInputNodeIndices(),
				bestOfTheBestChromosome.getOutputNodeIndices(),
				bestOfTheBestChromosome.getConnections());

		logger.info("\tLayers: {}", layersNodes);

		final GraphvizFormatter graphvizFormatter = new GraphvizFormatter();
		final String bestNetworkStr = graphvizFormatter.format(bestOfTheBestChromosome,
				Map.of(0, "A", 1, "B", 2, "bias=1", 3, "A xor B"));
		logger.info("{}", bestNetworkStr);

		if (bestNetworkFilename.isPresent()) {
			FileUtils.forceMkdirParent(new File(bestNetworkFilename.get()));

			try (var fileWriter = new FileWriter(bestNetworkFilename.get(), StandardCharsets.UTF_8);
					var bufferedWriter = new BufferedWriter(fileWriter)) {
				bufferedWriter.write(bestNetworkStr);
			}
		}

		if (evolutionResult.bestFitness() <= 3.80f) {
			throw new IllegalStateException("Evolution unsuccesful. Fitness too low");
		}
	}

	public static void main(final String[] args) throws IOException {
		logger.info("Starting");

		/**
		 * Parse CLI
		 */
		logger.info("Parsing command line arguments");
		final CommandLineParser parser = new DefaultParser();

		final Options options = new Options();
		options.addOption("h", "help", false, "print help");
		options.addOption(PARAM_FILENAME_BEST_NETWORK, LONG_PARAM_FILENAME_BEST_NETWORK, true, "save the best network");
		options.addOption(PARAM_DEST_CSV, LONG_PARAM_DEST_CSV, true, "destination csv file");
		options.addOption(PARAM_POPULATION_SIZE, LONG_PARAM_POPULATION_SIZE, true, "Population size");

		final Optional<String> bestNetworkFilename;
		String csvFilename = DEFAULT_DEST_CSV;
		int populationSize = DEFAULT_POPULATION_SIZE;
		try {
			final CommandLine line = parser.parse(options, args);
			if (line.hasOption("h")) {
				CLIUtils.cliHelpAndExit(logger, Main.class, options, null);
			}

			if (line.hasOption("s")) {
				bestNetworkFilename = Optional.of(line.getOptionValue("s"));
			} else {
				bestNetworkFilename = Optional.empty();
			}

			if (line.hasOption(PARAM_DEST_CSV)) {
				csvFilename = line.getOptionValue(PARAM_DEST_CSV);
			}

			if (line.hasOption(PARAM_POPULATION_SIZE)) {
				populationSize = Integer.parseInt(line.getOptionValue(PARAM_POPULATION_SIZE));
			}

		} catch (ParseException exp) {
			CLIUtils.cliHelpAndExit(logger, Main.class, options, "Unexpected exception:" + exp.getMessage());

			// This piece will never execute
			throw new RuntimeException(); // java doesn't detect the System.exit in cliError and create some issues with
													// potential not initialized final parameters.
		}

		logger.info("Population size: {}", populationSize);

		logger.info("CSV output located at {}", csvFilename);
		FileUtils.forceMkdirParent(new File(csvFilename));

		executeNEATXor(bestNetworkFilename, populationSize, csvFilename);

		logger.info("The End.");
	}
}