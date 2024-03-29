package net.bmahe.genetics4j.core.combination.multicombinations;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.MultiCombinations;

public class MultiCombinationsHandler<T extends Comparable<T>> implements ChromosomeCombinatorHandler<T> {

	private final RandomGenerator randomGenerator;

	public MultiCombinationsHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		final boolean isMultiCombinationPolicy = combinationPolicy instanceof MultiCombinations;

		if (isMultiCombinationPolicy == false) {
			return false;
		}

		final MultiCombinations multiCombinations = (MultiCombinations) combinationPolicy;

		return multiCombinations.combinationPolicies()
				.stream()
				.allMatch((cp) -> chromosomeCombinatorResolver.canHandle(cp, chromosome));
	}

	@Override
	public ChromosomeCombinator<T> resolve(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(MultiCombinations.class, combinationPolicy);

		final MultiCombinations multiCombinations = (MultiCombinations) combinationPolicy;

		final List<ChromosomeCombinator<T>> chromosomeCombinators = multiCombinations.combinationPolicies()
				.stream()
				.map((cp) -> {
					return chromosomeCombinatorResolver.resolve(cp, chromosome);
				})
				.collect(Collectors.toList());

		return new MultiChromosomeCombinations<T>(randomGenerator, chromosomeCombinators);
	}
}