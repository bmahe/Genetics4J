package net.bmahe.genetics4j.extras.evolutionlisteners;

@FunctionalInterface
public interface ColumnExtractorFunction<T extends Comparable<T>, U> {

	Object apply(final EvolutionStep<T, U> evolutionStep);
}