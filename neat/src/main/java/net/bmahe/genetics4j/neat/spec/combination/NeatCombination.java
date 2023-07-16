package net.bmahe.genetics4j.neat.spec.combination;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.ParentComparisonPolicy;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.FitnessComparison;

@Value.Immutable
public interface NeatCombination extends CombinationPolicy {

	public static final double DEFAULT_INHERITANCE_THRESHOLD = 0.5d;

	public static final double DEFAULT_REENABLE_GENE_INHERITANCE_THRESHOLD = 0.25d;

	/**
	 * Matching, excess and disjoint genes may be chosen randomly between the
	 * parents. By default the selection is unbiased toward any parent.
	 * This threshold can be adjusted towards the better individual or the lesser
	 * one.
	 * <br/>
	 * Acceptable values are between 0 and 1 (inclusive), and higher values will
	 * favor the better individual
	 * 
	 * @return
	 */
	@Value.Default
	default public double inheritanceThresold() {
		return DEFAULT_INHERITANCE_THRESHOLD;
	}

	/**
	 * {@return If a gene is disabled in either parent, there is a chance it will
	 * get re-enabled if it is enabled in either parent.
	 * <br/>
	 * This setting configures that threshold}
	 * 
	 */
	@Value.Default
	default public double reenableGeneInheritanceThresold() {
		return DEFAULT_REENABLE_GENE_INHERITANCE_THRESHOLD;
	}

	/**
	 * {@return The policy used to compare parents. Defaults to fitness comparison.}
	 */
	@Value.Default
	default public ParentComparisonPolicy parentComparisonPolicy() {
		return FitnessComparison.build();
	}

	@Value.Check
	default void check() {
		Validate.inclusiveBetween(0, 1, inheritanceThresold());
		Validate.inclusiveBetween(0, 1, reenableGeneInheritanceThresold());
	}

	class Builder extends ImmutableNeatCombination.Builder {
	}

	static Builder builder() {
		return new Builder();
	}

	static NeatCombination build() {
		return builder().build();
	}

}