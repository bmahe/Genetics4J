package net.bmahe.genetics4j.core.postevaluationprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.Population;

@Value.Immutable
public abstract class FitnessSharing<T extends Comparable<T>> implements Function<Population<T>, Population<T>> {

	@Value.Parameter
	public abstract BiFunction<Genotype, Genotype, Double> distance();

	@Value.Parameter
	public abstract Function<Double, Double> sharing();

	@Value.Parameter
	public abstract BiFunction<Individual<T>, Double, T> scaleFitness();

	@Override
	public Population<T> apply(final Population<T> population) {
		Validate.notNull(population);

		if (population.isEmpty()) {
			return population;
		}

		final List<T> newFitness = new ArrayList<>();
		for (int i = 0; i < population.size(); i++) {
			final Genotype genotypeI = population.getGenotype(i);
			final Individual<T> individual = population.getIndividual(i);

			double sumSharing = 0.0d;
			for (int j = 0; j < population.size(); j++) {
				final Genotype genotypeJ = population.getGenotype(j);

				final double distance = distance().apply(genotypeI, genotypeJ);
				final double sharing = sharing().apply(distance);
				sumSharing += sharing;
			}

			final T newFitnessI = scaleFitness().apply(individual, sumSharing);
			newFitness.add(newFitnessI);
		}

		return Population.<T>of(population.getAllGenotypes(), newFitness);
	}

	public static FitnessSharing<Double> of(final BiFunction<Genotype, Genotype, Double> distance,
			final Function<Double, Double> sharing) {
		return ImmutableFitnessSharing
				.of(distance, sharing, (individual, sumSharing) -> individual.fitness() / sumSharing);
	}

	public static FitnessSharing<Double> ofStandard(final BiFunction<Genotype, Genotype, Double> distance,
			final double sigma) {
		return FitnessSharing.of(distance, (d) -> {
			if (d < 0.0 || d > sigma) {
				return 0.0;
			}

			return 1 - d / sigma;
		});
	}

	public static FitnessSharing<Double> ofStandard(final BiFunction<Genotype, Genotype, Double> distance,
			final double sigma, final double alpha) {
		return FitnessSharing.of(distance, (d) -> {
			if (d < 0.0 || d > sigma) {
				return 0.0;
			}

			return 1 - Math.pow(d / sigma, alpha);
		});
	}

	public static FitnessSharing<Float> ofFloatFitness(final BiFunction<Genotype, Genotype, Double> distance,
			final Function<Double, Double> sharing) {
		return ImmutableFitnessSharing
				.of(distance, sharing, (individual, sumSharing) -> (float) (individual.fitness() / sumSharing));
	}
}