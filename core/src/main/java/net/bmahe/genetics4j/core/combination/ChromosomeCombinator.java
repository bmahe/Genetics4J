package net.bmahe.genetics4j.core.combination;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;

public interface ChromosomeCombinator {

	Chromosome combine(Chromosome chromosome1, Chromosome chromosome2);
}