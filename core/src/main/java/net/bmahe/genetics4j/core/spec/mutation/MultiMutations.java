package net.bmahe.genetics4j.core.spec.mutation;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

/**
 * Select uniformly a mutation policy among a list
 *
 */
@Value.Immutable
public abstract class MultiMutations implements MutationPolicy {

	@Value.Parameter
	public abstract List<MutationPolicy> mutationPolicies();

	@Value.Check
	protected void check() {
		Validate.notNull(mutationPolicies());
	}

	public static MultiMutations of(final List<MutationPolicy> mutationPolicies) {
		return ImmutableMultiMutations.of(mutationPolicies);
	}

	public static MultiMutations of(final MutationPolicy... mutationPolicies) {
		return ImmutableMultiMutations.of(Arrays.asList(mutationPolicies));
	}
}