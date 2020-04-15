package net.bmahe.genetics4j.core;

import java.util.List;

@FunctionalInterface
public interface EvolutionListener<T> {

	void onEvolution(final long generation, final Genotype[] population, final List<T> fitness);
}