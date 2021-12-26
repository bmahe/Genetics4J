package net.bmahe.genetics4j.samples.symbolicregression;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

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
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.replacement.Elitism;
import net.bmahe.genetics4j.core.termination.Terminations;
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
import net.bmahe.genetics4j.moo.MOOEAExecutionContexts;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class SymbolicRegressionWithMOO {
	final static public Logger logger = LogManager.getLogger(SymbolicRegressionWithMOO.class);

	final static public String PARAM_DEST_CSV = "d";
	final static public String LONG_PARAM_DEST_CSV = "csv-dest";

	final static public String PARAM_POPULATION_SIZE = "p";
	final static public String LONG_PARAM_POPULATION_SIZE = "population-size";

	final static public String DEFAULT_DEST_CSV = SymbolicRegressionWithMOO.class.getSimpleName() + ".csv";

	final static public int DEFAULT_POPULATION_SIZE = 500;

	public static void cliError(final Options options, final String errorMessage) {
		final HelpFormatter formatter = new HelpFormatter();
		logger.error(errorMessage);
		formatter.printHelp(SymbolicRegressionWithMOO.class.getSimpleName(), options);
		System.exit(-1);
	}

	@SuppressWarnings("unchecked")
	public void run(String csvFilename, int populationSize) {
		Validate.isTrue(StringUtils.isNotBlank(csvFilename));
		Validate.isTrue(populationSize > 0);

		final Random random = new Random();

		final Program program = SymbolicRegressionUtils.buildProgram(random);

		final Comparator<Genotype> deduplicator = (a, b) -> TreeNodeUtils.compare(a, b, 0);

		// tag::compute_fitness[]
		final Fitness<FitnessVector<Double>> computeFitness = (genoType) -> {
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
					if (Double.isFinite(resultDouble)) {
						mse += (expected - resultDouble) * (expected - resultDouble);
					} else {
						mse += 1_000_000_000;
					}
				}
			}

			return Double.isFinite(mse) ? new FitnessVector<Double>(mse / 100.0,
					(double) chromosome.getRoot()
							.getSize())
					: new FitnessVector<Double>(Double.MAX_VALUE, Double.MAX_VALUE);
		};
		// end::compute_fitness[]

		// tag::ea_config[]
		final var eaConfigurationBuilder = new EAConfiguration.Builder<FitnessVector<Double>>();
		eaConfigurationBuilder.chromosomeSpecs(ProgramTreeChromosomeSpec.of(program)) // <1>
				.parentSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 3, deduplicator)) // <2>
				.replacementStrategy(Elitism.builder() // <3>
						.offspringRatio(0.995)
						.offspringSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 3, deduplicator))
						.survivorSelectionPolicy(NSGA2Selection.ofFitnessVector(2, deduplicator))
						.build())
				.combinationPolicy(ProgramRandomCombine.build())
				.mutationPolicies(MultiMutations
						.of(ProgramRandomMutate.of(0.15 * 3), ProgramRandomPrune.of(0.15 * 3), NodeReplacement.of(0.15 * 3)),
						ProgramApplyRules.of(SimplificationRules.SIMPLIFY_RULES))
				.optimization(Optimization.MINIMIZE)
				.termination(Terminations.or(Terminations.<FitnessVector<Double>>ofMaxGeneration(200),
						(eaConfiguration, generation, population, fitness) -> fitness.stream()
								.anyMatch(fv -> fv.get(0) <= 0.000001 && fv.get(1) <= 20))) // <4>
				.fitness(computeFitness);
		final EAConfiguration<FitnessVector<Double>> eaConfiguration = eaConfigurationBuilder.build();
		// end::ea_config[]

		// tag::eae_moo[]
		final var eaExecutionContextBuilder = GPEAExecutionContexts.<FitnessVector<Double>>forGP(random);
		MOOEAExecutionContexts.enrichWithMOO(eaExecutionContextBuilder);
		// end::eae_moo[]
		eaExecutionContextBuilder.populationSize(populationSize);
		eaExecutionContextBuilder.numberOfPartitions(Math.max(1,
				Runtime.getRuntime()
						.availableProcessors() - 3));

		eaExecutionContextBuilder.addEvolutionListeners(
				EvolutionListeners.ofLogTopN(logger,
						5,
						Comparator.<FitnessVector<Double>, Double>comparing(fv -> fv.get(0))
								.reversed(),
						(genotype) -> TreeNodeUtils.toStringTreeNode(genotype, 0)),
				SymbolicRegressionUtils.csvLogger(csvFilename,
						evolutionStep -> evolutionStep.fitness()
								.get(0),
						evolutionStep -> evolutionStep.fitness()
								.get(1)));

		final EAExecutionContext<FitnessVector<Double>> eaExecutionContext = eaExecutionContextBuilder.build();
		final EASystem<FitnessVector<Double>> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<FitnessVector<Double>> evolutionResult = eaSystem.evolve();
		final Genotype bestGenotype = evolutionResult.bestGenotype();
		final TreeChromosome<Operation<?>> bestChromosome = (TreeChromosome<Operation<?>>) bestGenotype.getChromosome(0);
		logger.info("Best genotype: {}", bestChromosome.getRoot());
		logger.info("Best genotype - pretty print: {}", TreeNodeUtils.toStringTreeNode(bestChromosome.getRoot()));

		final int depthIdx = 1;
		for (int i = 0; i < 15; i++) {
			final int depth = i;
			final Optional<Integer> optIdx = IntStream.range(0,
					evolutionResult.fitness()
							.size())
					.boxed()
					.filter((idx) -> evolutionResult.fitness()
							.get(idx)
							.get(depthIdx) == depth)
					.sorted((a, b) -> Double.compare(evolutionResult.fitness()
							.get(a)
							.get(0),
							evolutionResult.fitness()
									.get(b)
									.get(0)))
					.findFirst();

			optIdx.stream()
					.forEach((idx) -> {
						final TreeChromosome<Operation<?>> treeChromosome = (TreeChromosome<Operation<?>>) evolutionResult
								.population()
								.get(idx)
								.getChromosome(0);

						logger.info("Best genotype for depth {} - score {} -> {}",
								depth,
								evolutionResult.fitness()
										.get(idx)
										.get(0),
								TreeNodeUtils.toStringTreeNode(treeChromosome.getRoot()));
					});
		}
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

		final var symbolicRegression = new SymbolicRegressionWithMOO();
		symbolicRegression.run(csvFilename, populationSize);
	}
}