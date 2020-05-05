package net.bmahe.genetics4j.core.termination;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;

@FunctionalInterface
public interface Termination<T> {
	boolean isDone(final long generation, final List<Genotype> population, final List<T> fitness);
}