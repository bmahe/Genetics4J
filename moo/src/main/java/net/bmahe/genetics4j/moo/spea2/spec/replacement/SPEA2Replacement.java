package net.bmahe.genetics4j.moo.spea2.spec.replacement;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.replacement.ReplacementStrategy;
import net.bmahe.genetics4j.moo.FitnessVector;

@Value.Immutable
public abstract class SPEA2Replacement<T extends Comparable<T>> implements ReplacementStrategy {

	/**
	 * Defines the Pareto dominance relation
	 * 
	 * @return
	 */
	@Value.Default
	public Comparator<T> dominance() {
		return (a, b) -> a.compareTo(b);
	}

	/**
	 * Comparator used for deduplication of solution prior to processing
	 * <p>
	 * If not specified, it defaults to not do any deduplication
	 * 
	 * @return
	 */
	@Value.Default
	public Optional<Comparator<Genotype>> deduplicate() {
		return Optional.empty();
	}

	/**
	 * Determine the k-nearest distance to compute.
	 * <p>
	 * It will default to sqrt(|archive| + |population|)
	 * 
	 * @return
	 */
	@Value.Default
	public Optional<Integer> k() {
		return Optional.empty();
	}

	/**
	 * Define how to compute distances in objective space between two solutions
	 * 
	 * @return Distance
	 */
	@Value.Parameter
	public abstract BiFunction<T, T, Double> distance();

	public static class Builder<T extends Comparable<T>> extends ImmutableSPEA2Replacement.Builder<T> {
	}

	public static <U extends Comparable<U>> Builder<U> builder() {
		return new Builder<U>();
	}

	/**
	 * Factory method to instantiate a SPEA2Selection when fitness is defined as a
	 * FitnessVector of a Number
	 * 
	 * @param <U>         Type of the fitness measurement
	 * @param deduplicate Deduplicator comparator. Null value with disable
	 *                    deduplication
	 * @return A new instance of SPEA2Replacement
	 */
	public static <U extends Number & Comparable<U>> SPEA2Replacement<FitnessVector<U>>
			ofFitnessVector(final Comparator<Genotype> deduplicate) {

		final var builder = new Builder<FitnessVector<U>>();
		builder.deduplicate(Optional.ofNullable(deduplicate));

		builder.distance((fv1, fv2) -> {

			final int dimensions = fv1.dimensions();

			double sum = 0.0;
			for (int i = 0; i < dimensions; i++) {
				final double v1 = fv1.get(i).doubleValue();
				final double v2 = fv2.get(i).doubleValue();

				sum += (v2 - v1) * (v2 - v1);
			}

			return Math.sqrt(sum);
		});

		return builder.build();
	}

	/**
	 * Factory method to instantiate a SPEA2Selection when fitness is defined as a
	 * FitnessVector of a Number
	 * 
	 * @param <U> Type of the fitness measurement
	 * @return A new instance of SPEA2Replacement
	 */
	public static <U extends Number & Comparable<U>> SPEA2Replacement<FitnessVector<U>> ofFitnessVector() {

		return ofFitnessVector(null);
	}
}