package net.bmahe.genetics4j.core.combination;

import java.util.List;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;

public interface ChromosomeCombinator {

	List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2);
}