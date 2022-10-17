package net.bmahe.genetics4j.neat.spec.selection;

import java.util.function.BiPredicate;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

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

	public static <U extends Comparable<U>> NeatSelection<U> build() {
		return ImmutableNeatSelection.<U>builder()
				.build();
	}

	public static <U extends Comparable<U>> NeatSelection<U> of(final float perSpeciesKeepRatio,
			final BiPredicate<Individual<U>, Individual<U>> speciesPredicate, final SelectionPolicy speciesSelection) {
		return ImmutableNeatSelection.<U>builder()
				.perSpeciesKeepRatio(perSpeciesKeepRatio)
				.speciesPredicate(speciesPredicate)
				.speciesSelection(speciesSelection)
				.build();
	}

	public static <U extends Comparable<U>> NeatSelection<U> of(
			final BiPredicate<Individual<U>, Individual<U>> speciesPredicate, final SelectionPolicy speciesSelection) {
		return ImmutableNeatSelection.<U>builder()
				.speciesPredicate(speciesPredicate)
				.speciesSelection(speciesSelection)
				.build();
	}

}