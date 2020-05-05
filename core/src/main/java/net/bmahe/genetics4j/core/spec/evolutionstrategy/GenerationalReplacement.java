package net.bmahe.genetics4j.core.spec.evolutionstrategy;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

/**
 * Generational Replacement evolutional strategy
 * <p>
 * This strategy only retain the best offsprings to compose the next generation
 * of a population
 *
 */
@Value.Immutable
public interface GenerationalReplacement extends EvolutionStrategy {

	public abstract SelectionPolicy offspringSelectionPolicy();

	class Builder extends ImmutableGenerationalReplacement.Builder {
	}

	public static Builder builder() {
		return new Builder();
	}

}