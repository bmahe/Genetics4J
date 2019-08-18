package net.bmahe.genetics4j.core;

@FunctionalInterface
public interface Termination {
	boolean isDone(long generation, Genotype[] population, double[] fitness);
}