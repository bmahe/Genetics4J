package net.bmahe.genetics4j.core.spec.combination;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SinglePointArithmetic implements CombinationPolicy {

	public static final double DEFAULT_ALPHA = 0.5d;

	@Value.Parameter
	@Value.Default
	public double alpha() {
		return DEFAULT_ALPHA;
	}

	public static SinglePointArithmetic build() {
		return new SinglePointArithmetic() {
		};
	}

	public static SinglePointArithmetic of(final double alpha) {
		return ImmutableSinglePointArithmetic.of(alpha);
	}
}