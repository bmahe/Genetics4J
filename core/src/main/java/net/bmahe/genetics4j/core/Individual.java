package net.bmahe.genetics4j.core;

import org.immutables.value.Value;

@Value.Immutable
public interface Individual<T extends Comparable<T>> {

	@Value.Parameter
	Genotype genotype();

	@Value.Parameter
	T fitness();

	static <U extends Comparable<U>> Individual<U> of(final Genotype genotype, final U fitness) {
		return ImmutableIndividual.of(genotype, fitness);
	}
}