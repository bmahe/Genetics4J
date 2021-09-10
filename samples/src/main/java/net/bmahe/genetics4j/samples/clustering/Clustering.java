package net.bmahe.genetics4j.samples.clustering;

import static net.bmahe.genetics4j.core.termination.Terminations.or;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
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
import net.bmahe.genetics4j.core.postevaluationprocess.FitnessSharing;
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
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;

public class Clustering {
	final static public Logger logger = LogManager.getLogger(Clustering.class);

	final static public int DEFAULT_NUM_CLUSTERS = 5;
	final static public int DEFAULT_NUMBER_TOURNAMENTS = 2;
	final static public int DEFAULT_POPULATION_SIZE = 300;
	final static public double DEFAULT_RANDOM_MUTATION_RATE = 0.15d;
	final static public double DEFAULT_CREEP_MUTATION_RATE = 0.20d;
	final static public double DEFAULT_CREEP_MUTATION_MEAN = 0.0d;
	final static public double DEFAULT_CREEP_MUTATION_STDDEV = 10;
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

	public static void cliError(final Options options, final String errorMessage) {
		final HelpFormatter formatter = new HelpFormatter();
		logger.error(errorMessage);
		formatter.printHelp(Clustering.class.getSimpleName(), options);
		System.exit(-1);
	}

	public static double[][] toPhenotype(final Genotype genotype) {
		Validate.notNull(genotype);

		final var doubleChromosome = genotype.getChromosome(0, DoubleChromosome.class);

		final int numClusters = doubleChromosome.getSize() / 2;
		final double[][] clusters = new double[numClusters][2];

		for (int i = 0; i < numClusters; i++) {
			clusters[i][0] = doubleChromosome.getAllele(i * 2);
			clusters[i][1] = doubleChromosome.getAllele(i * 2 + 1);
		}

		return clusters;
	}

	private final static FitnessSharing simpleDistanceFitnessSharing = FitnessSharing.ofStandard((i0, i1) -> {
		final var p0 = toPhenotype(i0);
		final var p1 = toPhenotype(i1);

		double distanceAcc = 0.0d;
		for (int i = 0; i < p0.length; i++) {
			double distance = 0.0d;
			distance += (p1[i][0] - p0[i][0]) * (p1[i][0] - p0[i][0]);
			distance += (p1[i][1] - p0[i][1]) * (p1[i][1] - p0[i][1]);

			distanceAcc += Math.sqrt(distance);
		}

		return distanceAcc;
	}, 5.0);

	private final static FitnessSharing hausDorffFitnessSharing = FitnessSharing.ofStandard((i0, i1) -> {
		final var p0 = toPhenotype(i0);
		final var p1 = toPhenotype(i1);

		double min0 = Double.MAX_VALUE;
		for (int i = 0; i < p0.length; i++) {
			for (int j = 0; j < p1.length; j++) {
				final double distance = Math
						.sqrt((p0[i][0] - p1[j][0]) * (p0[i][0] - p1[j][0]) - (p0[i][1] - p1[j][1]) * (p0[i][1] - p1[j][1]));
				if (distance < min0) {
					min0 = distance;
				}
			}
		}

		double min1 = Double.MAX_VALUE;
		for (int i = 0; i < p1.length; i++) {
			for (int j = 0; j < p0.length; j++) {
				final double distance = Math
						.sqrt((p0[i][0] - p1[j][0]) * (p0[i][0] - p1[j][0]) - (p0[i][1] - p1[j][1]) * (p0[i][1] - p1[j][1]));
				if (distance < min1) {
					min1 = distance;
				}
			}
		}

		return Math.max(min0, min1);

	}, 5.0);

	private final static Fitness<Double> computeFitness(final int numDataPoints, final double[][] data,
			final int numClusters) {
		Validate.isTrue(numDataPoints > 0);
		Validate.isTrue(numDataPoints == data.length);
		Validate.isTrue(numClusters > 0);

		return (genoType) -> {

			final double[][] clusters = toPhenotype(genoType);

			final double[] closestClusterIndex = new double[numDataPoints];
			final double[] closestClusterDistance = new double[numDataPoints];
			final double[] secondClosestClusterIndex = new double[numDataPoints];
			final double[] secondClosestClusterDistance = new double[numDataPoints];

			final double[] allClusterDistance = new double[numClusters];
			for (int i = 0; i < numDataPoints; i++) {
				closestClusterIndex[i] = -1;
				secondClosestClusterIndex[i] = -1;

				final double dataX = data[i][0];
				final double dataY = data[i][1];

				for (int clusterIndex = 0; clusterIndex < numClusters; clusterIndex++) {
					final double clusterX = clusters[clusterIndex][0];
					final double clusterY = clusters[clusterIndex][1];

					final double distance = Math
							.sqrt(((clusterX - dataX) * (clusterX - dataX)) + ((clusterY - dataY) * (clusterY - dataY)));
					allClusterDistance[clusterIndex] = distance;

					if (closestClusterIndex[i] == -1 || distance < closestClusterDistance[i]) {
						closestClusterIndex[i] = clusterIndex;
						closestClusterDistance[i] = distance;
					}
				}

				for (int clusterIndex = 0; clusterIndex < numClusters; clusterIndex++) {
					if (clusterIndex != closestClusterIndex[i] && (secondClosestClusterIndex[i] == -1
							|| allClusterDistance[clusterIndex] < secondClosestClusterDistance[i])) {
						secondClosestClusterIndex[i] = clusterIndex;
						secondClosestClusterDistance[i] = allClusterDistance[clusterIndex];
					}
				}

			}

			double sumA = 0.0d;
			double sumB = 0.0d;
			for (int i = 0; i < numDataPoints; i++) {
				sumA += closestClusterDistance[i];
				sumB += secondClosestClusterDistance[i];
			}
			final double a = sumA / numDataPoints;
			final double b = sumB / numDataPoints;

			return (b - a) / Math.max(a, b);
		};
	}

	public static double[][] generateClusters(final Random random, final int numClusters, final double minX,
			final double maxX, final double minY, final double maxY) {

		logger.info("Generating {} clusters", numClusters);

		final double[][] clusters = new double[numClusters][2];
		for (int i = 0; i < numClusters; i++) {
			clusters[i][0] = minX + random.nextDouble() * (maxX - minX);
			clusters[i][1] = minY + random.nextDouble() * (maxY - minY);
		}

		return clusters;
	}

	public static double[][] generateDataPoints(final Random random, final double[][] clusters, final int numDataPoints,
			final double radius) {

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

	public static double[][] loadClusters(final String filename) {
		logger.info("Loading clusters from {}", filename);

		Reader in;
		try {
			in = new FileReader(filename);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		Iterable<CSVRecord> records;
		try {
			records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final List<double[]> entries = new ArrayList<>();
		for (final CSVRecord record : records) {
			final double x = Double.parseDouble(record.get(1));
			final double y = Double.parseDouble(record.get(2));

			entries.add(new double[] { x, y });
		}

		final double[][] clusters = new double[entries.size()][2];
		for (int i = 0; i < entries.size(); i++) {
			clusters[i][0] = entries.get(i)[0];
			clusters[i][1] = entries.get(i)[1];
		}
		return clusters;
	}

	public static double[][] loadDataPoints(final String filename) throws IOException {
		final Reader in = new FileReader(filename);
		final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader()
				.withSkipHeaderRecord(true)
				.parse(in);
		final List<double[]> entries = new ArrayList<>();
		for (final CSVRecord record : records) {
			final double cluster = Double.parseDouble(record.get(0));
			final double x = Double.parseDouble(record.get(1));
			final double y = Double.parseDouble(record.get(2));

			entries.add(new double[] { cluster, x, y });
		}

		final double[][] clusters = new double[entries.size()][3];
		for (int i = 0; i < entries.size(); i++) {
			clusters[i][0] = entries.get(i)[1];
			clusters[i][1] = entries.get(i)[2];
			clusters[i][2] = entries.get(i)[0];
		}
		return clusters;
	}

	public static void persistClusters(final double[][] clusters, final String originalClustersFilename)
			throws IOException {
		logger.info("Saving clusters to CSV: {}", originalClustersFilename);

		final CSVPrinter csvPrinter;
		try {
			csvPrinter = CSVFormat.DEFAULT.withAutoFlush(true)
					.withHeader(new String[] { "cluster", "x", "y" })
					.print(Path.of(originalClustersFilename), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Could not open {}", originalClustersFilename, e);
			throw new RuntimeException("Could not open file " + originalClustersFilename, e);
		}

		for (int i = 0; i < clusters.length; i++) {
			try {
				csvPrinter.printRecord(i, clusters[i][0], clusters[i][1]);
			} catch (IOException e) {
				throw new RuntimeException("Could not write data", e);
			}
		}
		csvPrinter.close(true);
	}

	public static void persistDataPoints(final double[][] clusters, final double[][] data,
			final String originalDataFilename) throws IOException {
		logger.info("Saving data to CSV: {}", originalDataFilename);

		final int numDataPoints = data.length;

		final CSVPrinter csvPrinter;
		try {
			csvPrinter = CSVFormat.DEFAULT.withAutoFlush(true)
					.withHeader(new String[] { "cluster", "x", "y" })
					.print(Path.of(originalDataFilename), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Could not open {}", originalDataFilename, e);
			throw new RuntimeException("Could not open file " + originalDataFilename, e);
		}

		for (int i = 0; i < numDataPoints; i++) {
			try {
				csvPrinter.printRecord((int) data[i][2], data[i][0], data[i][1]);
			} catch (IOException e) {
				throw new RuntimeException("Could not write data", e);
			}
		}
		csvPrinter.close(true);
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
				numClusters, 10_000);
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

		final int numDataPoints = 5000;

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
		options
				.addOption(PARAM_COMBINATION_ARITHMETIC, LONG_PARAM_COMBINATION_ARITHMETIC, true, "combination arithmetic");
		options.addOption(PARAM_COMBINATION_CROSSOVER, LONG_PARAM_COMBINATION_CROSSOVER, true, "combination crossover");
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
		Optional<Long> paramFixedTermination = Optional.empty();

		int numberTournaments = DEFAULT_NUMBER_TOURNAMENTS;
		int populationSize = DEFAULT_POPULATION_SIZE;
		final double randomMutationRate;
		final double creepMutationRate;
		final double creepMutationMean;
		final double creepMutationStdDev;
		final int combinationArithmetic;
		final int combinationCrossover;
		try {
			final CommandLine line = parser.parse(options, args);

			if (line.hasOption(PARAM_NUMBER_TOURNAMENTS)) {
				numberTournaments = Integer.parseInt(line.getOptionValue(PARAM_NUMBER_TOURNAMENTS).strip());
			}

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
			paramSourceDataCSV = Optional.ofNullable(line.getOptionValue(PARAM_SOURCE_DATA_CSV)).map(String::strip);
			paramOutputCSV = Optional.ofNullable(line.getOptionValue(PARAM_OUTPUT_CSV)).map(String::strip);
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
		final double[][] clusters = paramSourceClustersCSV
				.map(sourceClustersFileName -> loadClusters(sourceClustersFileName))
				.orElseGet(() -> generateClusters(random, numClusters, minX, maxX, minY, maxY));
		logger.info("Found {} clusters", clusters.length);

		logger.info("Preparing data points");
		final double[][] data = paramSourceDataCSV.map(sourceDataCSVFileName -> {
			try {
				logger.info("Loading data points");
				return loadDataPoints(sourceDataCSVFileName);
			} catch (IOException e) {
				throw new RuntimeException("Could not load " + sourceDataCSVFileName, e);
			}
		}).orElseGet(() -> generateDataPoints(random, clusters, numDataPoints, radius));

		final String originalClustersFilename = "originalClusters.csv";
		if (paramSourceClustersCSV.isPresent()) {
			logger.info("Not persisting clusters since it was provided");
		} else {
			logger.info("Saving clusters to CSV: {}", originalClustersFilename);
			persistClusters(clusters, originalClustersFilename);
		}

		final String originalDataFilename = "originalData.csv";
		if (paramSourceDataCSV.isPresent()) {
			logger.info("Not persisting data since it was provided");
		} else {
			logger.info("Saving data to CSV: {}", originalDataFilename);
			persistDataPoints(clusters, data, originalDataFilename);
		}

		logger.info("Clustering data with Apache Commons Math");

		final List<CentroidCluster<LocationWrapper>> clusterResults = apacheCommonsMathCluster(clusters, data);

		logger.info("Definition of genetic problem");

		final int k = numClusters;
		final var fitnessFunction = computeFitness(numDataPoints, data, k);

		final var terminations = paramFixedTermination
				.map(maxGeneration -> Terminations.<Double>ofMaxGeneration(maxGeneration))
				.orElseGet(() -> or(Terminations.<Double>ofMaxGeneration(2_000), Terminations.ofStableFitness(100)));

		logger.info("Terminations: {}", paramFixedTermination);
		logger.info("Parameters: random_mutation_rate: {} - creep_mutation_rate: {} - creep_mutation_stddev: {} ",
				randomMutationRate,
				creepMutationRate,
				creepMutationStdDev);
		logger.info("Combinations: arithmetic {} ; crossover {}", combinationArithmetic, combinationCrossover);

		final var eaConfigurationBuilder = new EAConfiguration.Builder<Double>();
		eaConfigurationBuilder.chromosomeSpecs(DoubleChromosomeSpec.of(k * 2, min, max))
				.parentSelectionPolicy(Tournament.of(numberTournaments))
				.combinationPolicy(MultiCombinations.of(MultiPointArithmetic.of(combinationArithmetic, 0.5),
						MultiPointCrossover.of(combinationCrossover)))
				.mutationPolicies(MultiMutations.of(RandomMutation.of(randomMutationRate),
						CreepMutation.ofNormal(creepMutationRate, creepMutationMean, creepMutationStdDev)))
				.fitness(fitnessFunction)
				.termination(terminations);
		final var eaConfiguration = eaConfigurationBuilder.build();

		final var eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(populationSize)
				.addEvolutionListeners(EvolutionListeners.ofLogTopN(logger, 3),
						new CSVEvolutionListener.Builder<Double, Double>().filename(paramOutputCSV.orElse("output.csv"))
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
		logger.info("Best genotype: " + evolutionResult.bestGenotype());
		logger.info("  with fitness: {}", evolutionResult.bestFitness());
		logger.info("  at generation: {}", evolutionResult.generation());

		final Genotype bestGenotype = evolutionResult.bestGenotype();
		final double[][] bestPhenotype = toPhenotype(bestGenotype);
		logger.info("Best phenotype:");
		for (int i = 0; i < numClusters; i++) {
			logger.info("\tx: {} - y: {}", bestPhenotype[i][0], bestPhenotype[i][1]);
		}

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

		logger.info("Apache commons math output:");
		// output the clusters
		final double[] apacheClusters = new double[numClusters * 2];
		for (int i = 0; i < clusterResults.size(); i++) {
			final CentroidCluster<LocationWrapper> centroidCluster = clusterResults.get(i);
			logger.info("\t{}", centroidCluster.getCenter());
			apacheClusters[i * 2] = centroidCluster.getCenter().getPoint()[0];
			apacheClusters[i * 2 + 1] = centroidCluster.getCenter().getPoint()[1];
		}
		final var apacheFitness = fitnessFunction
				.compute(new Genotype(new DoubleChromosome(k * 2, -100.0d, 100.0d, apacheClusters)));
		logger.info("Apache fitness: {}", apacheFitness);

		logger.info("Done");
	}

}