package net.bmahe.genetics4j.neat.spec.mutation;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

@Value.Immutable
public abstract class AddConnection implements MutationPolicy {

	@Value.Parameter
	public abstract double populationMutationProbability();

	@Value.Check
	protected void check() {
		Validate.inclusiveBetween(0.0, 1.0, populationMutationProbability());
	}

	/**
	 * Construct a new immutable {@code AddNodeMutation} instance.
	 *
	 * @param populationMutationProbability The value for the
	 *                                      {@code populationMutationProbability}
	 *                                      attribute
	 * @return An immutable AddNodeMutation instance
	 */
	public static AddConnection of(final double populationMutationProbability) {
		return ImmutableAddConnection.of(populationMutationProbability);
	}

}