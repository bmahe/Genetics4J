package net.bmahe.genetics4j.neat.spec.combination.parentcompare;

import org.immutables.value.Value;

/**
 * Comparing parents based on fitness first and then their size in case of equal
 * fitness.
 */
@Value.Immutable
public interface FitnessThenSizeComparison extends ParentComparisonPolicy {

	class Builder extends ImmutableFitnessThenSizeComparison.Builder {
	}

	static Builder builder() {
		return new Builder();
	}

	static FitnessThenSizeComparison build() {
		return builder().build();
	}
}