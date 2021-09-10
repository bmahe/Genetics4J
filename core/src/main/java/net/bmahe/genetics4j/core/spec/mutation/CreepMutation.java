package net.bmahe.genetics4j.core.spec.mutation;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.spec.statistics.distributions.NormalDistribution;

@Value.Immutable
public abstract class CreepMutation implements MutationPolicy {

	@Value.Parameter
	public abstract double populationMutationProbability();

	@Value.Parameter
	public abstract Distribution distribution();

	@Value.Check
	protected void check() {
		Validate.inclusiveBetween(0.0, 1.0, populationMutationProbability());
	}

	public static CreepMutation of(final double populationMutationProbability, final Distribution distribution) {
		return ImmutableCreepMutation.of(populationMutationProbability, distribution);
	}

	public static CreepMutation ofNormal(final double populationMutationProbability, final double mean,
			final double standardDeviation) {
		return ImmutableCreepMutation.of(populationMutationProbability, NormalDistribution.of(mean, standardDeviation));
	}
}