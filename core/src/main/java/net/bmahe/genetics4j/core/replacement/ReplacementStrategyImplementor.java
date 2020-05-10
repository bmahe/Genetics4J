package net.bmahe.genetics4j.core.replacement;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.EAConfiguration;

public interface ReplacementStrategyImplementor<T extends Comparable<T>> {

	Population<T> select(final EAConfiguration<T> eaConfiguration, final int numIndividuals,
			final List<Genotype> population, final List<T> populationScores, final List<Genotype> offsprings,
			final List<T> offspringScores);

}