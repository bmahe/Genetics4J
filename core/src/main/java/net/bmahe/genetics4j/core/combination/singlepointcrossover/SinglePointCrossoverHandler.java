package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.FloatChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;

public class SinglePointCrossoverHandler<T extends Comparable<T>> implements ChromosomeCombinatorHandler<T> {

	private final RandomGenerator randomGenerator;

	public SinglePointCrossoverHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof SinglePointCrossover
				&& (chromosome instanceof BitChromosomeSpec || chromosome instanceof IntChromosomeSpec
						|| chromosome instanceof DoubleChromosomeSpec || chromosome instanceof FloatChromosomeSpec);
	}

	@Override
	public ChromosomeCombinator<T> resolve(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(SinglePointCrossover.class, combinationPolicy);

		if (chromosome instanceof BitChromosomeSpec) {
			return new BitChromosomeSinglePointCrossover<T>(randomGenerator);
		}

		if (chromosome instanceof IntChromosomeSpec) {
			return new IntChromosomeSinglePointCrossover<T>(randomGenerator);
		}

		if (chromosome instanceof DoubleChromosomeSpec) {
			return new DoubleChromosomeSinglePointCrossover<T>(randomGenerator);
		}

		if (chromosome instanceof FloatChromosomeSpec) {
			return new FloatChromosomeSinglePointCrossover<T>(randomGenerator);
		}

		throw new IllegalArgumentException("Could not handle chromosome " + chromosome);
	}
}