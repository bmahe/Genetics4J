package net.bmahe.genetics4j.core.spec;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;

@Value.Immutable
public abstract class EvolutionResult<T extends Comparable<T>> {

	@Value.Parameter
	public abstract GenotypeSpec<T> genotypeSpec();

	@Value.Parameter
	public abstract long generation();

	@Value.Parameter
	public abstract Genotype[] population();

	@Value.Parameter
	public abstract List<T> fitness();

	public Genotype bestGenotype() {
		final Genotype[] population = population();
		final List<T> fitness = fitness();

		Validate.notNull(population);
		Validate.notNull(fitness);
		Validate.isTrue(population.length == fitness.size());
		Validate.isTrue(population.length > 0);

		switch (genotypeSpec().optimization()) {
		case MAXIMZE:
		case MINIMIZE:
			break;
		default:
			throw new IllegalArgumentException("Unsupported optimization " + genotypeSpec().optimization());
		}

		final Comparator<T> comparator = Optimization.MAXIMZE.equals(genotypeSpec().optimization())
				? Comparator.naturalOrder()
				: Comparator.reverseOrder();

		Genotype bestCandidate = population[0];
		T bestScore = fitness.get(0);

		for (int i = 0; i < fitness.size(); i++) {
			final T score = fitness.get(i);

			if (comparator.compare(bestScore, score) < 0) {
				bestScore = score;
				bestCandidate = population[i];
			}

		}

		return bestCandidate;
	}
}