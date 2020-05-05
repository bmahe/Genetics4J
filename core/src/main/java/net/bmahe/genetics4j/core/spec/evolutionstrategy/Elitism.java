package net.bmahe.genetics4j.core.spec.evolutionstrategy;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

/**
 * Specify an elitism based evolution strategy
 * <p>
 * Elitism will retain the best individuals of both offsprings and survivors of
 * the previous generation.
 */
@Value.Immutable
public interface Elitism extends EvolutionStrategy {
	public static final double DEFAULT_OFFSPRING_RATIO = 0.95;

	public abstract SelectionPolicy offspringSelectionPolicy();

	public abstract SelectionPolicy survivorSelectionPolicy();

	/**
	 * Defines how many children will be generated at each iteration. Value must be
	 * between 0 and 1 (inclusive)
	 * 
	 * @return
	 */
	@Value.Default
	default double offspringRatio() {
		return DEFAULT_OFFSPRING_RATIO;
	}

	class Builder extends ImmutableElitism.Builder {
	}

	public static Builder builder() {
		return new Builder();
	}
}