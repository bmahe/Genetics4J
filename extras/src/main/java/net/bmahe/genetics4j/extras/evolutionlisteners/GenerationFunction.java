package net.bmahe.genetics4j.extras.evolutionlisteners;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;

@FunctionalInterface
public interface GenerationFunction<T extends Comparable<T>, U> {

	U apply(final long generation, final List<Genotype> population, final List<T> fitness, final boolean isDone);
}