package net.bmahe.genetics4j.core.spec.chromosome;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.spec.statistics.distributions.UniformDistribution;

@Value.Immutable
public abstract class DoubleChromosomeSpec implements ChromosomeSpec {

	@Value.Parameter
	public abstract int size();

	@Value.Parameter
	public abstract double minValue();

	@Value.Parameter
	public abstract double maxValue();

	@Value.Default
	public Distribution distribution() {
		return UniformDistribution.build();
	}

	@Value.Check
	protected void check() {
		Validate.isTrue(size() > 0);
		Validate.isTrue(minValue() <= maxValue());
	}

	public static class Builder extends ImmutableDoubleChromosomeSpec.Builder {
	}

	/**
	 * Construct a new immutable {@code DoubleChromosomeSpec} instance.
	 * 
	 * @param size     The value for the {@code size} attribute
	 * @param minValue The value for the {@code minValue} attribute
	 * @param maxValue The value for the {@code maxValue} attribute
	 * @return An immutable DoubleChromosomeSpec instance
	 */

	public static DoubleChromosomeSpec of(final int size, final double minValue, final double maxValue) {
		return ImmutableDoubleChromosomeSpec.of(size, minValue, maxValue);
	}

	public static DoubleChromosomeSpec of(final int size, final double minValue, final double maxValue,
			final Distribution distribution) {
		Validate.notNull(distribution);

		final var doubleChromosomeSpecBuilder = new ImmutableDoubleChromosomeSpec.Builder();

		doubleChromosomeSpecBuilder.size(size).minValue(minValue).maxValue(maxValue).distribution(distribution);

		return doubleChromosomeSpecBuilder.build();
	}
}