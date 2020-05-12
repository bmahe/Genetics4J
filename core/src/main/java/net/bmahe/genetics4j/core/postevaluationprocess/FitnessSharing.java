package net.bmahe.genetics4j.core.postevaluationprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;

@Value.Immutable
public abstract class FitnessSharing implements Function<Population<Double>, Population<Double>> {

	@Value.Parameter
	public abstract BiFunction<Genotype, Genotype, Double> distance();

	@Value.Parameter
	public abstract Function<Double, Double> sharing();

	@Override
	public Population<Double> apply(final Population<Double> population) {
		Validate.notNull(population);

		final List<Double> newFitness = new ArrayList<>();
		for (int i = 0; i < population.getAllGenotypes().size(); i++) {
			final Genotype genotypeI = population.getGenotype(i);
			final Double fitnessI = population.getFitness(i);

			double sumSharing = 0.0d;
			for (int j = 0; j < population.getAllGenotypes().size(); j++) {
				final Genotype genotypeJ = population.getGenotype(j);

				final double distance = distance().apply(genotypeI, genotypeJ);
				final double sharing = sharing().apply(distance);
				sumSharing += sharing;
			}

			final double newFitnessI = fitnessI / sumSharing;
			newFitness.add(newFitnessI);
		}

		return Population.of(population.getAllGenotypes(), newFitness);
	}

	public static FitnessSharing of(final BiFunction<Genotype, Genotype, Double> distance,
			final Function<Double, Double> sharing) {
		return ImmutableFitnessSharing.of(distance, sharing);
	}

	public static FitnessSharing ofStandard(final BiFunction<Genotype, Genotype, Double> distance, final double sigma) {
		return ImmutableFitnessSharing.of(distance, (d) -> {
			if (d < 0.0 || d > sigma) {
				return 0.0;
			}

			return 1 - d / sigma;
		});
	}

	public static FitnessSharing ofStandard(final BiFunction<Genotype, Genotype, Double> distance, final double sigma,
			final double alpha) {
		return ImmutableFitnessSharing.of(distance, (d) -> {
			if (d < 0.0 || d > sigma) {
				return 0.0;
			}

			return 1 - Math.pow(d / sigma, alpha);
		});
	}
}