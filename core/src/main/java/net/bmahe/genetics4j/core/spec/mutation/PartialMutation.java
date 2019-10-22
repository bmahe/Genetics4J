package net.bmahe.genetics4j.core.spec.mutation;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class PartialMutation implements MutationPolicy {

	@Value.Parameter
	public abstract int chromosomeIndex();

	@Value.Parameter
	public abstract MutationPolicy mutationPolicy();

	@Value.Check
	protected void check() {
		Validate.isTrue(chromosomeIndex() >= 0);
		Validate.notNull(mutationPolicy());
	}

	public static PartialMutation of(final int chromosomeIndex, final MutationPolicy mutationPolicy) {
		return ImmutablePartialMutation.of(chromosomeIndex, mutationPolicy);
	}
}