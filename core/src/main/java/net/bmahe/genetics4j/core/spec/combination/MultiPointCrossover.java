package net.bmahe.genetics4j.core.spec.combination;

import org.immutables.value.Value;

@Value.Immutable
public abstract class MultiPointCrossover implements CombinationPolicy {

	@Value.Parameter
	public abstract int numCrossovers();

	/**
	 * Construct a new immutable {@code MultiPointCrossover} instance.
	 *
	 * @param numCrossovers The value for the {@code numCrossovers} attribute
	 * @return An immutable MultiPointCrossover instance
	 */
	public static MultiPointCrossover of(final int numCrossovers) {
		return ImmutableMultiPointCrossover.of(numCrossovers);
	}
}