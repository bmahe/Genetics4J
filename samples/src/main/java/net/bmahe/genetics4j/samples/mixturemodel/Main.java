package net.bmahe.genetics4j.samples.mixturemodel;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.distribution.MixtureMultivariateNormalDistribution;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.fitting.MultivariateNormalMixtureExpectationMaximization;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.samples.CLIUtils;

public class Main {
	public static final Logger logger = LogManager.getLogger(Main.class);

	public static final String OPT_BASE_DIR = "base-directory";

	public static final int NUM_POINTS_PER_DISTRIBUTION = 2_000;
	public static final int MIN_NUM_DISTRIBUTIONS = 3;
	public static final int MAX_NUM_DISTRIBUTIONS = 6;

	public static final float MIN_MEAN = 0f;
	public static final float MAX_MEAN = 30f;

	public static final float MIN_STD_DEV = 0.5f;
	public static final float MAX_STD_DEV = 5f;

	public static final int DISTRIBUTION_NUM_PARAMETERS = 6;

	public static final int MAX_GENERATIONS = 1_000;

	public static final double EPSILON = 0.0001;

	public static final Comparator<Genotype> deduplicator = (a, b) -> {
		final var aChromosome = a.getChromosome(0, FloatChromosome.class);
		final var bChromosome = b.getChromosome(0, FloatChromosome.class);

		if (aChromosome.getSize() != bChromosome.getSize()) {
			return Integer.compare(a.getSize(), b.getSize());
		}

		for (int i = 0; i < aChromosome.getSize(); i++) {
			final var alleleA = aChromosome.getAllele(i);
			final var alleleB = bChromosome.getAllele(i);

			final var diff = alleleB - alleleA;
			if (diff > EPSILON) {
				return 1;
			} else if (diff < -EPSILON) {
				return -1;
			}
		}

		return 0;
	};

	public static Genotype toGenotype(final int maxPossibleDistributions,
			final MixtureMultivariateNormalDistribution mixtureMultivariateNormalDistribution) {
		Validate.notNull(mixtureMultivariateNormalDistribution);

		final float[] values = new float[maxPossibleDistributions * DISTRIBUTION_NUM_PARAMETERS];

		int i = 0;
		final var components = mixtureMultivariateNormalDistribution.getComponents();
		Validate.isTrue(components.size() <= maxPossibleDistributions);

		for (final Pair<Double, MultivariateNormalDistribution> component : components) {

			final var alpha = component.getFirst();
			final var multivariateNormalDistribution = component.getSecond();

			final double[] means = multivariateNormalDistribution.getMeans();
			final RealMatrix covariances = multivariateNormalDistribution.getCovariances();

			values[i + 0] = alpha.floatValue();
			values[i + 1] = (float) means[0];
			values[i + 2] = (float) means[1];
			values[i + 3] = (float) covariances.getEntry(0, 0) + 15;
			values[i + 4] = (float) covariances.getEntry(0, 1) + 15;
			values[i + 5] = (float) covariances.getEntry(1, 1) + 15;

			i += DISTRIBUTION_NUM_PARAMETERS;
		}

		return new Genotype(
				new FloatChromosome(maxPossibleDistributions * DISTRIBUTION_NUM_PARAMETERS, 0, MAX_MEAN, values));
	}

	public static void main(String[] args) throws IOException {
		logger.info("Starting");

		/**
		 * Parse CLI
		 */
		logger.info("Parsing command line arguments");
		final CommandLineParser parser = new DefaultParser();

		final Options options = new Options();
		options.addOption("h", "help", false, "print help");
		options.addOption(null, "num-clusters", true, "number of clusters");
		options.addOption(null, OPT_BASE_DIR, true, "base directory for output files");

		String baseDir = "./";
		try {
			final CommandLine line = parser.parse(options, args);
			if (line.hasOption("h")) {
				CLIUtils.cliHelpAndExit(logger, Main.class, options, null);
			}

			if (line.hasOption(OPT_BASE_DIR)) {
				baseDir = line.getOptionValue(OPT_BASE_DIR);
				if (baseDir.endsWith("/") == false) {
					baseDir = baseDir + "/";
				}
			}
		} catch (ParseException exp) {
			CLIUtils.cliHelpAndExit(logger, Main.class, options, "Unexpected exception:" + exp.getMessage());

			// This piece will never execute
			throw new RuntimeException(); // java doesn't detect the System.exit in cliError and create some issues with
													// potential not initialized final parameters.
		}

		logger.info("Using base directory for files: {}", baseDir);
		FileUtils.forceMkdir(new File(baseDir));

		logger.info("Starting process");
		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		for (int i = 0; i < 100; i++) {
			randomGenerator.nextInt();
		}

		// tag::data_generation[]
		final int maxPossibleDistributions = MAX_NUM_DISTRIBUTIONS - 1;
		logger.info("Generating dataset");
		final int numDistributions = randomGenerator.nextInt(MIN_NUM_DISTRIBUTIONS, MAX_NUM_DISTRIBUTIONS);
		logger.info("Generating {} distributions:", numDistributions);

		final double[][] means = new double[numDistributions][2];
		final double[][][] covariances = new double[numDistributions][2][2];

		final int[] cluster = new int[NUM_POINTS_PER_DISTRIBUTION * numDistributions];
		final float[][] samples = new float[NUM_POINTS_PER_DISTRIBUTION * numDistributions][2];
		final double[][] samplesDouble = new double[NUM_POINTS_PER_DISTRIBUTION * numDistributions][2];
		for (int i = 0; i < numDistributions; i++) {
			means[i] = new double[] { randomGenerator.nextDouble(MIN_MEAN, MAX_MEAN),
					randomGenerator.nextDouble(MIN_MEAN, MAX_MEAN) };

			/**
			 * See
			 * https://stackoverflow.com/questions/619335/a-simple-algorithm-for-generating-positive-semidefinite-matrices
			 */
			final double[][] randomMatrix = new double[][] {
					{ randomGenerator.nextDouble(0, 4) - 2, randomGenerator.nextDouble(0, 4) - 2 },
					{ randomGenerator.nextDouble(0, 4) - 2, randomGenerator.nextDouble(0, 4) - 2 } };
			final double[][] covariance = new double[][] {
					{ randomMatrix[0][0] * randomMatrix[0][0] + randomMatrix[0][1] * randomMatrix[0][1],
							randomMatrix[0][0] * randomMatrix[1][0] + randomMatrix[0][1] * randomMatrix[1][1] },
					{ randomMatrix[0][0] * randomMatrix[1][0] + randomMatrix[0][1] * randomMatrix[1][1],
							randomMatrix[1][0] * randomMatrix[1][0] + randomMatrix[1][1] * randomMatrix[1][1] } };
			covariances[i] = covariance;

			logger.info("\t index: {} - mean: {} - covariance: {}", i, means[i], covariance);
			final var multivariateNormalDistribution = new MultivariateNormalDistribution(means[i], covariance);
			for (int j = 0; j < 5; j++) {
				final var sample = multivariateNormalDistribution.sample();
				logger.info("\t\t{} - {}", sample, multivariateNormalDistribution.density(sample));
			}
			for (int j = 0; j < NUM_POINTS_PER_DISTRIBUTION; j++) {
				final var sample = multivariateNormalDistribution.sample();
				samples[i * NUM_POINTS_PER_DISTRIBUTION + j] = new float[] { (float) sample[0], (float) sample[1] };
				samplesDouble[i * NUM_POINTS_PER_DISTRIBUTION + j] = sample;
				cluster[i * NUM_POINTS_PER_DISTRIBUTION + j] = i;
			}
		}

		final float[] x = new float[NUM_POINTS_PER_DISTRIBUTION * numDistributions];
		final float[] y = new float[NUM_POINTS_PER_DISTRIBUTION * numDistributions];
		for (int i = 0; i < NUM_POINTS_PER_DISTRIBUTION * numDistributions; i++) {
			x[i] = samples[i][0];
			y[i] = samples[i][1];
		}

		ClusteringUtils.persistClusters(x, y, cluster, baseDir + "original.csv");
		// end::data_generation[]

		final var singleObjectiveMethod = new SingleObjectiveMethod(DISTRIBUTION_NUM_PARAMETERS,
				baseDir,
				MAX_GENERATIONS);
		singleObjectiveMethod.run(maxPossibleDistributions, samplesDouble, x, y, baseDir, List.of());

		final var mooCPU = new MooCPU(DISTRIBUTION_NUM_PARAMETERS, deduplicator, baseDir, MAX_GENERATIONS);
		final long startCPU = System.currentTimeMillis();
		final var bestCPUResult = mooCPU.run(maxPossibleDistributions, samplesDouble, x, y, "GA", List.of());
		final long endCPU = System.currentTimeMillis();
		final long durationCPUMs = endCPU - startCPU;

		final var mooGPU = new MooGPU(DISTRIBUTION_NUM_PARAMETERS, deduplicator, baseDir, MAX_GENERATIONS);
		final long startGPU = System.currentTimeMillis();
		mooGPU.run(maxPossibleDistributions,
				numDistributions,
				samplesDouble,
				samples,
				x,
				y,
				"mixturemodel-moo-gpu",
				List.of(),
				bestCPUResult);
		final long endGPU = System.currentTimeMillis();
		final long durationGPUMs = endGPU - startGPU;

		logger.info("Duration CPU: {} s - Duration GPU: {} s", durationCPUMs / 1000, durationGPUMs / 1000);

		// tag::commons-math[]
		logger.info("Evaluating with commons math");
		final var mmmmm = new MultivariateNormalMixtureExpectationMaximization(samplesDouble);

		final var initialEstimatedDistribution = MultivariateNormalMixtureExpectationMaximization.estimate(samplesDouble,
				numDistributions);
		mmmmm.fit(initialEstimatedDistribution);
		final var mixtureMultivariateNormalDistribution = mmmmm.getFittedModel();
		final var components = mixtureMultivariateNormalDistribution.getComponents();
		final var fittedCommonsMathGenotype = toGenotype(maxPossibleDistributions, mixtureMultivariateNormalDistribution);
		// end::commons-math[]

		logger.info("GPU run with best seeds from commons math results");
		mooGPU.run(maxPossibleDistributions,
				numDistributions,
				samplesDouble,
				samples,
				x,
				y,
				"mixturemodel-moo-gpu-seed",
				List.of(fittedCommonsMathGenotype),
				bestCPUResult);

		logger.info("Best results from apache commons math:");
		final int[] mnmeAssigned = new int[samples.length];
		for (int i = 0; i < samples.length; i++) {
			double bestScore = Double.NEGATIVE_INFINITY;

			for (int c = 0; c < components.size(); c++) {
				final var component = components.get(c);
				final var alpha = component.getFirst();
				final var distribution = component.getSecond();

				final var score = alpha * distribution.density(samplesDouble[i]);
				if (score > bestScore) {
					bestScore = score;
					mnmeAssigned[i] = c;
				}
			}
		}

		logger.info("Components:");
		for (int c = 0; c < components.size(); c++) {
			final var component = components.get(c);
			final var alpha = component.getFirst();
			final var distribution = component.getSecond();

			logger.debug("\talpha: {} - means: {} - covariances: {}",
					alpha,
					distribution.getMeans(),
					distribution.getCovariances());
		}

		logger.info("Commons-math: log likelyhood: {}", mmmmm.getLogLikelihood());
		ClusteringUtils.persistClusters(x, y, mnmeAssigned, baseDir + "assigned-commons-math.csv");

		final var fitnessCPUFunc = mooCPU.fitnessCPU(maxPossibleDistributions, samplesDouble);
		final var mnmeFitness = fitnessCPUFunc.compute(fittedCommonsMathGenotype);

		ClusteringUtils.writeCSVReferenceValue(baseDir + "commons-math.csv", MAX_GENERATIONS, mnmeFitness.get(0));
		logger.info("Score of the Commons Math results: {}, comparing to {}", mnmeFitness, mmmmm.getLogLikelihood());
		logger.info("Best CPU evolution: {}", bestCPUResult.bestFitness());
	}
}