package net.bmahe.genetics4j.core.spec.selection;

import java.util.Comparator;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Individual;

@Value.Immutable
public abstract class DoubleTournament<T extends Comparable<T>> implements SelectionPolicy {

	@Value.Parameter
	public abstract Tournament<T> fitnessTournament();

	@Value.Parameter
	public abstract Comparator<Individual<T>> parsimonyComparator();

	@Value.Parameter
	public abstract double parsimonyTournamentSize();

	@Value.Default
	public boolean doFitnessFirst() {
		return true;
	}

	@Value.Check
	public void check() {
		Validate.inclusiveBetween(0.0d, 2.0d, parsimonyTournamentSize());
	}

	public static <U extends Comparable<U>> DoubleTournament<U> of(final Tournament<U> fitnessTournament,
			final Comparator<Individual<U>> parsimonyComparator, final double parsimonyTournamentSize) {
		return ImmutableDoubleTournament.of(fitnessTournament, parsimonyComparator, parsimonyTournamentSize);
	}
}