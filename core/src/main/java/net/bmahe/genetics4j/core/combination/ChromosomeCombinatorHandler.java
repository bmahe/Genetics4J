package net.bmahe.genetics4j.core.combination;

import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;

public interface ChromosomeCombinatorHandler {
	boolean canHandle(ChromosomeCombinatorResolver chromosomeCombinatorResolver, CombinationPolicy combinationPolicy,
			ChromosomeSpec chromosome);

	ChromosomeCombinator resolve(ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			CombinationPolicy combinationPolicy, ChromosomeSpec chromosome);
}