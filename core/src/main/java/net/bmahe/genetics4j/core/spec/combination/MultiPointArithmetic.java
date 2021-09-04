package net.bmahe.genetics4j.core.spec.combination;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class MultiPointArithmetic implements CombinationPolicy {

	public static final double DEFAULT_ALPHA = 0.5d;

	@Value.Parameter
	public abstract int numCrossovers();

	@Value.Parameter
	@Value.Default
	public double alpha() {
		return DEFAULT_ALPHA;
	}

	public static MultiPointArithmetic of(final int numCrossovers, final double alpha) {
		Validate.isTrue(numCrossovers > 0);
		return ImmutableMultiPointArithmetic.of(numCrossovers, alpha);
	}

	public static MultiPointArithmetic of(final int numCrossovers) {
		return of(numCrossovers, DEFAULT_ALPHA);
	}
}