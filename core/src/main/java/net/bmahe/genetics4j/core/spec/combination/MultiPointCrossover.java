package net.bmahe.genetics4j.core.spec.combination;

import org.immutables.value.Value;

@Value.Immutable
public abstract class MultiPointCrossover implements CombinationPolicy {

	@Value.Parameter
	public abstract int numCrossovers();
}