package net.bmahe.genetics4j.core.spec.combination;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.combination.ImmutableMultiCombinations;
import net.bmahe.genetics4j.core.spec.combination.MultiCombinations;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;

@Value.Immutable
public abstract class MultiCombinations implements CombinationPolicy {

	@Value.Parameter
	public abstract List<CombinationPolicy> combinationPolicies();

	@Value.Check
	protected void check() {
		Validate.notNull(combinationPolicies());
	}

	public static MultiCombinations of(final List<CombinationPolicy> combinationPolicies) {
		return ImmutableMultiCombinations.of(combinationPolicies);
	}

	public static MultiCombinations of(final CombinationPolicy... combinationPolicies) {
		return ImmutableMultiCombinations.of(Arrays.asList(combinationPolicies));
	}
}