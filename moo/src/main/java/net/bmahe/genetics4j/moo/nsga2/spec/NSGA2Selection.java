package net.bmahe.genetics4j.moo.nsga2.spec;

import java.util.Comparator;
import java.util.function.Function;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.moo.FitnessVector;

/**
 * NSGA2 Selection specification
 * <p>
 * Select individuals based on their NSGA2 score, going from the most dominating
 * ones to the lesser ones
 * 
 * @param <T> Type of the fitness measurement
 */
@Value.Immutable
public abstract class NSGA2Selection<T extends Comparable<T>> implements SelectionPolicy {

	@Value.Parameter
	public abstract int numberObjectives();

	@Value.Default
	public Comparator<T> dominance() {
		return (a, b) -> a.compareTo(b);
	}

	/**
	 * Sort T based on the objective passed as a parameter
	 * 
	 * @return
	 */
	@Value.Parameter
	public abstract Function<Integer, Comparator<T>> objectiveComparator();

	/**
	 * Define how to compute distances between fitness scores along their objectives
	 * 
	 * @return Distance computation method
	 */
	@Value.Parameter
	public abstract ObjectiveDistance<T> distance();

	public static class Builder<T extends Comparable<T>> extends ImmutableNSGA2Selection.Builder<T> {
	}

	public static <U extends Comparable<U>> Builder<U> builder() {
		return new Builder<U>();
	}

	/**
	 * Factory method to instantiate a NSGA2Selection when fitness is defined as a
	 * FitnessVector of a Number
	 * 
	 * @param <U>              Type of the fitness measurement
	 * @param numberObjectives Number of objectives and dimensions of the
	 *                         FitnessVector
	 * @return A new instance of TournamentNSGA2Selection
	 */
	public static <U extends Number & Comparable<U>> NSGA2Selection<FitnessVector<U>>
			ofFitnessVector(final int numberObjectives) {

		final var builder = new Builder<FitnessVector<U>>();

		builder.objectiveComparator((m) -> (a, b) -> Double.compare(a.get(m).doubleValue(), b.get(m).doubleValue()))
				.distance((a, b, m) -> b.get(m).doubleValue() - a.get(m).doubleValue())
				.numberObjectives(numberObjectives);

		return builder.build();
	}
}