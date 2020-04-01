package net.bmahe.genetics4j.gp.spec.mutation;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

@Value.Immutable
public interface ProgramRandomPrune extends MutationPolicy {

	@Value.Parameter
	public double populationMutationProbability();

	@Value.Check
	default void check() {
		Validate.inclusiveBetween(0.0, 1.0, populationMutationProbability());
	}

	public static ProgramRandomPrune of(final double populationMutationProbability) {
		return ImmutableProgramRandomPrune.of(populationMutationProbability);
	}
}