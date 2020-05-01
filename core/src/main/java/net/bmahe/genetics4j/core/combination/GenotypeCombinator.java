package net.bmahe.genetics4j.core.combination;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.EAConfiguration;

public interface GenotypeCombinator {

	List<Genotype> combine(final EAConfiguration eaConfiguration, final List<List<Chromosome>> chromosomes);
}