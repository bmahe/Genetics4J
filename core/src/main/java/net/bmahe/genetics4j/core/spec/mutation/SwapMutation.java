package net.bmahe.genetics4j.core.spec.mutation;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class SwapMutation implements MutationPolicy {

	@Value.Parameter
	public abstract double populationMutationProbability();

	@Value.Parameter
	public abstract int numSwap();

	@Value.Check
	protected void check() {
		Validate.inclusiveBetween(0.0, 1.0, populationMutationProbability());
		Validate.isTrue(numSwap() > 0);
	}
}