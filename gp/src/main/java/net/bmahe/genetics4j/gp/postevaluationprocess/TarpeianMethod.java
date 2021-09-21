package net.bmahe.genetics4j.gp.postevaluationprocess;

import java.util.function.Function;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;

@Value.Immutable
public abstract class TarpeianMethod implements Function<Population<Double>, Population<Double>> {

	@Value.Parameter
	public abstract RandomGenerator randomGenerator();

	@Value.Parameter
	public abstract Function<Genotype, Integer> sizeFunction();

	@Value.Parameter
	public abstract double probability();

	@Value.Parameter
	public abstract double newValue();

	@Value.Check
	protected void check() {
		Validate.inclusiveBetween(0.0d, 1.0d, probability());
	}

	@Override
	public Population<Double> apply(final Population<Double> population) {
		Validate.notNull(population);

		if (population.isEmpty()) {
			return population;
		}

		final double averageSize = population.getAllGenotypes()
				.stream()
				.map(genotype -> sizeFunction().apply(genotype))
				.mapToInt(i -> i)
				.average()
				.getAsDouble();

		final Population<Double> newPopulation = new Population<>();
		for (int i = 0; i < population.size(); i++) {

			final Genotype genotype = population.getGenotype(i);
			final int size = sizeFunction().apply(genotype);

			double fitness = population.getFitness(i);
			if (size > averageSize && randomGenerator().nextDouble() < probability()) {
				fitness = newValue();
			}

			newPopulation.add(genotype, fitness);
		}

		return newPopulation;
	}

	public static TarpeianMethod of(final RandomGenerator randomGenerator,
			final Function<Genotype, Integer> sizeFunction, final double probability, final double newValue) {
		return ImmutableTarpeianMethod.of(randomGenerator, sizeFunction, probability, newValue);
	}

	public static TarpeianMethod ofTreeChromosome(final RandomGenerator randomGenerator, final int chromosomeIndex,
			final double probability, final double newValue) {

		return ImmutableTarpeianMethod.of(randomGenerator,
				(genotype) -> genotype.getChromosome(chromosomeIndex, TreeChromosome.class).getSize(),
				probability,
				newValue);
	}
}