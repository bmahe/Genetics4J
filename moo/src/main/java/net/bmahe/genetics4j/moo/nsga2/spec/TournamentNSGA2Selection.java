package net.bmahe.genetics4j.moo.nsga2.spec;

import java.util.Comparator;
import java.util.function.Function;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.moo.FitnessVector;

/**
 * Tournament based NSGA2 selection
 * <p>
 * This method of selection follows the method describe in the NSGA2 paper where
 * the individuals are sorted according to NSGA2 and then selected through
 * tournaments where they are compared based on their NSGA2 metric
 *
 * @param <T> Type of the fitness measurement
 */
@Value.Immutable
public abstract class TournamentNSGA2Selection<T extends Comparable<T>> implements SelectionPolicy {

	/**
	 * Describe how many objectives are embedded in T
	 * 
	 * @return Number of objectives embedded in T
	 */
	@Value.Parameter
	public abstract int numberObjectives();

	/**
	 * Comparator for dominance
	 * 
	 * @return Comparator for dominance
	 */
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

	/**
	 * Number of candidates in each tournament
	 * 
	 * @return Number of candidates in each tournament
	 */
	@Value.Parameter
	public abstract int numCandidates();

	public static class Builder<T extends Comparable<T>> extends ImmutableTournamentNSGA2Selection.Builder<T> {
	}

	public static <U extends Comparable<U>> Builder<U> builder() {
		return new Builder<U>();
	}

	/**
	 * Factory method to instantiate a Tournament based NSGA2 selection when fitness
	 * is defined as a FitnessVector of a Number
	 * 
	 * @param <U>              Type of the fitness measurement
	 * @param numberObjectives Number of objectives and dimensions of the
	 *                         FitnessVector
	 * @param numberCandidates Number of candidates in each tournament
	 * @return A new instance of TournamentNSGA2Selection
	 */
	public static <U extends Number & Comparable<U>> TournamentNSGA2Selection<FitnessVector<U>>
			ofFitnessVector(final int numberObjectives, final int numberCandidates) {

		final var builder = new Builder<FitnessVector<U>>();

		builder.objectiveComparator((m) -> (a, b) -> Double.compare(a.get(m).doubleValue(), b.get(m).doubleValue()))
				.distance((a, b, m) -> b.get(m).doubleValue() - a.get(m).doubleValue())
				.numberObjectives(numberObjectives)
				.numCandidates(numberCandidates);

		return builder.build();
	}
}