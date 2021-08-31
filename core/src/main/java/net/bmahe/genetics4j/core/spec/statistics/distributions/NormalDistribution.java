package net.bmahe.genetics4j.core.spec.statistics.distributions;

import org.immutables.value.Value;

@Value.Immutable
public abstract class NormalDistribution implements Distribution {

	@Value.Parameter
	@Value.Default
	public double mean() {
		return 0.0d;
	}

	@Value.Parameter
	@Value.Default
	public double standardDeviation() {
		return 1.0d;
	}

	public static NormalDistribution of(final double mean, final double standardDeviation) {
		return ImmutableNormalDistribution.of(mean, standardDeviation);
	}
}