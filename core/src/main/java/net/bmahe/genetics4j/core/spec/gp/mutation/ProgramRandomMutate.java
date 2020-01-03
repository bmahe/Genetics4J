package net.bmahe.genetics4j.core.spec.gp.mutation;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;

@Value.Immutable
public interface ProgramRandomMutate extends MutationPolicy {

	@Value.Parameter
	public double populationMutationProbability();

	public static ProgramRandomMutate of(final double populationMutationProbability) {
		return ImmutableProgramRandomMutate.of(populationMutationProbability);
	}
}