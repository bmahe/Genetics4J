package net.bmahe.genetics4j.samples.symbolicregression;

import static net.bmahe.genetics4j.core.termination.Terminations.ofFitnessAtMost;
import static net.bmahe.genetics4j.core.termination.Terminations.ofMaxGeneration;
import static net.bmahe.genetics4j.core.termination.Terminations.or;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.core.Fitness;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.replacement.Elitism;
import net.bmahe.genetics4j.core.spec.selection.DoubleTournament;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.math.SimplificationRules;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.spec.GPEAExecutionContexts;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.combination.ProgramRandomCombine;
import net.bmahe.genetics4j.gp.spec.mutation.NodeReplacement;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramApplyRules;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomMutate;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;
import net.bmahe.genetics4j.gp.utils.ProgramUtils;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;
import net.bmahe.genetics4j.moo.FitnessVector;

public class SymbolicRegressionWithDoubleTournament {
	final static public Logger logger = LogManager.getLogger(SymbolicRegressionWithDoubleTournament.class);

	final static public String PARAM_DEST_CSV = "d";
	final static public String LONG_PARAM_DEST_CSV = "csv-dest";

	final static public String PARAM_POPULATION_SIZE = "p";
	final static public String LONG_PARAM_POPULATION_SIZE = "population-size";

	final static public String DEFAULT_DEST_CSV = SymbolicRegressionWithDoubleTournament.class.getSimpleName() + ".csv";

	final static public int DEFAULT_POPULATION_SIZE = 500;

	public static void cliError(final Options options, final String errorMessage) {
		final HelpFormatter formatter = new HelpFormatter();
		logger.error(errorMessage);
		formatter.printHelp(SymbolicRegressionWithDoubleTournament.class.getSimpleName(), options);
		System.exit(-1);
	}

	@SuppressWarnings("unchecked")
	public void run(String csvFilename, int populationSize) {
		Validate.isTrue(StringUtils.isNotBlank(csvFilename));
		Validate.isTrue(populationSize > 0);

		final Random random = new Random();

		final Program program = SymbolicRegressionUtils.buildProgram(random);

		// tag::compute_fitness[]
		final Fitness<Double> computeFitness = (genoType) -> {
			final TreeChromosome<Operation<?>> chromosome = (TreeChromosome<Operation<?>>) genoType.getChromosome(0);
			final Double[][] inputs = new Double[100][1];
			for (int i = 0; i < 100; i++) {
				inputs[i][0] = (i - 50) * 1.2;
			}

			double mse = 0;
			for (final Double[] input : inputs) {

				final double x = input[0];
				final double expected = SymbolicRegressionUtils.evaluate(x);
				final Object result = ProgramUtils.execute(chromosome, input);

				if (Double.isFinite(expected)) {
					final Double resultDouble = (Double) result;
					mse += Double.isFinite(resultDouble) ? (expected - resultDouble) * (expected - resultDouble)
							: 1_000_000_000;
				}
			}
			return Double.isFinite(mse) ? mse / 100.0d : Double.MAX_VALUE;
		};
		// end::compute_fitness[]

		// tag::double_tournament[]
		final Comparator<Individual<Double>> parsimonyComparator = (a, b) -> {
			final TreeChromosome<Operation<?>> treeChromosomeA = a.genotype().getChromosome(0, TreeChromosome.class);
			final TreeChromosome<Operation<?>> treeChromosomeB = b.genotype().getChromosome(0, TreeChromosome.class);

			return Integer.compare(treeChromosomeA.getSize(), treeChromosomeB.getSize());
		};

		final DoubleTournament<Double> doubleTournament = DoubleTournament
				.of(Tournament.of(3), parsimonyComparator, 1.1d);
		// end::double_tournament[]

		// tag::ea_config[]
		final var eaConfigurationBuilder = new EAConfiguration.Builder<Double>();
		eaConfigurationBuilder.chromosomeSpecs(ProgramTreeChromosomeSpec.of(program)) // <1>
				.parentSelectionPolicy(doubleTournament)
				.replacementStrategy(Elitism.builder() // <2>
						.offspringRatio(0.99)
						.offspringSelectionPolicy(doubleTournament)
						.survivorSelectionPolicy(doubleTournament)
						.build())
				.combinationPolicy(ProgramRandomCombine.build())
				.mutationPolicies(ProgramRandomMutate.of(0.10),
						ProgramRandomPrune.of(0.12),
						NodeReplacement.of(0.05),
						ProgramApplyRules.of(SimplificationRules.SIMPLIFY_RULES))
				.optimization(Optimization.MINIMIZE) // <3>
				.termination(or(ofMaxGeneration(200), ofFitnessAtMost(0.00001)))
				.fitness(computeFitness);
		final EAConfiguration<Double> eaConfiguration = eaConfigurationBuilder.build();
		// end::ea_config[]

		final var eaExecutionContextBuilder = GPEAExecutionContexts.<Double>forGP(random);
		EAExecutionContexts.enrichForScalarFitness(eaExecutionContextBuilder);

		eaExecutionContextBuilder.populationSize(populationSize);
		eaExecutionContextBuilder.numberOfPartitions(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));

		eaExecutionContextBuilder.addEvolutionListeners(
				EvolutionListeners.ofLogTopN(logger, 5, Comparator.<Double>reverseOrder(), (genotype) -> {
					final TreeChromosome<Operation<?>> chromosome = (TreeChromosome<Operation<?>>) genotype
							.getChromosome(0);
					final TreeNode<Operation<?>> root = chromosome.getRoot();

					return TreeNodeUtils.toStringTreeNode(root);
				}),
				SymbolicRegressionUtils.csvLoggerDouble(csvFilename,
						evolutionStep -> evolutionStep.fitness(),
						evolutionStep -> (double) evolutionStep.individual()
								.getChromosome(0, TreeChromosome.class)
								.getSize()));

		final EAExecutionContext<Double> eaExecutionContext = eaExecutionContextBuilder.build();
		final EASystem<Double> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<Double> evolutionResult = eaSystem.evolve();
		final Genotype bestGenotype = evolutionResult.bestGenotype();
		final TreeChromosome<Operation<?>> bestChromosome = (TreeChromosome<Operation<?>>) bestGenotype
				.getChromosome(0);
		logger.info("Best genotype: {}", bestChromosome.getRoot());
		logger.info("Best genotype - pretty print: {}", TreeNodeUtils.toStringTreeNode(bestChromosome.getRoot()));
	}

	public static void main(String[] args) throws IOException {

		/**
		 * Parse CLI
		 */

		final CommandLineParser parser = new DefaultParser();

		final Options options = new Options();
		options.addOption(PARAM_DEST_CSV, LONG_PARAM_DEST_CSV, true, "destination csv file");

		options.addOption(PARAM_POPULATION_SIZE, LONG_PARAM_POPULATION_SIZE, true, "Population size");

		String csvFilename = DEFAULT_DEST_CSV;
		int populationSize = DEFAULT_POPULATION_SIZE;
		try {
			final CommandLine line = parser.parse(options, args);

			if (line.hasOption(PARAM_DEST_CSV)) {
				csvFilename = line.getOptionValue(PARAM_DEST_CSV);
			}

			if (line.hasOption(PARAM_POPULATION_SIZE)) {
				populationSize = Integer.parseInt(line.getOptionValue(PARAM_POPULATION_SIZE));
			}

		} catch (ParseException exp) {
			cliError(options, "Unexpected exception:" + exp.getMessage());
		}

		logger.info("Population size: {}", populationSize);

		logger.info("CSV output located at {}", csvFilename);
		FileUtils.forceMkdirParent(new File(csvFilename));

		final var symbolicRegression = new SymbolicRegressionWithDoubleTournament();
		symbolicRegression.run(csvFilename, populationSize);
	}
}