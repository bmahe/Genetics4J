package net.bmahe.genetics4j.core.selection;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;

@FunctionalInterface
public interface Selector {

	List<Genotype> select(final GenotypeSpec genotypeSpec, final int numIndividuals, final Genotype[] population,
			final double[] fitnessScore);
}