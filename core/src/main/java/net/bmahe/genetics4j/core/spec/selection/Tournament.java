package net.bmahe.genetics4j.core.spec.selection;

import java.util.Comparator;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Individual;

@Value.Style(overshadowImplementation = true)
@Value.Immutable
public abstract class Tournament<T extends Comparable<T>> implements SelectionPolicy {

	@Value.Parameter
	public abstract int numCandidates();

	@Value.Default
	public Comparator<Individual<T>> comparator() {
		return Comparator.comparing(Individual::fitness);
	}

	/*
	 * TODO: Enabling this makes immutables create non-compilable code
	 * 
	 * @Value.Check
	 * protected void check() {
	 * Validate.isTrue(numCandidates() > 0);
	 * }
	 */

	public static class Builder<T extends Comparable<T>> extends ImmutableTournament.Builder<T> {
	}

	public static <U extends Comparable<U>> Tournament<U> of(final int numCandidates) {
		return ImmutableTournament.of(numCandidates);
	}

	public static <U extends Comparable<U>> Tournament<U> of(final int numCandidates,
			final Comparator<Individual<U>> comparator) {
		return new Builder<U>().numCandidates(numCandidates).comparator(comparator).build();
	}

}