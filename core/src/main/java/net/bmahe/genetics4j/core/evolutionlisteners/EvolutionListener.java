package net.bmahe.genetics4j.core.evolutionlisteners;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;

@FunctionalInterface
public interface EvolutionListener<T> {

	void onEvolution(final long generation, final List<Genotype> population, final List<T> fitness);
}