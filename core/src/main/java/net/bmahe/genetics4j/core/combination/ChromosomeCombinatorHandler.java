package net.bmahe.genetics4j.core.combination;

import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;

public interface ChromosomeCombinatorHandler {
	boolean canHandle(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome);

	ChromosomeCombinator resolve(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome);
}