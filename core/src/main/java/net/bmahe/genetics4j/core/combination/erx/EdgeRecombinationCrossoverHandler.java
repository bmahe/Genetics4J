package net.bmahe.genetics4j.core.combination.erx;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.EdgeRecombinationCrossover;

public class EdgeRecombinationCrossoverHandler implements ChromosomeCombinatorHandler {

	private final Random random;

	public EdgeRecombinationCrossoverHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof EdgeRecombinationCrossover && chromosome instanceof IntChromosomeSpec;
	}

	@Override
	public ChromosomeCombinator resolve(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isTrue(canHandle(chromosomeCombinatorResolver, combinationPolicy, chromosome));

		return new IntEdgeRecombinationCrossover(random);
	}

}
