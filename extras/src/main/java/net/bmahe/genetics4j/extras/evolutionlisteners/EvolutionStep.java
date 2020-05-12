package net.bmahe.genetics4j.extras.evolutionlisteners;

import java.util.Optional;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;

@Value.Immutable
public interface EvolutionStep<T extends Comparable<T>, U> {

	@Value.Parameter
	Optional<U> context();

	@Value.Parameter
	long generation();

	@Value.Parameter
	int individualIndex();

	@Value.Parameter
	Genotype individual();

	@Value.Parameter
	T fitness();

	@Value.Parameter
	boolean isDone();

	public static class Builder<T extends Comparable<T>, U> extends ImmutableEvolutionStep.Builder<T, U> {
	}

	public static <T extends Comparable<T>, U> EvolutionStep<T, U> of(final Optional<U> context, final long generation,
			final int individualIndex, final Genotype individual, final T fitness, final boolean isDone) {

		return ImmutableEvolutionStep.of(context, generation, individualIndex, individual, fitness, isDone);
	}

}