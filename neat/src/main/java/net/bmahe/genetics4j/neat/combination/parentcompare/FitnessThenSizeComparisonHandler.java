package net.bmahe.genetics4j.neat.combination.parentcompare;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.FitnessThenSizeComparison;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.ParentComparisonPolicy;

public class FitnessThenSizeComparisonHandler implements ParentComparisonHandler {

	@Override
	public boolean canHandle(final ParentComparisonPolicy parentComparisonPolicy) {
		Validate.notNull(parentComparisonPolicy);

		return parentComparisonPolicy instanceof FitnessThenSizeComparison;
	}

	@Override
	public ChosenOtherChromosome compare(final ParentComparisonPolicy parentComparisonPolicy, final NeatChromosome first,
			final NeatChromosome second, final int fitnessComparison) {
		Validate.notNull(parentComparisonPolicy);
		Validate.notNull(first);
		Validate.notNull(second);

		if (fitnessComparison < 0 || (fitnessComparison == 0 && first.getNumAlleles() > second.getNumAlleles())) {
			return new ChosenOtherChromosome(second, first);
		}
		return new ChosenOtherChromosome(first, second);
	}
}