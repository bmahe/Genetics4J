package net.bmahe.genetics4j.moo.nsga2.spec;

import java.util.Comparator;
import java.util.function.Function;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

@Value.Immutable
public interface TournamentNSGA2Selection<T> extends SelectionPolicy {
	@Value.Parameter
	int numberObjectives();

	@Value.Parameter
	Comparator<T> dominance();

	/**
	 * Sort T based on the objective passed as a parameter
	 * 
	 * @return
	 */
	@Value.Parameter
	Function<Integer, Comparator<T>> objectiveComparator();

	@Value.Parameter
	ObjectiveDistance<T> distance();

	@Value.Parameter
	int numCandidates();
}