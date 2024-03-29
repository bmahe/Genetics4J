package net.bmahe.genetics4j.core.spec;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;

/**
 * Pair of Genotype to its associated fitness
 *
 * @param <T> Type of the fitness measurement
 */
@Value.Immutable
public interface GenotypeFitness<T> {

	@Value.Parameter
	Genotype genotype();

	@Value.Parameter
	T fitness();

	static <U> GenotypeFitness<U> of(final Genotype genotype, final U fitness) {
		return ImmutableGenotypeFitness.of(genotype, fitness);
	}
}