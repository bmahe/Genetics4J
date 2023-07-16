package net.bmahe.genetics4j.neat.combination.parentcompare;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.ParentComparisonPolicy;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.FitnessComparison;

public class FitnessComparisonHandler implements ParentComparisonHandler {

	@Override
	public boolean canHandle(final ParentComparisonPolicy parentComparisonPolicy) {
		Validate.notNull(parentComparisonPolicy);

		return parentComparisonPolicy instanceof FitnessComparison;
	}

	@Override
	public ChosenOtherChromosome compare(final ParentComparisonPolicy parentComparisonPolicy, final NeatChromosome first,
			final NeatChromosome second, final int fitnessComparison) {
		Validate.notNull(parentComparisonPolicy);
		Validate.notNull(first);
		Validate.notNull(second);

		if (fitnessComparison < 0) {
			return new ChosenOtherChromosome(second, first);
		}

		return new ChosenOtherChromosome(first, second);
	}
}