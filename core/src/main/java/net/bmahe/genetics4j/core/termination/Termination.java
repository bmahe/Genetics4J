package net.bmahe.genetics4j.core.termination;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.EAConfiguration;

@FunctionalInterface
public interface Termination<T extends Comparable<T>> {
	boolean isDone(final EAConfiguration<T> eaConfiguration, final long generation, final List<Genotype> population,
			final List<T> fitness);
}