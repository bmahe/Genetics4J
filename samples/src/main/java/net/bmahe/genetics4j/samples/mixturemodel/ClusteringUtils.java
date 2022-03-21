package net.bmahe.genetics4j.samples.mixturemodel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.moo.FitnessVector;

public class ClusteringUtils {
	final static public Logger logger = LogManager.getLogger(ClusteringUtils.class);

	public static int[] assignClustersDoubleChromosome(final int distributionNumParameters, final double[][] samples,
			final Genotype genotype) {

		final var fChromosome = genotype.getChromosome(0, DoubleChromosome.class);
		final int[] clusters = new int[samples.length];
		final double[] bestProb = new double[samples.length];

		for (int c = 0; c < clusters.length; c++) {
			clusters[c] = 0;
			bestProb[c] = Double.MIN_VALUE;
		}

		double sumAlpha = 0.0f;
		int k = 0;
		while (k < fChromosome.getSize()) {
			sumAlpha += fChromosome.getAllele(k);
			k += distributionNumParameters;
		}

		int i = 0;
		int clusterIndex = 0;
		while (i < fChromosome.getSize()) {

			final double alpha = fChromosome.getAllele(i) / sumAlpha;
			final double[] mean = new double[] { fChromosome.getAllele(i + 1), fChromosome.getAllele(i + 2) };
			final double[][] covariance = new double[][] {
					{ fChromosome.getAllele(i + 3) - 15, fChromosome.getAllele(i + 4) - 15 },
					{ fChromosome.getAllele(i + 4) - 15, fChromosome.getAllele(i + 5) - 15 } };

			try {
				final var multivariateNormalDistribution = new MultivariateNormalDistribution(mean, covariance);

				for (int j = 0; j < samples.length; j++) {
					float likelyhood = (float) (alpha * multivariateNormalDistribution.density(samples[j]));

					if (clusters[j] < 0 || bestProb[j] < likelyhood) {
						bestProb[j] = likelyhood;
						clusters[j] = clusterIndex;
					}
				}
			} catch (NonPositiveDefiniteMatrixException | SingularMatrixException | MathUnsupportedOperationException e) {
			}

			i += distributionNumParameters;
			clusterIndex++;
		}

		return clusters;
	}

	public static int[] assignClustersFloatChromosome(final int distributionNumParameters, final double[][] samples,
			final Genotype genotype) {

		final var fChromosome = genotype.getChromosome(0, FloatChromosome.class);
		final int[] clusters = new int[samples.length];
		final double[] bestProb = new double[samples.length];

		for (int c = 0; c < clusters.length; c++) {
			clusters[c] = 0;
			bestProb[c] = Double.MIN_VALUE;
		}

		double sumAlpha = 0.0f;
		int k = 0;
		while (k < fChromosome.getSize()) {
			sumAlpha += fChromosome.getAllele(k);
			k += distributionNumParameters;
		}

		int i = 0;
		int clusterIndex = 0;
		while (i < fChromosome.getSize()) {

			final double alpha = fChromosome.getAllele(i) / sumAlpha;
			final double[] mean = new double[] { fChromosome.getAllele(i + 1), fChromosome.getAllele(i + 2) };
			final double[][] covariance = new double[][] {
					{ fChromosome.getAllele(i + 3) - 15, fChromosome.getAllele(i + 4) - 15 },
					{ fChromosome.getAllele(i + 4) - 15, fChromosome.getAllele(i + 5) - 15 } };

			try {
				final var multivariateNormalDistribution = new MultivariateNormalDistribution(mean, covariance);

				for (int j = 0; j < samples.length; j++) {
					float likelyhood = (float) (alpha * multivariateNormalDistribution.density(samples[j]));

					if (clusters[j] < 0 || bestProb[j] < likelyhood) {
						bestProb[j] = likelyhood;
						clusters[j] = clusterIndex;
					}
				}
			} catch (NonPositiveDefiniteMatrixException | SingularMatrixException | MathUnsupportedOperationException e) {
			}

			i += distributionNumParameters;
			clusterIndex++;
		}

		return clusters;
	}

	public static void persistClusters(final float[] x, final float[] y, final int[] cluster, final String filename)
			throws IOException {
		Validate.isTrue(x.length == y.length);
		Validate.isTrue(x.length == cluster.length);
		logger.info("Saving clusters to CSV: {}", filename);

		final CSVPrinter csvPrinter;
		try {
			csvPrinter = CSVFormat.DEFAULT.withAutoFlush(true)
					.withHeader(new String[] { "cluster", "x", "y" })
					.print(Path.of(filename), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Could not open {}", filename, e);
			throw new RuntimeException("Could not open file " + filename, e);
		}

		for (int i = 0; i < cluster.length; i++) {
			try {
				csvPrinter.printRecord(cluster[i], x[i], y[i]);
			} catch (IOException e) {
				throw new RuntimeException("Could not write data", e);
			}
		}
		csvPrinter.close(true);
	}

	// TODO fix duplication
	public static void persistClusters(final double[] x, final double[] y, final int[] cluster, final String filename)
			throws IOException {
		Validate.isTrue(x.length == y.length);
		Validate.isTrue(x.length == cluster.length);
		logger.info("Saving clusters to CSV: {}", filename);

		final CSVPrinter csvPrinter;
		try {
			csvPrinter = CSVFormat.DEFAULT.withAutoFlush(true)
					.withHeader(new String[] { "cluster", "x", "y" })
					.print(Path.of(filename), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Could not open {}", filename, e);
			throw new RuntimeException("Could not open file " + filename, e);
		}

		for (int i = 0; i < cluster.length; i++) {
			try {
				csvPrinter.printRecord(cluster[i], x[i], y[i]);
			} catch (IOException e) {
				throw new RuntimeException("Could not write data", e);
			}
		}
		csvPrinter.close(true);
	}

	public static Map<Integer, Individual<FitnessVector<Float>>> groupByNumClusters(final double[][] samplesDouble,
			final EvolutionResult<FitnessVector<Float>> evolutionResult) {
		Validate.notEmpty(samplesDouble);
		Validate.notNull(evolutionResult);

		final Map<Integer, Individual<FitnessVector<Float>>> groups = new TreeMap<>();

		final var listFitnessResult = evolutionResult.fitness();
		final var populationResult = evolutionResult.population();

		for (int i = 0; i < populationResult.size(); i++) {

			final var genotype = populationResult.get(i);
			final var fitness = listFitnessResult.get(i);

			groups.compute(Math.round(fitness.get(1)),
					(k, currentBestIndividual) -> currentBestIndividual == null || currentBestIndividual.fitness()
							.get(0) < fitness.get(0) ? Individual.of(genotype, fitness) : currentBestIndividual);
		}

		return groups;
	}

	public static void categorizeByNumClusters(final int distributionNumParameters, final int maxPossibleDistributions,
			final float[] x, final float[] y, final double[][] samplesDouble,
			final EvolutionResult<FitnessVector<Float>> evolutionResult, final String baseDir, final String type)
			throws IOException {
		Validate.notEmpty(samplesDouble);
		Validate.notNull(evolutionResult);
		Validate.notBlank(baseDir);
		Validate.notBlank(type);

		final var groupedByNumClusters = groupByNumClusters(samplesDouble, evolutionResult);
		logger.info("Groups:");
		for (Entry<Integer, Individual<FitnessVector<Float>>> entry : groupedByNumClusters.entrySet()) {
			final int numUnusedClusters = entry.getKey();
			final var individual = entry.getValue();

			final int numClusters = maxPossibleDistributions - numUnusedClusters;

			logger.info("\tNum Clusters: {} - Unused Clusters: {} - Fitness: {}",
					numClusters,
					numUnusedClusters,
					individual.fitness());

			final int[] assignedClusters = ClusteringUtils
					.assignClustersFloatChromosome(distributionNumParameters, samplesDouble, individual.genotype());
			final Set<Integer> uniqueAssigned = new HashSet<>();
			uniqueAssigned.addAll(IntStream.of(assignedClusters)
					.boxed()
					.toList());

			ClusteringUtils.persistClusters(x,
					y,
					assignedClusters,
					baseDir + "assigned-" + type + "-" + uniqueAssigned.size() + ".csv");
		}
	}

	public static void writeCSVReferenceValue(final String filename, final int generations, final Number value)
			throws IOException {
		Validate.notBlank(filename);
		Validate.isTrue(generations > 0);
		Validate.notNull(value);

		final var csvPrinter = CSVFormat.DEFAULT.withAutoFlush(true)
				.withHeader("generation", "fitness")
				.print(Path.of(filename), StandardCharsets.UTF_8);

		for (int i = 0; i < generations; i++) {
			csvPrinter.printRecord(i, value);
		}
		csvPrinter.close();
	}
}