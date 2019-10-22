package net.bmahe.genetics4j.core.combination.multicombinations;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorHandler;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.MultiCombinations;

public class MultiCombinationsHandler implements ChromosomeCombinatorHandler {

	private final Random random;

	public MultiCombinationsHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
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
	public ChromosomeCombinator resolve(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(chromosomeCombinatorResolver);
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(MultiCombinations.class, combinationPolicy);

		final MultiCombinations multiCombinations = (MultiCombinations) combinationPolicy;

		final List<ChromosomeCombinator> chromosomeCombinators = multiCombinations.combinationPolicies()
				.stream()
				.map((cp) -> {
					return chromosomeCombinatorResolver.resolve(cp, chromosome);
				})
				.collect(Collectors.toList());

		return new MultiChromosomeCombinations(random, chromosomeCombinators);
	}
}