package net.bmahe.genetics4j.core.selection;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.EAConfiguration;

@FunctionalInterface
public interface Selector<T extends Comparable<T>> {

	List<Genotype> select(final EAConfiguration<T> eaConfiguration, final int numIndividuals,
			final Genotype[] population, final List<T> fitnessScore);
}