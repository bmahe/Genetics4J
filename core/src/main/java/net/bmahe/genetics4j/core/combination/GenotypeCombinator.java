package net.bmahe.genetics4j.core.combination;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

public interface GenotypeCombinator {

	List<Genotype> combine(final AbstractEAConfiguration eaConfiguration, final List<List<Chromosome>> chromosomes);
}