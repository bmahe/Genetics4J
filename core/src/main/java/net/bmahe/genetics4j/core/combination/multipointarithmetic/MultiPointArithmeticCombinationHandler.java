package net.bmahe.genetics4j.core.combination.multipointarithmetic;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.MultiPointArithmetic;

public class MultiPointArithmeticCombinationHandler implements ChromosomeCombinatorHandler {

	private final Random random;

	public MultiPointArithmeticCombinationHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof MultiPointArithmetic
				&& (chromosome instanceof IntChromosomeSpec || chromosome instanceof DoubleChromosomeSpec);
	}

	@Override
	public ChromosomeCombinator resolve(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(MultiPointArithmetic.class, combinationPolicy);

		if (chromosome instanceof IntChromosomeSpec) {
			return new IntChromosomeMultiPointArithmetic(random, (MultiPointArithmetic) combinationPolicy);
		}

		if (chromosome instanceof DoubleChromosomeSpec) {
			return new DoubleChromosomeMultiPointArithmetic(random, (MultiPointArithmetic) combinationPolicy);
		}

		throw new IllegalArgumentException("Could not handle chromosome " + chromosome);
	}

}