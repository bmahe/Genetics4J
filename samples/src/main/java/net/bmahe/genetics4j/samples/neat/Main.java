package net.bmahe.genetics4j.samples.neat;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.core.Fitness;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfiguration.Builder;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
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
import net.bmahe.genetics4j.neat.spec.mutation.NeatConnectionWeight;
import net.bmahe.genetics4j.neat.spec.mutation.SwitchStateMutation;
import net.bmahe.genetics4j.neat.spec.selection.NeatSelection;
import net.bmahe.genetics4j.samples.CLIUtils;

public class Main {
	public static final Logger logger = LogManager.getLogger(Main.class);

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

			final var feedForwardNetwork = new FeedForwardNetwork(inputNodeIndices,
					outputNodeIndices,
					connections,
					Activations.tanhFloat);

			final int outputNodeIndex = outputNodeIndices.iterator()
					.next();
			for (final boolean a : Set.of(false, true)) {
				final float aValue = a ? 1 : 0;
				for (final boolean b : Set.of(false, true)) {
					final float bValue = b ? 1 : 0;
					final float expectedOutput = a ^ b ? 1 : 0;

					// 2 is bias
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

					errorDistance += Math.sqrt((computedOutput - expectedOutput) * (computedOutput - expectedOutput));
				}
			}

			return 4 - errorDistance;
		};
	}

	public static void doStuff() {

		final Builder<Float> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder.chromosomeSpecs(NeatChromosomeSpec.of(3, 1, -5, 5))
				.parentSelectionPolicy(NeatSelection.of(
						(i1, i2) -> NeatUtils.compatibilityDistance(i1.genotype(), i2.genotype(), 0, 1, 1, 1f) < 3.0,
						Tournament.of(3)))
				.combinationPolicy(NeatCombination.build())
				.mutationPolicies(CreepMutation.of(0.30, NormalDistribution.of(0.0, 0.333)),
						RandomMutation.of(0.30),
						NeatConnectionWeight.builder()
								.populationMutationProbability(0.30)
								.build(),
						SwitchStateMutation.of(0.03),
						AddNode.of(0.03),
						AddConnection.of(0.05))
				.fitness(fitnessNEAT(false))
				.optimization(Optimization.MAXIMZE)
				.termination(
						Terminations.<Float>or(Terminations.ofStableFitness(200), Terminations.ofFitnessAtLeast(3.95f)));
		final EAConfiguration<Float> eaConfiguration = eaConfigurationBuilder.build();

		final var eaExecutionContextBuilder = NeatEAExecutionContexts.<Float>standard();
		eaExecutionContextBuilder.populationSize(200)
				.addEvolutionListeners(EvolutionListeners.<Float>ofLogTopN(logger, 3, Comparator.naturalOrder()),
						CSVEvolutionListener.<Float, Void>of("output.csv",
								List.of(ColumnExtractor.of("generation", evolutionStep -> evolutionStep.generation()),
										ColumnExtractor.of("fitness", evolutionStep -> evolutionStep.fitness()),
										ColumnExtractor.of("individual_index", evolutionStep -> evolutionStep.individualIndex()),
										ColumnExtractor.of("individual", evolutionStep -> evolutionStep.individual()))));

		final var eaExecutionContext = eaExecutionContextBuilder.build();

		final EASystem<Float> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);
		final EvolutionResult<Float> evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: {}", evolutionResult.bestGenotype());
		logger.info("\twith fitness: {}", evolutionResult.bestFitness());
		logger.info("\tat generation: {}", evolutionResult.generation());

		final var bestOfTheBest = evolutionResult.population()
				.stream()
				.max(Comparator.comparing(g -> fitnessNEAT(false).compute(g)))
				.get();
		logger.info("Best of the best: {}", bestOfTheBest);
		final var bestFitnessRaw = fitnessNEAT(true).compute(bestOfTheBest);
		logger.info("\tand raw fitness: {}", bestFitnessRaw);
		final var bestOfTheBestChromosome = bestOfTheBest.getChromosome(0, NeatChromosome.class);
		final List<List<Integer>> layersNodes = NeatUtils.partitionLayersNodes(
				bestOfTheBestChromosome.getInputNodeIndices(),
				bestOfTheBestChromosome.getOutputNodeIndices(),
				bestOfTheBestChromosome.getConnections());

		logger.info("\tLayers: {}", layersNodes);
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

		try {
			final CommandLine line = parser.parse(options, args);
			if (line.hasOption("h")) {
				CLIUtils.cliHelpAndExit(logger, Main.class, options, null);
			}

		} catch (ParseException exp) {
			CLIUtils.cliHelpAndExit(logger, Main.class, options, "Unexpected exception:" + exp.getMessage());

			// This piece will never execute
			throw new RuntimeException(); // java doesn't detect the System.exit in cliError and create some issues with
													// potential not initialized final parameters.
		}

		doStuff();

		logger.info("The End.");
	}
}