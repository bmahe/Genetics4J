package net.bmahe.genetics4j.core.spec.replacement;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.replacement.ImmutableDeleteNLast;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

/**
 * Delete N Last
 * <p>
 * This replacement strategy deletes the N weakest individuals and replace them
 * with the best offsprings
 * 
 */
@Value.Immutable
public interface DeleteNLast extends ReplacementStrategy {

	static final double DEFAULT_WEAK_RATIO = 0.05;

	/**
	 * How many weakest individuals to consider for replacement
	 * 
	 * @return
	 */
	@Value.Default
	@Value.Parameter
	default double weakRatio() {
		return DEFAULT_WEAK_RATIO;
	}

	/**
	 * Describe which offsprings to select for the next generation
	 * 
	 * @return
	 */
	@Value.Parameter
	public abstract SelectionPolicy offspringSelectionPolicy();

	@Value.Check
	default void check() {
		Validate.inclusiveBetween(0.0, 1.0, weakRatio());
	}

	class Builder extends ImmutableDeleteNLast.Builder {
	}

	static Builder builder() {
		return new Builder();
	}

	static DeleteNLast of(final double weakRatio, final SelectionPolicy selectionPolicy) {
		return builder().weakRatio(weakRatio).offspringSelectionPolicy(selectionPolicy).build();
	}
}