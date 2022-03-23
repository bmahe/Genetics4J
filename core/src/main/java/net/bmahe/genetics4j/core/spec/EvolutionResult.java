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
	public abstract AbstractEAConfiguration<T> eaConfiguration();

	@Value.Parameter
	public abstract long generation();

	@Value.Parameter
	public abstract List<Genotype> population();

	@Value.Parameter
	public abstract List<T> fitness();

	@Value.Derived
	public GenotypeFitness<T> bestIndividual() {

		final List<Genotype> population = population();
		final List<T> fitness = fitness();

		Validate.notNull(population);
		Validate.notNull(fitness);
		Validate.isTrue(population.size() == fitness.size());
		Validate.isTrue(population.size() > 0);

		switch (eaConfiguration().optimization()) {
			case MAXIMZE:
			case MINIMIZE:
				break;
			default:
				throw new IllegalArgumentException("Unsupported optimization " + eaConfiguration().optimization());
		}

		final Comparator<T> comparator = Optimization.MAXIMZE.equals(eaConfiguration().optimization())
				? Comparator.naturalOrder()
				: Comparator.reverseOrder();

		final Optional<Integer> bestIndexOpt = IntStream.range(0, fitness.size())
				.boxed()
				.max((a, b) -> comparator.compare(fitness.get(a), fitness.get(b)));

		final Integer bestIndex = bestIndexOpt.orElseThrow(
				() -> new IllegalStateException("Couldn't find a best entry despite having a non-zero population"));

		return GenotypeFitness.of(population.get(bestIndex), fitness.get(bestIndex));
	}

	public Genotype bestGenotype() {
		return bestIndividual().genotype();
	}

	public T bestFitness() {
		return bestIndividual().fitness();
	}
}