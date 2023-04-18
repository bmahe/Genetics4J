package net.bmahe.genetics4j.neat.combination;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.combination.NeatCombination;

public class NeatCombinationHandler<T extends Comparable<T>> implements ChromosomeCombinatorHandler<T> {

	private final RandomGenerator randomGenerator;

	public NeatCombinationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof NeatCombination && chromosome instanceof NeatChromosomeSpec;
	}

	@Override
	public ChromosomeCombinator<T> resolve(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(NeatCombination.class, combinationPolicy);
		Validate.isInstanceOf(NeatChromosomeSpec.class, chromosome);

		final var neatCombination = (NeatCombination) combinationPolicy;
		return new NeatChromosomeCombinator<>(randomGenerator, neatCombination);
	}
}