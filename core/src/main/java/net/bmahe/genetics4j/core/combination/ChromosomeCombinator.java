package net.bmahe.genetics4j.core.combination;

import java.util.List;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

public interface ChromosomeCombinator<T extends Comparable<T>> {

	List<Chromosome> combine(
			final AbstractEAConfiguration<T> eaConfiguration, final Chromosome firstChromosome,
			final T firstParentFitness, final Chromosome secondChromosome, final T secondParentFitness);
}