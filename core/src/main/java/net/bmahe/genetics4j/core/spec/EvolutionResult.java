package net.bmahe.genetics4j.core.spec;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;

@Value.Immutable
public abstract class EvolutionResult {

	@Value.Parameter
	public abstract long generation();

	@Value.Parameter
	public abstract Genotype[] population();

	@Value.Parameter
	public abstract double[] fitness();

	public Genotype bestGenotype() {
		final Genotype[] population = population();
		final double[] fitness = fitness();

		Validate.notNull(population);
		Validate.notNull(fitness);
		Validate.isTrue(population.length == fitness.length);
		Validate.isTrue(population.length > 0);

		double maxFitness = fitness[0];
		int maxFitnessIndex = 0;

		for (int i = 0; i < fitness.length; i++) {
			final double score = fitness[i];

			if (score > maxFitness) {
				maxFitness = score;
				maxFitnessIndex = i;
			}

		}

		return population[maxFitnessIndex];
	}
}