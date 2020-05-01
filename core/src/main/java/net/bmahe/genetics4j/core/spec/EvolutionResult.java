package net.bmahe.genetics4j.core.spec;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

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

	@Value.Derived
	public GenotypeFitness<T> bestIndividual() {

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

		final Optional<Integer> bestIndexOpt = IntStream.range(0, fitness.size())
				.boxed()
				.max((a, b) -> comparator.compare(fitness.get(a), fitness.get(b)));

		final Integer bestIndex = bestIndexOpt.orElseThrow(
				() -> new IllegalStateException("Couldn't find a best entry despite having a non-zero population"));

		return GenotypeFitness.of(population[bestIndex], fitness.get(bestIndex));
	}

	public Genotype bestGenotype() {
		return bestIndividual().genotype();
	}

	public T bestFitness() {
		return bestIndividual().fitness();
	}
}