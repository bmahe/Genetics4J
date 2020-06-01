package net.bmahe.genetics4j.moo;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;

public class FitnessVector<T extends Comparable<T>> implements Comparable<FitnessVector<T>> {

	private final List<T> fitnesses;
	private final List<Comparator<T>> comparators;

	public FitnessVector(final Collection<T> _vector, final Collection<Comparator<T>> _comparators) {
		Validate.notNull(_vector);
		Validate.isTrue(_vector.size() > 0);
		Validate.notNull(_comparators);
		Validate.isTrue(_vector.size() == _comparators.size());

		fitnesses = List.copyOf(_vector);
		comparators = List.copyOf(_comparators);
	}

	public FitnessVector(final Collection<T> _vector) {
		this(_vector,
				IntStream.range(0, _vector.size())
						.boxed()
						.map((i) -> Comparator.<T>naturalOrder())
						.collect(Collectors.toList()));
	}

	public FitnessVector(T... _vector) {
		this(List.of(_vector));
	}

	public int dimensions() {
		return fitnesses.size();
	}

	public T get(final int index) {
		Validate.exclusiveBetween(-1, fitnesses.size(), index);

		return fitnesses.get(index);
	}

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