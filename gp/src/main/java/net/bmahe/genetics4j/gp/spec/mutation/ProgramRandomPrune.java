package net.bmahe.genetics4j.gp.spec.mutation;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

@Value.Immutable
public interface ProgramRandomPrune extends MutationPolicy {

	@Value.Parameter
	public double populationMutationProbability();

	public static ProgramRandomPrune of(final double populationMutationProbability) {
		return ImmutableProgramRandomPrune.of(populationMutationProbability);
	}
}