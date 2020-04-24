package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.moo.nsga2.spec.ObjectiveDistance;

public class NSGA2Utils {

	private NSGA2Utils() {

	}

	public static <T> List<Set<Integer>> rankedPopulation(final Comparator<T> dominance, final List<T> fitnessScore) {
		Validate.notNull(dominance);
		Validate.notNull(fitnessScore);
		Validate.isTrue(fitnessScore.isEmpty() == false);

		final Map<Integer, Set<Integer>> dominating = new HashMap<>();
		final Map<Integer, Integer> dominatedCount = new HashMap<>();

		final List<Set<Integer>> rankedPopulation = new ArrayList<>();
		rankedPopulation.add(new HashSet<>());
		final Set<Integer> firstFront = rankedPopulation.get(0);

		for (int i = 0; i < fitnessScore.size(); i++) {

			final T individualFitness = fitnessScore.get(i);
			int dominated = 0;

			for (int otherIndex = 0; otherIndex < fitnessScore.size(); otherIndex++) {

				final T otherFitness = fitnessScore.get(otherIndex);

				final int comparison = dominance.compare(individualFitness, otherFitness);
				if (comparison > 0) {
					dominating.computeIfAbsent(i, (k) -> new HashSet<>());
					dominating.get(i).add(otherIndex);
				} else if (comparison < 0) {
					dominated++;
				}
			}
			dominatedCount.put(i, dominated);

			// it dominates everything -> it is part of the first front
			if (dominated == 0) {
				firstFront.add(i);
			}
		}

		int frontIndex = 0;
		while (frontIndex < rankedPopulation.size() && rankedPopulation.get(frontIndex).isEmpty() == false) {
			final Set<Integer> currentFront = rankedPopulation.get(frontIndex);

			final Set<Integer> nextFront = new HashSet<>();

			for (final int i : currentFront) {
				if (dominating.containsKey(i)) {
					for (final Integer dominatedByI : dominating.get(i)) {
						final Integer updatedDominatedCount = dominatedCount.computeIfPresent(dominatedByI,
								(k, v) -> v - 1);

						if (updatedDominatedCount != null && updatedDominatedCount == 0) {
							nextFront.add(dominatedByI);
						}
					}

				}
			}

			rankedPopulation.add(nextFront);
			frontIndex++;
		}

		return rankedPopulation;
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
				distances[i] += objectiveDistance.distance(previousFitness, nextFitness, m) / maxDistance;
			}
		}

		return distances;
	}
}