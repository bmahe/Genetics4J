package net.bmahe.genetics4j.samples.clustering;

import net.bmahe.genetics4j.core.postevaluationprocess.FitnessSharing;

public class FitnessSharingUtils {

	// tag::fitness_sharing[]
	public final static FitnessSharing clusterDistance = FitnessSharing.ofStandard((i0, i1) -> {
		final var cluster0 = PhenotypeUtils.toPhenotype(i0);
		final var cluster1 = PhenotypeUtils.toPhenotype(i1);

		double distanceAcc = 0.0d;
		for (int i = 0; i < cluster0.length; i++) {
			double distance = 0.0d;
			distance += (cluster1[i][0] - cluster0[i][0]) * (cluster1[i][0] - cluster0[i][0]);
			distance += (cluster1[i][1] - cluster0[i][1]) * (cluster1[i][1] - cluster0[i][1]);

			distanceAcc += Math.sqrt(distance);
		}

		return distanceAcc;
	}, 5.0);
	// end::fitness_sharing[]

	public final static FitnessSharing hausDorffFitnessSharing = FitnessSharing.ofStandard((i0, i1) -> {
		final var p0 = PhenotypeUtils.toPhenotype(i0);
		final var p1 = PhenotypeUtils.toPhenotype(i1);

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

}