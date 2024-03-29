package net.bmahe.genetics4j.core.combination.multipointcrossover;

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
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;

public class MultiPointCrossoverCombinationHandler<T extends Comparable<T>> implements ChromosomeCombinatorHandler<T> {

	private final RandomGenerator randomGenerator;

	public MultiPointCrossoverCombinationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof MultiPointCrossover
				&& (chromosome instanceof BitChromosomeSpec || chromosome instanceof IntChromosomeSpec
						|| chromosome instanceof DoubleChromosomeSpec || chromosome instanceof FloatChromosomeSpec);
	}

	@Override
	public ChromosomeCombinator<T> resolve(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(MultiPointCrossover.class, combinationPolicy);

		if (chromosome instanceof BitChromosomeSpec) {
			return new BitChromosomeMultiPointCrossover<T>(randomGenerator, (MultiPointCrossover) combinationPolicy);
		}

		if (chromosome instanceof IntChromosomeSpec) {
			return new IntChromosomeMultiPointCrossover<T>(randomGenerator, (MultiPointCrossover) combinationPolicy);
		}

		if (chromosome instanceof DoubleChromosomeSpec) {
			return new DoubleChromosomeMultiPointCrossover<T>(randomGenerator, (MultiPointCrossover) combinationPolicy);
		}

		if (chromosome instanceof FloatChromosomeSpec) {
			return new FloatChromosomeMultiPointCrossover<T>(randomGenerator, (MultiPointCrossover) combinationPolicy);
		}

		throw new IllegalArgumentException("Could not handle chromosome " + chromosome);
	}
}