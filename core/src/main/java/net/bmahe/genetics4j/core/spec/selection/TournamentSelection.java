package net.bmahe.genetics4j.core.spec.selection;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class TournamentSelection implements SelectionPolicy {

	public abstract int numCandidates();

	public static TournamentSelection build(final int numCandidates) {
		Validate.isTrue(numCandidates > 0);

		return new TournamentSelection() {

			@Override
			public int numCandidates() {
				return numCandidates;
			}
		};
	}
}