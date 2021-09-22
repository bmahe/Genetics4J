package net.bmahe.genetics4j.core.combination.singlepointarithmetic;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.SinglePointArithmetic;

public class SinglePointArithmeticCombinationHandler implements ChromosomeCombinatorHandler {

	private final RandomGenerator randomGenerator;

	public SinglePointArithmeticCombinationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof SinglePointArithmetic
				&& (chromosome instanceof IntChromosomeSpec || chromosome instanceof DoubleChromosomeSpec);
	}

	@Override
	public ChromosomeCombinator resolve(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(SinglePointArithmetic.class, combinationPolicy);

		final var singlePointArithmeticRecombination = (SinglePointArithmetic) combinationPolicy;
		final double alpha = singlePointArithmeticRecombination.alpha();

		if (chromosome instanceof IntChromosomeSpec) {
			return new IntChromosomeSinglePointArithmetic(randomGenerator, alpha);
		}

		if (chromosome instanceof DoubleChromosomeSpec) {
			return new DoubleChromosomeSinglePointArithmetic(randomGenerator, alpha);
		}

		throw new IllegalArgumentException("Could not handle chromosome " + chromosome);
	}
}