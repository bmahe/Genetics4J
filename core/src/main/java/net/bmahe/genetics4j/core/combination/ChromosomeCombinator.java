package net.bmahe.genetics4j.core.combination;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;

public interface ChromosomeCombinator {

	public boolean canHandle(CombinationPolicy combinationPolicy, ChromosomeSpec chromosome);

	public Chromosome combine(CombinationPolicy combinationPolicy, Chromosome chromosome1, Chromosome chromosome2);
}