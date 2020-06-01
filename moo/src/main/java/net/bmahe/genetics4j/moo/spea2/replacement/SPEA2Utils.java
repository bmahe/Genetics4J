package net.bmahe.genetics4j.moo.spea2.replacement;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import net.bmahe.genetics4j.core.Population;

public class SPEA2Utils {

	private SPEA2Utils() {
	}

	public static <T extends Comparable<T>> int strength(final Comparator<T> dominance, final int index,
			final T fitness, final Population<T> population) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < population.size());

		Validate.notNull(fitness);
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);

		int dominatedCount = 0;

		for (int j = 0; j < population.size(); j++) {
			final T fitnessJ = population.getFitness(j);

			if (dominance.compare(fitness, fitnessJ) > 0) {
				dominatedCount++;
			}
		}

		return dominatedCount;
	}

	public static <T extends Comparable<T>> int rawFitness(final Comparator<T> dominance, final double[] strengths,
			final int index, final T fitness, final Population<T> population) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < population.size());

		Validate.notNull(strengths);
		Validate.notNull(fitness);
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);
		Validate.isTrue(population.size() == strengths.length);

		int rawFitness = 0;

		for (int j = 0; j < population.size(); j++) {
			final T fitnessJ = population.getFitness(j);

			if (index != j) {
				if (dominance.compare(fitness, fitnessJ) < 0) {
					rawFitness += strengths[j];
				}
			}
		}

		return rawFitness;
	}

	public static <T extends Comparable<T>> List<Pair<Integer, Double>> kthDistances(
			final double[][] distanceObjectives, final int index, final T fitness,
			final Population<T> combinedPopulation) {
		Validate.notNull(distanceObjectives);
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < combinedPopulation.size());

		Validate.notNull(fitness);
		Validate.notNull(combinedPopulation);
		Validate.isTrue(combinedPopulation.size() > 0);

		return IntStream.range(0, combinedPopulation.size())
				.boxed()
				.sorted((a, b) -> Double.compare(distanceObjectives[index][a], distanceObjectives[index][b]))
				.map(i -> ImmutablePair.of(i, distanceObjectives[index][i]))
				.collect(Collectors.toList());

	}

}