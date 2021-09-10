package net.bmahe.genetics4j.core.spec.statistics.distributions;

import org.immutables.value.Value;

@Value.Immutable
public abstract class UniformDistribution implements Distribution {

	
	public static UniformDistribution build() {
		return ImmutableUniformDistribution.builder().build();
	}	
}