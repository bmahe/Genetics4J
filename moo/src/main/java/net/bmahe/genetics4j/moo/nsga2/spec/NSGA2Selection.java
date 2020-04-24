package net.bmahe.genetics4j.moo.nsga2.spec;

import java.util.Comparator;
import java.util.function.Function;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

@Value.Immutable
public abstract class NSGA2Selection<T> implements SelectionPolicy {

	@Value.Parameter
	public abstract int numberObjectives();

	@Value.Parameter
	public abstract Comparator<T> dominance();

	/**
	 * Sort T based on the objective passed as a parameter
	 * 
	 * @return
	 */
	@Value.Parameter
	public abstract Function<Integer, Comparator<T>> objectiveComparator();

	@Value.Parameter
	public abstract ObjectiveDistance<T> distance();
}