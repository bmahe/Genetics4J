package net.bmahe.genetics4j.samples.clustering;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Fitness;

public class FitnessUtils {

	// tag::a_i[]
	private final static double a_i(final double[][] data, final double[][] distances,
			final Map<Integer, Set<Integer>> clusterToMembers, final int clusterIndex, final int i) {
		Validate.notNull(data);
		Validate.notNull(distances);
		Validate.notNull(clusterToMembers);

		final var members = clusterToMembers.get(clusterIndex);

		double sumDistances = 0.0;
		for (final int memberIndex : members) {
			if (memberIndex != i) {
				sumDistances += distances[i][memberIndex];
			}
		}

		return sumDistances / ((double) members.size() - 1.0d);
	}
	// end::a_i[]

	// tag::b_i[]
	private final static double b_i(final double[][] data, final double[][] distances,
			final Map<Integer, Set<Integer>> clusterToMembers, final int numClusters, final int clusterIndex,
			final int i) {
		Validate.notNull(data);
		Validate.notNull(distances);
		Validate.notNull(clusterToMembers);
		Validate.isTrue(numClusters > 0);
		Validate.inclusiveBetween(0, numClusters - 1, clusterIndex);

		double minMean = -1;
		for (int otherClusterIndex = 0; otherClusterIndex < numClusters; otherClusterIndex++) {

			if (otherClusterIndex != clusterIndex) {

				final var members = clusterToMembers.get(otherClusterIndex);

				if (members != null && members.size() > 0) {
					double sumDistances = 0.0;
					for (final int memberIndex : members) {
						sumDistances += distances[i][memberIndex];
					}

					final double meanDistance = sumDistances / members.size();

					if (minMean < 0 || meanDistance < minMean) {
						minMean = meanDistance;
					}
				}
			}
		}

		if (minMean < 0.0) {
			throw new IllegalStateException("Average min can't be negative. Missing clusters?");
		}
		return minMean;
	}
	// end::b_i[]

	public final static int[] assignDataToClusters(final double[][] data, double[][] distances,
			final double[][] clusters) {

		final double[] closestClusterDistance = new double[data.length];
		final int[] closestClusterIndex = new int[data.length];

		for (int i = 0; i < data.length; i++) {
			closestClusterIndex[i] = -1;

			final double dataX = data[i][0];
			final double dataY = data[i][1];

			for (int clusterIndex = 0; clusterIndex < clusters.length; clusterIndex++) {
				final double clusterX = clusters[clusterIndex][0];
				final double clusterY = clusters[clusterIndex][1];

				final double distance = Math
						.sqrt(((clusterX - dataX) * (clusterX - dataX)) + ((clusterY - dataY) * (clusterY - dataY)));

				if (closestClusterIndex[i] == -1 || distance < closestClusterDistance[i]) {
					closestClusterIndex[i] = clusterIndex;
					closestClusterDistance[i] = distance;
				}
			}
		}

		return closestClusterIndex;
	}

	public final static double computeSilhouetteScore(final double[][] data, double[][] distances, final int numClusters,
			final Map<Integer, Set<Integer>> clusterToMembers, final int[] closestClusterIndex, final int i) {

		final int clusterI = closestClusterIndex[i];

		double silhouetteScore = 0.0d;
		if (clusterToMembers.getOrDefault(clusterI, Set.of()).size() > 1) {
			final double ai = a_i(data, distances, clusterToMembers, clusterI, i);
			final double bi = b_i(data, distances, clusterToMembers, numClusters, clusterI, i);

			silhouetteScore = (bi - ai) / Math.max(ai, bi);
		}

		return silhouetteScore;
	}

	public final static double computeSumSquaredErrors(final double[][] data, double[][] distances,
			final double[][] clusters, final Map<Integer, Set<Integer>> clusterToMembers,
			final int[] closestClusterIndex) {

		double sumSquareErrors = 0.0d;
		for (int i = 0; i < data.length; i++) {
			final double[] cluster = clusters[closestClusterIndex[i]];

			sumSquareErrors += (cluster[0] - data[i][0]) * (cluster[0] - data[i][0]);
			sumSquareErrors += (cluster[1] - data[i][1]) * (cluster[1] - data[i][1]);
		}

		return sumSquareErrors;
	}

	// tag::fitness[]
	public final static Fitness<Double> computeFitness(final int numDataPoints, final double[][] data,
			double[][] distances, final int numClusters) {
		Validate.notNull(data);
		Validate.notNull(distances);
		Validate.isTrue(numDataPoints > 0);
		Validate.isTrue(numDataPoints == data.length);
		Validate.isTrue(numDataPoints == distances.length);
		Validate.isTrue(numClusters > 0);

		return (genoType) -> {

			final double[][] clusters = PhenotypeUtils.toPhenotype(genoType);

			final int[] closestClusterIndex = assignDataToClusters(data, distances, clusters);

			final Map<Integer, Set<Integer>> clusterToMembers = new HashMap<>();

			for (int i = 0; i < numDataPoints; i++) {
				final var members = clusterToMembers.computeIfAbsent(closestClusterIndex[i], k -> new HashSet<>());
				members.add(i);
			}

			double sum_si = 0.0;
			for (int i = 0; i < numDataPoints; i++) {
				sum_si += computeSilhouetteScore(data, distances, numClusters, clusterToMembers, closestClusterIndex, i);
			}

			return sum_si;
		};
	}
	// end::fitness[]

	// Copy/pasted for the Clustering doc
	// tag::fitness_with_sse[]
	public final static Fitness<Double> computeFitnessWithSSE(final int numDataPoints, final double[][] data,
			double[][] distances, final int numClusters) {
		Validate.notNull(data);
		Validate.notNull(distances);
		Validate.isTrue(numDataPoints > 0);
		Validate.isTrue(numDataPoints == data.length);
		Validate.isTrue(numDataPoints == distances.length);
		Validate.isTrue(numClusters > 0);

		return (genoType) -> {

			final double[][] clusters = PhenotypeUtils.toPhenotype(genoType);

			final int[] closestClusterIndex = assignDataToClusters(data, distances, clusters);

			final Map<Integer, Set<Integer>> clusterToMembers = new HashMap<>();

			for (int i = 0; i < numDataPoints; i++) {
				final var members = clusterToMembers.computeIfAbsent(closestClusterIndex[i], k -> new HashSet<>());
				members.add(i);
			}

			double sum_si = 0.0;
			for (int i = 0; i < numDataPoints; i++) {
				sum_si += computeSilhouetteScore(data, distances, numClusters, clusterToMembers, closestClusterIndex, i);
			}

			final double sumSquaredError = computeSumSquaredErrors(data,
					distances,
					clusters,
					clusterToMembers,
					closestClusterIndex);

			return sum_si + (1.0 / sumSquaredError);
		};
	}
	// end::fitness_with_sse[]

}