package net.bmahe.genetics4j.neat.spec.selection;

import java.util.function.BiPredicate;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.neat.NeatUtils;

@Value.Immutable
public abstract class NeatSelection<T extends Comparable<T>> implements SelectionPolicy {

	@Value.Default
	public float perSpeciesKeepRatio() {
		return 0.90f;
	}

	@Value.Default
	public int minSpeciesSize() {
		return 5;
	}

	public abstract BiPredicate<Individual<T>, Individual<T>> speciesPredicate();

	public abstract SelectionPolicy speciesSelection();

	@Value.Check
	public void check() {
		Validate.inclusiveBetween(0.0f, 1.0f, perSpeciesKeepRatio());
		Validate.isTrue(perSpeciesKeepRatio() > 0.0f);
	}

	public static class Builder<T extends Comparable<T>> extends ImmutableNeatSelection.Builder<T> {
	}

	public static <U extends Comparable<U>> Builder<U> builder() {
		return new Builder<U>();
	}

	public static <U extends Comparable<U>> NeatSelection<U> of(final float perSpeciesKeepRatio,
			final BiPredicate<Individual<U>, Individual<U>> speciesPredicate, final SelectionPolicy speciesSelection) {
		return new Builder<U>().perSpeciesKeepRatio(perSpeciesKeepRatio)
				.speciesPredicate(speciesPredicate)
				.speciesSelection(speciesSelection)
				.build();
	}

	public static <U extends Comparable<U>> NeatSelection<U> of(
			final BiPredicate<Individual<U>, Individual<U>> speciesPredicate, final SelectionPolicy speciesSelection) {
		return new Builder<U>().speciesPredicate(speciesPredicate)
				.speciesSelection(speciesSelection)
				.build();
	}

	/**
	 * Construct a default NeatSelection based on standard parameters:
	 * - Neat compatibility distance with standard coefficients of weight 1.0 and
	 * excess and disjoint genes of 2. As well as distance threshold of 1
	 * - Tournaments of 3 individuals
	 * 
	 * @param <U>
	 * @return
	 */
	public static <U extends Comparable<U>> NeatSelection<U> ofDefault() {
		return new Builder<U>()
				.speciesPredicate(
						(i1, i2) -> NeatUtils.compatibilityDistance(i1.genotype(), i2.genotype(), 0, 2, 2, 1f) < 1.0)
				.speciesSelection(Tournament.of(3))
				.build();
	}
}