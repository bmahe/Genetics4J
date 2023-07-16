package net.bmahe.genetics4j.neat.combination.parentcompare;

import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.ParentComparisonPolicy;

public interface ParentComparisonHandler {

	boolean canHandle(final ParentComparisonPolicy parentComparisonPolicy);

	ChosenOtherChromosome compare(final ParentComparisonPolicy parentComparisonPolicy, final NeatChromosome first,
			final NeatChromosome second, final int fitnessComparison);
}