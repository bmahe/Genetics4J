package net.bmahe.genetics4j.core.spec.selection;

import java.util.Comparator;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Individual;

@Value.Style(overshadowImplementation = true)
@Value.Immutable
public interface ProportionalTournament<T extends Comparable<T>> extends SelectionPolicy {

	@Value.Parameter
	int numCandidates();

	@Value.Parameter
	double proportionFirst();

	@Value.Parameter
	Comparator<Individual<T>> firstComparator();

	@Value.Parameter
	Comparator<Individual<T>> secondComparator();

	@Value.Check
	default void check() {
		Validate.inclusiveBetween(0.0, 1.0, proportionFirst());
	}

	public static <T extends Comparable<T>> ProportionalTournament<T> of(int numCandidates, double proportionFirst,
			Comparator<Individual<T>> firstComparator, Comparator<Individual<T>> secondComparator) {
		return ImmutableProportionalTournament.of(numCandidates, proportionFirst, firstComparator, secondComparator);
	}

}