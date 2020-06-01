package net.bmahe.genetics4j.core.spec.replacement;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.replacement.ImmutableElitism;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

/**
 * Specify an elitism based replacement strategy
 * <p>
 * Elitism will retain the best individuals of both offsprings and survivors of
 * the previous generation.
 */
@Value.Immutable
public interface Elitism extends ReplacementStrategy {
	static final double DEFAULT_OFFSPRING_RATIO = 0.95;

	/**
	 * Describe which offsprings to select for the next generation
	 * 
	 * @return
	 */
	public abstract SelectionPolicy offspringSelectionPolicy();

	/**
	 * Describe which survivors to select for the next generation
	 * 
	 * @return
	 */
	public abstract SelectionPolicy survivorSelectionPolicy();

	/**
	 * Defines how many children will be generated at each iteration. Value must be
	 * between 0 and 1 (inclusive)
	 * <p>
	 * The number of survivor will be the complement of it, or 1 - offspringRatio()
	 * 
	 * @return
	 */
	@Value.Default
	default double offspringRatio() {
		return DEFAULT_OFFSPRING_RATIO;
	}

	@Value.Check
	default void check() {
		Validate.inclusiveBetween(0.0, 1.0, offspringRatio());
	}

	class Builder extends ImmutableElitism.Builder {
	}

	public static Builder builder() {
		return new Builder();
	}
}