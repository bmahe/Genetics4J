package net.bmahe.genetics4j.core.spec;

import java.util.function.BiFunction;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;

@Value.Immutable
public abstract class EvolutionResult {

	@Value.Parameter
	public abstract GenotypeSpec genotypeSpec();

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

		switch (genotypeSpec().optimization()) {
			case MAXIMZE:
			case MINIMIZE:
				break;
			default:
				throw new IllegalArgumentException("Unsupported optimization " + genotypeSpec().optimization());
		}

		final BiFunction<Double, Double, Boolean> isScoreBetter = Optimization.MAXIMZE
				.equals(genotypeSpec().optimization()) ? (best, score) -> best < score : (best, score) -> best > score;

		Genotype bestCandidate = population[0];
		double bestScore = fitness[0];

		for (int i = 0; i < fitness.length; i++) {
			final double score = fitness[i];

			if (isScoreBetter.apply(bestScore, score)) {
				bestScore = score;
				bestCandidate = population[i];
			}

		}

		return bestCandidate;
	}
}