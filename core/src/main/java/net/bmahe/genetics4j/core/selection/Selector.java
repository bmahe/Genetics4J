package net.bmahe.genetics4j.core.selection;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;

@FunctionalInterface
public interface Selector<T extends Comparable<T>> {

	List<Genotype> select(final GenotypeSpec<T> genotypeSpec, final int numIndividuals, final Genotype[] population,
			final List<T> fitnessScore);
}