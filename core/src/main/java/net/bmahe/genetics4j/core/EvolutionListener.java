package net.bmahe.genetics4j.core;

@FunctionalInterface
public interface EvolutionListener {

	void onEvolution(long generation, Genotype[] population, double[] fitness);
}