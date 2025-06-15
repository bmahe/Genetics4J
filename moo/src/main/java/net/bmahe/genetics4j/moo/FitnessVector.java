package net.bmahe.genetics4j.moo;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;

/**
 * Represents a multi-objective fitness vector for multi-objective optimization (MOO).
 * 
 * <p>A FitnessVector encapsulates multiple fitness values, each representing the quality
 * of a solution in a different objective dimension. This is essential for multi-objective
 * evolutionary algorithms like NSGA-II and SPEA-II where solutions are evaluated against
 * multiple, often conflicting objectives.
 * 
 * <p>The fitness vector provides:
 * <ul>
 * <li>Storage for multiple objective values</li>
 * <li>Custom comparators for each objective (minimization/maximization)</li>
 * <li>Pareto dominance comparison logic</li>
 * <li>Type-safe access to individual objective values</li>
 * </ul>
 * 
 * <p>Pareto dominance comparison rules:
 * <ul>
 * <li>Vector A dominates B if A is better in all objectives</li>
 * <li>Vector A weakly dominates B if A is better or equal in all objectives and better in at least one</li>
 * <li>Vectors are non-dominated if neither dominates the other</li>
 * </ul>
 * 
 * @param <T> the type of the fitness values, must be comparable
 * @see net.bmahe.genetics4j.moo.nsga2
 * @see net.bmahe.genetics4j.moo.spea2
 * @see ParetoUtils
 */
public class FitnessVector<T extends Comparable<T>> implements Comparable<FitnessVector<T>> {

	private final List<T> fitnesses;
	private final List<Comparator<T>> comparators;

	/**
	 * Creates a fitness vector with the specified values and comparators.
	 * 
	 * @param _vector the fitness values for each objective
	 * @param _comparators the comparators defining optimization direction for each objective
	 * @throws IllegalArgumentException if vectors are null, empty, or have different sizes
	 */
	public FitnessVector(final Collection<T> _vector, final Collection<Comparator<T>> _comparators) {
		Validate.notNull(_vector);
		Validate.isTrue(_vector.size() > 0);
		Validate.notNull(_comparators);
		Validate.isTrue(_vector.size() == _comparators.size());

		fitnesses = List.copyOf(_vector);
		comparators = List.copyOf(_comparators);
	}

	/**
	 * Creates a fitness vector with natural ordering for all objectives.
	 * 
	 * <p>All objectives will use natural ordering (ascending), which means
	 * smaller values are considered better (minimization).
	 * 
	 * @param _vector the fitness values for each objective
	 * @throws IllegalArgumentException if vector is null or empty
	 */
	public FitnessVector(final Collection<T> _vector) {
		this(_vector,
				IntStream.range(0, _vector.size())
						.boxed()
						.map((i) -> Comparator.<T>naturalOrder())
						.collect(Collectors.toList()));
	}

	/**
	 * Creates a fitness vector from variable arguments with natural ordering.
	 * 
	 * @param _vector the fitness values for each objective
	 * @throws IllegalArgumentException if vector is empty
	 */
	public FitnessVector(T... _vector) {
		this(List.of(_vector));
	}

	/**
	 * Returns the number of objectives in this fitness vector.
	 * 
	 * @return the number of objectives
	 */
	public int dimensions() {
		return fitnesses.size();
	}

	/**
	 * Returns the fitness value for the specified objective.
	 * 
	 * @param index the index of the objective (0-based)
	 * @return the fitness value for the specified objective
	 * @throws IllegalArgumentException if index is out of bounds
	 */
	public T get(final int index) {
		Validate.exclusiveBetween(-1, fitnesses.size(), index);

		return fitnesses.get(index);
	}

	/**
	 * Returns the comparator for the specified objective.
	 * 
	 * @param index the index of the objective (0-based)
	 * @return the comparator defining optimization direction for the objective
	 * @throws IllegalArgumentException if index is out of bounds
	 */
	public Comparator<T> getComparator(final int index) {
		Validate.exclusiveBetween(-1, fitnesses.size(), index);

		return comparators.get(index);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comparators == null) ? 0 : comparators.hashCode());
		result = prime * result + ((fitnesses == null) ? 0 : fitnesses.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FitnessVector other = (FitnessVector) obj;
		if (comparators == null) {
			if (other.comparators != null)
				return false;
		} else if (!comparators.equals(other.comparators))
			return false;
		if (fitnesses == null) {
			if (other.fitnesses != null)
				return false;
		} else if (!fitnesses.equals(other.fitnesses))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FitnessVector [fitnesses=" + fitnesses + "]";
	}

	@Override
	public int compareTo(final FitnessVector<T> o) {
		return compare(this, o);
	}

	/**
	 * Compares two fitness vectors using Pareto dominance.
	 * 
	 * <p>This method implements Pareto dominance comparison:
	 * <ul>
	 * <li>Returns positive value if fv1 dominates fv2 (fv1 is better in all objectives)</li>
	 * <li>Returns negative value if fv2 dominates fv1 (fv2 is better in all objectives)</li>
	 * <li>Returns 0 if vectors are non-dominated (neither dominates the other)</li>
	 * </ul>
	 * 
	 * @param <U> the type of the fitness values
	 * @param fv1 the first fitness vector to compare
	 * @param fv2 the second fitness vector to compare
	 * @return positive if fv1 dominates fv2, negative if fv2 dominates fv1, 0 if non-dominated
	 * @throws IllegalArgumentException if vectors are null or have different dimensions
	 */
	public static <U extends Comparable<U>> int compare(final FitnessVector<U> fv1, final FitnessVector<U> fv2) {
		Validate.notNull(fv1);
		Validate.notNull(fv2);

		if (fv1.dimensions() != fv2.dimensions()) {
			throw new IllegalArgumentException("Can't compare FitnessVector with different dimensions");
		}

		int greater = 0;
		int lesser = 0;

		for (int i = 0; i < fv1.dimensions(); i++) {
			final U d1 = fv1.get(i);
			final U d2 = fv2.get(i);

			final int compared = fv1.comparators.get(i).compare(d1, d2);

			if (compared < 0) {
				lesser++;
			} else if (compared > 0) {
				greater++;
			}
		}

		if (lesser == 0 && greater > 0) {
			return 1;
		}
		if (greater == 0 && lesser > 0) {
			return -1;
		}
		return 0;
	}
}