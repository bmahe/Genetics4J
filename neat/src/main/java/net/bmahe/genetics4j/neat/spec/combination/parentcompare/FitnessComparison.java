package net.bmahe.genetics4j.neat.spec.combination.parentcompare;

import org.immutables.value.Value;

/**
 * Comparing parents based on their fitness
 */
@Value.Immutable
public interface FitnessComparison extends ParentComparisonPolicy {

	class Builder extends ImmutableFitnessComparison.Builder {
	}

	static Builder builder() {
		return new Builder();
	}

	static FitnessComparison build() {
		return builder().build();
	}
}