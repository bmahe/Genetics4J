package net.bmahe.genetics4j.core.spec.replacement;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.replacement.ImmutableGenerationalReplacement;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

/**
 * Generational Replacement strategy
 * <p>
 * This strategy only retain the best offsprings to compose the next generation
 * of a population
 *
 */
@Value.Immutable
public interface GenerationalReplacement extends ReplacementStrategy {

	public abstract SelectionPolicy offspringSelectionPolicy();

	class Builder extends ImmutableGenerationalReplacement.Builder {
	}

	public static Builder builder() {
		return new Builder();
	}

}