package net.bmahe.genetics4j.core.spec.chromosome;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.spec.statistics.distributions.UniformDistribution;

@Value.Immutable
public abstract class FloatChromosomeSpec implements ChromosomeSpec {

	@Value.Parameter
	public abstract int size();

	@Value.Parameter
	public abstract float minValue();

	@Value.Parameter
	public abstract float maxValue();

	@Value.Default
	public Distribution distribution() {
		return UniformDistribution.build();
	}

	@Value.Check
	protected void check() {
		Validate.isTrue(size() > 0);
		Validate.isTrue(minValue() <= maxValue());
	}

	public static class Builder extends ImmutableFloatChromosomeSpec.Builder {
	}

	/**
	 * Construct a new immutable {@code FloatChromosomeSpec} instance.
	 * 
	 * @param size     The value for the {@code size} attribute
	 * @param minValue The value for the {@code minValue} attribute
	 * @param maxValue The value for the {@code maxValue} attribute
	 * @return An immutable FloatChromosomeSpec instance
	 */

	public static FloatChromosomeSpec of(final int size, final float minValue, final float maxValue) {
		return ImmutableFloatChromosomeSpec.of(size, minValue, maxValue);
	}

	public static FloatChromosomeSpec of(final int size, final float minValue, final float maxValue,
			final Distribution distribution) {
		Validate.notNull(distribution);

		final var floatChromosomeSpecBuilder = new ImmutableFloatChromosomeSpec.Builder();

		floatChromosomeSpecBuilder.size(size)
				.minValue(minValue)
				.maxValue(maxValue)
				.distribution(distribution);

		return floatChromosomeSpecBuilder.build();
	}
}