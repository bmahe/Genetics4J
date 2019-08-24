package net.bmahe.genetics4j.core.spec.chromosome;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class IntChromosomeSpec implements ChromosomeSpec {

	@Value.Parameter
	public abstract int size();

	@Value.Parameter
	public abstract int minValue();

	@Value.Parameter
	public abstract int maxValue();

	@Value.Check
	protected void check() {
		Validate.isTrue(size() > 0);
		Validate.isTrue(minValue() <= maxValue());
	}

	public static class Builder extends ImmutableIntChromosomeSpec.Builder {
	}

	/**
	 * Construct a new immutable {@code IntChromosomeSpec} instance.
	 * 
	 * @param size     The value for the {@code size} attribute
	 * @param minValue The value for the {@code minValue} attribute
	 * @param maxValue The value for the {@code maxValue} attribute
	 * @return An immutable IntChromosomeSpec instance
	 */
	public static IntChromosomeSpec of(final int size, final int minValue, final int maxValue) {
		return ImmutableIntChromosomeSpec.of(size, minValue, maxValue);
	}

}