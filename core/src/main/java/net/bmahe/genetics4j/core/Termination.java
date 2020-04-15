package net.bmahe.genetics4j.core;

import java.util.List;

@FunctionalInterface
public interface Termination<T> {
	boolean isDone(final long generation, final Genotype[] population, final List<T> fitness);
}