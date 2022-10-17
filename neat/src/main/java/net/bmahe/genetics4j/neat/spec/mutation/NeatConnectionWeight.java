package net.bmahe.genetics4j.neat.spec.mutation;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.spec.statistics.distributions.NormalDistribution;

@Value.Immutable
public abstract class NeatConnectionWeight implements MutationPolicy {

	@Value.Default
	public double populationMutationProbability() {
		return 0.80;
	}

	@Value.Default
	public double perturbationRatio() {
		return 0.90;
	}

	@Value.Default
	public Distribution perturbationDistribution() {
		return NormalDistribution.of(0.0, 1.0);
	}

	@Value.Check
	protected void check() {
		Validate.inclusiveBetween(0.0, 1.0, populationMutationProbability());
	}

	public static class Builder extends ImmutableNeatConnectionWeight.Builder {
	}

	public static NeatConnectionWeight build() {
		return new ImmutableNeatConnectionWeight.Builder().build();
	}

	public static Builder builder() {
		return new Builder();
	}
}