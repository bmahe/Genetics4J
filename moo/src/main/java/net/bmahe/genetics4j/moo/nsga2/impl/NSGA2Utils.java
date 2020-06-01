package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.moo.ObjectiveDistance;

public class NSGA2Utils {

	private NSGA2Utils() {

	}

	public static <T> double[] crowdingDistanceAssignment(final int numberObjectives, final List<T> fitnessScore,
			final Function<Integer, Comparator<T>> objectiveComparator, final ObjectiveDistance<T> objectiveDistance) {
		Validate.isTrue(numberObjectives > 0);
		Validate.notNull(fitnessScore);
		Validate.isTrue(fitnessScore.isEmpty() == false);
		Validate.notNull(objectiveComparator);
		Validate.notNull(objectiveDistance);

		final double[] distances = new double[fitnessScore.size()];

		for (int m = 0; m < numberObjectives; m++) {
			final Comparator<T> objective = objectiveComparator.apply(m);

			final int[] sortedIndexes = IntStream.range(0, fitnessScore.size())
					.boxed()
					.sorted((a, b) -> objective.compare(fitnessScore.get(a), fitnessScore.get(b)))
					.mapToInt((e) -> e)
					.toArray();

			distances[sortedIndexes[0]] = Double.POSITIVE_INFINITY;
			distances[sortedIndexes[sortedIndexes.length - 1]] = Double.POSITIVE_INFINITY;

			final T minFitnessByObjective = fitnessScore.get(sortedIndexes[0]);
			final T maxFitnessByObjective = fitnessScore.get(sortedIndexes[sortedIndexes.length - 1]);
			final double maxDistance = objectiveDistance.distance(minFitnessByObjective, maxFitnessByObjective, m);

			for (int i = 1; i < sortedIndexes.length - 1; i++) {
				final T previousFitness = fitnessScore.get(sortedIndexes[i - 1]);
				final T nextFitness = fitnessScore.get(sortedIndexes[i + 1]);

				if (maxDistance > 0.0) {
					distances[i] += objectiveDistance.distance(previousFitness, nextFitness, m) / maxDistance;
				}
			}
		}

		return distances;
	}
}