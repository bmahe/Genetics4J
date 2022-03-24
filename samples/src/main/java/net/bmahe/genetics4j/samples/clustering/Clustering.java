package net.bmahe.genetics4j.samples.clustering;

import static net.bmahe.genetics4j.core.termination.Terminations.or;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.core.Fitness;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiCombinations;
import net.bmahe.genetics4j.core.spec.combination.MultiPointArithmetic;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Termination;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;
import net.bmahe.genetics4j.samples.CLIUtils;

public class Clustering {
	final static public Logger logger = LogManager.getLogger(Clustering.class);

	final static public int DEFAULT_NUM_CLUSTERS = 6;
	final static public int DEFAULT_NUMBER_TOURNAMENTS = 2;
	final static public int DEFAULT_POPULATION_SIZE = 120;
	final static public double DEFAULT_RANDOM_MUTATION_RATE = 0.15d;
	final static public double DEFAULT_CREEP_MUTATION_RATE = 0.20d;
	final static public double DEFAULT_CREEP_MUTATION_MEAN = 0.0d;
	final static public double DEFAULT_CREEP_MUTATION_STDDEV = 5;
	final static public int DEFAULT_COMBINATION_ARITHMETIC = 3;
	final static public int DEFAULT_COMBINATION_CROSSOVER_ARITHMETIC = 3;

	final static public String PARAM_NUM_CLUSTERS = "n";
	final static public String LONG_PARAM_NUM_CLUSTERS = "num-clusters";

	final static public String PARAM_NUMBER_TOURNAMENTS = "t";
	final static public String LONG_PARAM_NUMBER_TOURNAMENTS = "num-tournaments";

	final static public String PARAM_POPULATION_SIZE = "p";
	final static public String LONG_PARAM_POPULATION_SIZE = "population-size";

	final static public String PARAM_SOURCE_CLUSTERS_CSV = "c";
	final static public String LONG_PARAM_SOURCE_CUSTERS_CSV = "source-clusters";

	final static public String PARAM_SOURCE_DATA_CSV = "s";
	final static public String LONG_PARAM_SOURCE_DATA_CSV = "source-data";

	final static public String PARAM_FIXED_TERMINATION = "f";
	final static public String LONG_PARAM_FIXED_TERMINATION = "fixed-termination";

	final static public String PARAM_RANDOM_MUTATION_RATE = "r";
	final static public String LONG_PARAM_RANDOM_MUTATION_RATE = "random-mutation-rate";

	final static public String PARAM_CREEP_MUTATION_RATE = "m";
	final static public String LONG_PARAM_CREEP_MUTATION_RATE = "creep-mutation-rate";

	final static public String PARAM_CREEP_MUTATION_MEAN = "a";
	final static public String LONG_PARAM_CREEP_MUTATION_MEAN = "creep-mutation-mean";

	final static public String PARAM_CREEP_MUTATION_STD_DEV = "d";
	final static public String LONG_PARAM_CREEP_MUTATION_STD_DEV = "creep-mutation-std-dev";

	final static public String PARAM_COMBINATION_ARITHMETIC = "b";
	final static public String LONG_PARAM_COMBINATION_ARITHMETIC = "combination-arithmetic";

	final static public String PARAM_COMBINATION_CROSSOVER = "e";
	final static public String LONG_PARAM_COMBINATION_CROSSOVER = "combination-crossover";

	final static public String PARAM_OUTPUT_CSV = "o";
	final static public String LONG_PARAM_OUTPUT_CSV = "output";

	final static public String PARAM_OUTPUT_WITH_SSE_CSV = "g";
	final static public String LONG_PARAM_OUTPUT_WITH_SSE_CSV = "output-sse";

	final static public String PARAM_BASE_DIR_OUTPUT = "i";
	final static public String LONG_PARAM_BASE_DIR_OUTPUT = "base-dir";

	public static void cliError(final Options options, final String errorMessage) {
		CLIUtils.cliHelpAndExit(logger, Clustering.class, options, errorMessage);
	}

	private final static double computeDistance(final double[][] array, final int i, final int j) {
		final double xDiff = array[j][0] - array[i][0];
		final double yDiff = array[j][1] - array[i][1];
		return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
	}

	private final static double[][] computeAllDistances(final double[][] array) {

		final double[][] distances = new double[array.length][array.length];

		for (int i = 0; i < array.length; i++) {
			distances[i][i] = 0.0;
		}

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < i; j++) {
				final double distance = computeDistance(array, i, j);
				distances[i][j] = distance;
				distances[j][i] = distance;
			}
		}

		return distances;
	}

	// tag::cluster_generation[]
	public static double[][] generateClusters(final Random random, final int numClusters, final double minX,
			final double maxX, final double minY, final double maxY) {
		Validate.notNull(random);
		Validate.isTrue(numClusters > 0);
		Validate.isTrue(minX <= maxX);
		Validate.isTrue(minY <= maxY);

		logger.info("Generating {} clusters", numClusters);

		final double[][] clusters = new double[numClusters][2];
		for (int i = 0; i < numClusters; i++) {
			clusters[i][0] = minX + random.nextDouble() * (maxX - minX);
			clusters[i][1] = minY + random.nextDouble() * (maxY - minY);
		}

		return clusters;
	}
	// end::cluster_generation[]

	// tag::data_generation[]
	public static double[][] generateDataPoints(final Random random, final double[][] clusters, final int numDataPoints,
			final double radius) {
		Validate.notNull(random);
		Validate.notNull(clusters);
		Validate.isTrue(clusters.length > 0);

		final int numClusters = clusters.length;
		final double[][] data = new double[numDataPoints][3];
		for (int i = 0; i < numDataPoints; i++) {
			final int clusterIndex = i % numClusters;

			data[i][0] = random.nextGaussian() * radius + clusters[clusterIndex][0];
			data[i][1] = random.nextGaussian() * radius + clusters[clusterIndex][1];
			data[i][2] = clusterIndex;
		}

		return data;
	}
	// end::data_generation[]

	public static void doGA(final int k, final double min, final double max, final int numberTournaments,
			final int combinationArithmetic, final int combinationCrossover, final double randomMutationRate,
			final double creepMutationRate, final double creepMutationMean, final double creepMutationStdDev,
			final Fitness<Double> fitnessFunction, final Termination<Double> terminations, final int populationSize,
			final String outputCSV, final double[][] data, final double[][] distances, final String baseDir,
			final String filenameSuffix) throws IOException {

		// tag::ea_configuration[]
		final var eaConfigurationBuilder = new EAConfiguration.Builder<Double>();
		eaConfigurationBuilder.chromosomeSpecs(DoubleChromosomeSpec.of(k * 2, min, max))
				.parentSelectionPolicy(Tournament.of(numberTournaments))
				.combinationPolicy(MultiCombinations.of(MultiPointArithmetic.of(combinationArithmetic, 0.5),
						MultiPointCrossover.of(combinationCrossover)))
				.mutationPolicies(MultiMutations.of(RandomMutation.of(randomMutationRate),
						CreepMutation.ofNormal(creepMutationRate, creepMutationMean, creepMutationStdDev)))
				.fitness(fitnessFunction)
				.postEvaluationProcessor(FitnessSharingUtils.clusterDistance)
				.termination(terminations);
		final var eaConfiguration = eaConfigurationBuilder.build();
		// end::ea_configuration[]

		final var eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(populationSize)
				.addEvolutionListeners(EvolutionListeners.ofLogTopN(logger, 3),
						new CSVEvolutionListener.Builder<Double, Double>().filename(outputCSV)
								.columnExtractors(List.of(
										ColumnExtractor.of("generation", (evolutionStep) -> evolutionStep.generation()),
										ColumnExtractor.of("fitness", (evolutionStep) -> evolutionStep.fitness()),
										ColumnExtractor.of("combination_arithmetic", (evolutionStep) -> combinationArithmetic),
										ColumnExtractor.of("combination_crossover", (evolutionStep) -> combinationCrossover),
										ColumnExtractor.of("random_mutation_rate", (evolutionStep) -> randomMutationRate),
										ColumnExtractor.of("creep_mutation_mean", (evolutionStep) -> creepMutationMean),
										ColumnExtractor.of("creep_mutation_stddev", (evolutionStep) -> creepMutationStdDev),
										ColumnExtractor.of("creep_mutation_rate", (evolutionStep) -> creepMutationRate)))
								.build())
				.build();

		final var eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final var evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: {}", evolutionResult.bestGenotype());
		logger.info("  with fitness: {}", evolutionResult.bestFitness());
		logger.info("  at generation: {}", evolutionResult.generation());

		final Genotype bestGenotype = evolutionResult.bestGenotype();
		final double[][] bestPhenotype = PhenotypeUtils.toPhenotype(bestGenotype);
		logger.info("Best phenotype:");
		for (int i = 0; i < k; i++) {
			logger.info("\tx: {} - y: {}", bestPhenotype[i][0], bestPhenotype[i][1]);
		}
		final int[] bestClusterMembership = FitnessUtils.assignDataToClusters(data, distances, bestPhenotype);
		IOUtils
				.persistDataPoints(data, bestClusterMembership, baseDir + "clustering-result-ga" + filenameSuffix + ".csv");
		IOUtils.persistClusters(bestPhenotype, baseDir + "clustering-result-clusters-ga" + filenameSuffix + ".csv");

	}

	public static List<CentroidCluster<LocationWrapper>> apacheCommonsMathCluster(final double[][] clusters,
			final double[][] data) {

		logger.info("Initializing kmeans from Apache Commons Math");

		final long startTs = System.currentTimeMillis();

		final int numClusters = clusters.length;
		final int numDataPoints = data.length;

		final List<LocationWrapper> clusterInput = new ArrayList<LocationWrapper>(numDataPoints);
		for (int i = 0; i < numDataPoints; i++) {
			clusterInput.add(new LocationWrapper(data[i]));
		}

		logger.info("Running kmeans");

		final KMeansPlusPlusClusterer<LocationWrapper> clusterer = new KMeansPlusPlusClusterer<LocationWrapper>(
				numClusters,
				10_000);
		final List<CentroidCluster<LocationWrapper>> clusterResults = clusterer.cluster(clusterInput);

		final long durationMs = System.currentTimeMillis() - startTs;
		logger.info("Computation time: {}", DurationFormatUtils.formatDurationHMS(durationMs));

		return clusterResults;
	}

	public static void main(String[] args) throws IOException {
		logger.info("Starting");

		final Random random = new Random();

		final double min = -100;
		final double max = 100;
		final double minX = min;
		final double maxX = max;
		final double minY = min;
		final double maxY = max;

		final double radius = 8;

		final int numDataPoints = 1_000;

		/**
		 * Parse CLI
		 */

		final CommandLineParser parser = new DefaultParser();

		final Options options = new Options();
		options.addOption(PARAM_NUM_CLUSTERS, LONG_PARAM_NUM_CLUSTERS, true, "number of clusters");
		options.addOption(PARAM_NUMBER_TOURNAMENTS, LONG_PARAM_NUMBER_TOURNAMENTS, true, "number of tournaments");
		options.addOption(PARAM_SOURCE_CLUSTERS_CSV, LONG_PARAM_SOURCE_CUSTERS_CSV, true, "source csv file for clusters");
		options.addOption(PARAM_SOURCE_DATA_CSV, LONG_PARAM_SOURCE_DATA_CSV, true, "source csv file for data");
		options.addOption(PARAM_OUTPUT_CSV, LONG_PARAM_OUTPUT_CSV, true, "output csv");
		options.addOption(PARAM_OUTPUT_WITH_SSE_CSV, LONG_PARAM_OUTPUT_WITH_SSE_CSV, true, "output with sse csv");
		options
				.addOption(PARAM_COMBINATION_ARITHMETIC, LONG_PARAM_COMBINATION_ARITHMETIC, true, "combination arithmetic");
		options.addOption(PARAM_COMBINATION_CROSSOVER, LONG_PARAM_COMBINATION_CROSSOVER, true, "combination crossover");
		options.addOption(PARAM_POPULATION_SIZE, LONG_PARAM_POPULATION_SIZE, true, "population size");
		options.addOption(PARAM_BASE_DIR_OUTPUT, LONG_PARAM_BASE_DIR_OUTPUT, true, "base directory");
		options.addOption(PARAM_CREEP_MUTATION_STD_DEV,
				LONG_PARAM_CREEP_MUTATION_STD_DEV,
				true,
				"creep mutation std dev. Default: " + DEFAULT_CREEP_MUTATION_STDDEV);
		options.addOption(PARAM_CREEP_MUTATION_MEAN,
				LONG_PARAM_CREEP_MUTATION_MEAN,
				true,
				"creep mutation mean. Default: " + DEFAULT_CREEP_MUTATION_MEAN);
		options.addOption(PARAM_CREEP_MUTATION_RATE,
				LONG_PARAM_CREEP_MUTATION_RATE,
				true,
				"creep mutation rate. Default: " + DEFAULT_CREEP_MUTATION_RATE);
		options.addOption(PARAM_RANDOM_MUTATION_RATE,
				LONG_PARAM_RANDOM_MUTATION_RATE,
				true,
				"random mutation rate. Default: " + DEFAULT_RANDOM_MUTATION_RATE);
		options.addOption(PARAM_FIXED_TERMINATION,
				LONG_PARAM_FIXED_TERMINATION,
				true,
				"Fix the termination to the specified number of generations");

		Optional<String> paramSourceClustersCSV = Optional.empty();
		Optional<String> paramSourceDataCSV = Optional.empty();
		Optional<Integer> paramNumClusters = Optional.empty();
		Optional<String> paramOutputCSV = Optional.empty();
		Optional<String> paramOutputWithSSECSV = Optional.empty();
		Optional<Long> paramFixedTermination = Optional.empty();

		int numberTournaments = DEFAULT_NUMBER_TOURNAMENTS;
		int populationSize = DEFAULT_POPULATION_SIZE;
		final double randomMutationRate;
		final double creepMutationRate;
		final double creepMutationMean;
		final double creepMutationStdDev;
		final int combinationArithmetic;
		final int combinationCrossover;
		final String baseDir;
		try {
			final CommandLine line = parser.parse(options, args);

			if (line.hasOption(PARAM_NUMBER_TOURNAMENTS)) {
				numberTournaments = Integer.parseInt(line.getOptionValue(PARAM_NUMBER_TOURNAMENTS)
						.strip());
			}

			baseDir = Optional.ofNullable(line.getOptionValue(PARAM_BASE_DIR_OUTPUT))
					.orElse("");

			combinationArithmetic = Optional.ofNullable(line.getOptionValue(PARAM_COMBINATION_ARITHMETIC))
					.map(String::strip)
					.map(Integer::parseInt)
					.orElse(DEFAULT_COMBINATION_ARITHMETIC);

			combinationCrossover = Optional.ofNullable(line.getOptionValue(PARAM_COMBINATION_CROSSOVER))
					.map(String::strip)
					.map(Integer::parseInt)
					.orElse(DEFAULT_COMBINATION_CROSSOVER_ARITHMETIC);

			paramNumClusters = Optional.ofNullable(line.getOptionValue(PARAM_NUM_CLUSTERS))
					.map(String::strip)
					.map(Integer::parseInt);
			populationSize = Optional.ofNullable(line.getOptionValue(PARAM_POPULATION_SIZE))
					.map(String::strip)
					.map(Integer::parseInt)
					.orElse(DEFAULT_POPULATION_SIZE);

			paramSourceClustersCSV = Optional.ofNullable(line.getOptionValue(PARAM_SOURCE_CLUSTERS_CSV))
					.map(String::strip);

			paramSourceDataCSV = Optional.ofNullable(line.getOptionValue(PARAM_SOURCE_DATA_CSV))
					.map(String::strip);

			paramOutputCSV = Optional.ofNullable(line.getOptionValue(PARAM_OUTPUT_CSV))
					.map(String::strip);
			paramOutputWithSSECSV = Optional.ofNullable(line.getOptionValue(PARAM_OUTPUT_WITH_SSE_CSV))
					.map(String::strip);

			paramFixedTermination = Optional.ofNullable(line.getOptionValue(PARAM_FIXED_TERMINATION))
					.map(String::strip)
					.map(Long::parseLong);

			randomMutationRate = Optional.ofNullable(line.getOptionValue(PARAM_RANDOM_MUTATION_RATE))
					.map(String::strip)
					.map(Double::parseDouble)
					.orElse(DEFAULT_RANDOM_MUTATION_RATE);

			creepMutationRate = Optional.ofNullable(line.getOptionValue(PARAM_CREEP_MUTATION_RATE))
					.map(String::strip)
					.map(Double::parseDouble)
					.orElse(DEFAULT_CREEP_MUTATION_RATE);

			creepMutationMean = Optional.ofNullable(line.getOptionValue(PARAM_CREEP_MUTATION_MEAN))
					.map(String::strip)
					.map(Double::parseDouble)
					.orElse(DEFAULT_CREEP_MUTATION_MEAN);

			creepMutationStdDev = Optional.ofNullable(line.getOptionValue(PARAM_CREEP_MUTATION_STD_DEV))
					.map(String::strip)
					.map(Double::parseDouble)
					.orElse(DEFAULT_CREEP_MUTATION_STDDEV);

			logger.info("Unrecognized args:");
			boolean hasError = false;
			for (final String extraArg : line.getArgList()) {
				logger.info("\t[{}]", extraArg);
				if (extraArg.isBlank() == false) {
					hasError = true;
				}
			}

			if (hasError) {
				throw new RuntimeException();
			}
		} catch (ParseException exp) {
			cliError(options, "Unexpected exception:" + exp.getMessage());

			// This piece will never execute
			throw new RuntimeException(); // java doesn't detect the System.exit in cliError and create some issues with
													// potential not initialized final parameters.
		}

		final int numClusters = paramNumClusters.orElse(DEFAULT_NUM_CLUSTERS);

		logger.info("Preparing {} clusters", numClusters);
		final double[][] clusters = paramSourceClustersCSV.map(IOUtils::loadClusters)
				.orElseGet(() -> generateClusters(random, numClusters, minX, maxX, minY, maxY));
		logger.info("Found {} clusters", clusters.length);

		logger.info("Preparing data points");
		final double[][] data = paramSourceDataCSV.map(sourceDataCSVFileName -> {
			try {
				logger.info("Loading data points");
				return IOUtils.loadDataPoints(sourceDataCSVFileName);
			} catch (IOException e) {
				throw new RuntimeException("Could not load " + sourceDataCSVFileName, e);
			}
		})
				.orElseGet(() -> generateDataPoints(random, clusters, numDataPoints, radius));
		final double[][] distances = computeAllDistances(data);

		final String originalClustersFilename = "originalClusters.csv";
		if (paramSourceClustersCSV.isPresent()) {
			logger.info("Not persisting clusters since it was provided");
		} else {
			logger.info("Saving clusters to CSV: {}", originalClustersFilename);
			IOUtils.persistClusters(clusters, originalClustersFilename);
		}

		final String originalDataFilename = "originalData.csv";
		if (paramSourceDataCSV.isPresent()) {
			logger.info("Not persisting data since it was provided");
		} else {
			logger.info("Saving data to CSV: {}", originalDataFilename);
			IOUtils.persistDataPoints(data, originalDataFilename);
		}

		logger.info("Clustering data with Apache Commons Math");

		final List<CentroidCluster<LocationWrapper>> clusterResults = apacheCommonsMathCluster(clusters, data);

		logger.info("Definition of genetic problem");

		final int k = numClusters;
		final var fitnessFunction = FitnessUtils.computeFitness(numDataPoints, data, distances, k);

		final var terminations = paramFixedTermination
				.map(maxGeneration -> Terminations.<Double>ofMaxGeneration(maxGeneration))
				.orElseGet(() -> or(Terminations.<Double>ofMaxGeneration(500), Terminations.ofStableFitness(50)));

		logger.info("Terminations: {}", paramFixedTermination);
		logger.info("Parameters: random_mutation_rate: {} - creep_mutation_rate: {} - creep_mutation_stddev: {} ",
				randomMutationRate,
				creepMutationRate,
				creepMutationStdDev);
		logger.info("Combinations: arithmetic {} ; crossover {}", combinationArithmetic, combinationCrossover);

		logger.info("Running GA with Silhouette score");
		doGA(k,
				min,
				max,
				numberTournaments,
				combinationArithmetic,
				combinationCrossover,
				randomMutationRate,
				creepMutationRate,
				creepMutationMean,
				creepMutationStdDev,
				fitnessFunction,
				terminations,
				populationSize,
				paramOutputCSV.orElse("output.csv"),
				data,
				distances,
				baseDir,
				"");

		logger.info("Running GA with Silhouette score + SSE");
		final var fitnessFunctionWithSumSquareErrors = FitnessUtils
				.computeFitnessWithSSE(numDataPoints, data, distances, k);
		doGA(k,
				min,
				max,
				numberTournaments,
				combinationArithmetic,
				combinationCrossover,
				randomMutationRate,
				creepMutationRate,
				creepMutationMean,
				creepMutationStdDev,
				fitnessFunctionWithSumSquareErrors,
				terminations,
				populationSize,
				paramOutputWithSSECSV.orElse("output-with-sse.csv"),
				data,
				distances,
				baseDir,
				"-with-sse");

		logger.info("Original clusters:");
		final double[] originalMeans = new double[numClusters * 2];
		for (int i = 0; i < numClusters; i++) {
			logger.info("\tx: {} - y: {}", clusters[i][0], clusters[i][1]);
			originalMeans[i * 2] = clusters[i][0];
			originalMeans[i * 2 + 1] = clusters[i][1];
		}
		final var originalFitness = fitnessFunction
				.compute(new Genotype(new DoubleChromosome(k * 2, -100.0d, 100.0d, originalMeans)));
		logger.info("Original fitness: {}", originalFitness);
		final int[] originalClusterMembership = FitnessUtils.assignDataToClusters(data, distances, clusters);
		IOUtils.persistDataPoints(data, originalClusterMembership, baseDir + "clustering-result-original.csv");
		IOUtils.persistClusters(clusters, baseDir + "clustering-result-clusters-original.csv");

		logger.info("kmeans output:");
		// output the clusters
		final double[] kmeansClusters = new double[numClusters * 2];
		for (int i = 0; i < clusterResults.size(); i++) {
			final CentroidCluster<LocationWrapper> centroidCluster = clusterResults.get(i);
			logger.info("\t{}", centroidCluster.getCenter());
			kmeansClusters[i * 2] = centroidCluster.getCenter()
					.getPoint()[0];
			kmeansClusters[i * 2 + 1] = centroidCluster.getCenter()
					.getPoint()[1];
		}
		final var kmeansGenotype = new Genotype(new DoubleChromosome(k * 2, -100.0d, 100.0d, kmeansClusters));
		final var kmeansFitness = fitnessFunction.compute(kmeansGenotype);
		logger.info("kmeans fitness: {}", kmeansFitness);

		final int[] kmeansClusterMembership = FitnessUtils
				.assignDataToClusters(data, distances, PhenotypeUtils.toPhenotype(kmeansGenotype));
		IOUtils.persistDataPoints(data, kmeansClusterMembership, baseDir + "clustering-result-kmeans.csv");
		IOUtils.persistClusters(PhenotypeUtils.toPhenotype(kmeansGenotype),
				baseDir + "clustering-result-clusters-kmeans.csv");

		logger.info("Done");
	}
}