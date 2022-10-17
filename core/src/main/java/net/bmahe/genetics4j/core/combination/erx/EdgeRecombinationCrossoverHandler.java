package net.bmahe.genetics4j.core.combination.erx;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.EdgeRecombinationCrossover;

public class EdgeRecombinationCrossoverHandler<T extends Comparable<T>> implements ChromosomeCombinatorHandler<T> {

	private final RandomGenerator randomGenerator;

	public EdgeRecombinationCrossoverHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof EdgeRecombinationCrossover && chromosome instanceof IntChromosomeSpec;
	}

	@Override
	public ChromosomeCombinator<T> resolve(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isTrue(canHandle(chromosomeCombinatorResolver, combinationPolicy, chromosome));

		return new IntEdgeRecombinationCrossover<T>(randomGenerator);
	}
}