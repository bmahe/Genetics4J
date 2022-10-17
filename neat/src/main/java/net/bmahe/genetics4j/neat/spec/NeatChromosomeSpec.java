package net.bmahe.genetics4j.neat.spec;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

@Value.Immutable
public abstract class NeatChromosomeSpec implements ChromosomeSpec {

	@Value.Parameter
	public abstract int numInputs();

	@Value.Parameter
	public abstract int numOutputs();

	@Value.Parameter
	public abstract float minWeightValue();

	@Value.Parameter
	public abstract float maxWeightValue();

	@Value.Check
	protected void check() {
		Validate.isTrue(numInputs() > 0);
		Validate.isTrue(numOutputs() > 0);
	}

	public static class Builder extends ImmutableNeatChromosomeSpec.Builder {
	}

	public static NeatChromosomeSpec of(final int numInputs, final int numOutputs, final float minWeightValue,
			final float maxWeightValue) {
		return new Builder().numInputs(numInputs)
				.numOutputs(numOutputs)
				.minWeightValue(minWeightValue)
				.maxWeightValue(maxWeightValue)
				.build();
	}
}