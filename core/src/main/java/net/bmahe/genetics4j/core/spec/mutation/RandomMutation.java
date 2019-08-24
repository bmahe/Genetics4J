package net.bmahe.genetics4j.core.spec.mutation;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class RandomMutation implements MutationPolicy {

	@Value.Parameter
	public abstract double populationMutationProbability();

	@Value.Check
	protected void check() {
		Validate.inclusiveBetween(0.0, 1.0, populationMutationProbability());
	}

	/**
	 * Construct a new immutable {@code RandomMutation} instance.
	 *
	 * @param populationMutationProbability The value for the
	 *                                      {@code populationMutationProbability}
	 *                                      attribute
	 * @return An immutable RandomMutation instance
	 */
	public static RandomMutation of(final double populationMutationProbability) {
		return ImmutableRandomMutation.of(populationMutationProbability);
	}
}