package net.bmahe.genetics4j.moo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

public class ParetoUtils {

	private ParetoUtils() {

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
}