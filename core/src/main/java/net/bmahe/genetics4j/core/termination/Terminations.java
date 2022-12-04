package net.bmahe.genetics4j.core.termination;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

public class Terminations {

	public static <T extends Comparable<T>> Termination<T> ofMaxGeneration(final long maxGeneration) {
		Validate.isTrue(maxGeneration > 0);

		return new Termination<T>() {

			@Override
			public boolean isDone(final AbstractEAConfiguration<T> eaConfiguration, final long generation,
					final List<Genotype> population, final List<T> fitness) {
				Validate.isTrue(generation >= 0);

				return generation >= maxGeneration;
			}
		};
	}

	public static <T extends Comparable<T>> Termination<T> ofMaxTime(final Duration duration) {
		Validate.notNull(duration);

		return new Termination<T>() {

			private final long durationNanos = duration.get(ChronoUnit.NANOS);
			private Long startTime = null;

			@Override
			public boolean isDone(final AbstractEAConfiguration<T> eaConfiguration, final long generation,
					final List<Genotype> population, final List<T> fitness) {
				Validate.isTrue(generation >= 0);

				final long nowNanos = System.nanoTime();

				if (startTime == null) {
					startTime = nowNanos;
				}

				return nowNanos - startTime >= durationNanos;
			}
		};
	}

	@SafeVarargs
	public static <T extends Comparable<T>> Termination<T> and(final Termination<T>... terminations) {
		Validate.notNull(terminations);
		Validate.isTrue(terminations.length > 0);

		return new Termination<T>() {

			@Override
			public boolean isDone(final AbstractEAConfiguration<T> eaConfiguration, final long generation,
					final List<Genotype> population, final List<T> fitness) {
				return Arrays.stream(terminations)
						.allMatch((termination) -> termination.isDone(eaConfiguration, generation, population, fitness));
			}

		};
	}

	@SafeVarargs
	public static <T extends Comparable<T>> Termination<T> or(final Termination<T>... terminations) {
		Validate.notNull(terminations);
		Validate.isTrue(terminations.length > 0);

		return new Termination<T>() {

			@Override
			public boolean isDone(final AbstractEAConfiguration<T> eaConfiguration, final long generation,
					final List<Genotype> population, final List<T> fitness) {
				return Arrays.stream(terminations)
						.anyMatch((termination) -> termination.isDone(eaConfiguration, generation, population, fitness));
			}

		};
	}

	public static <T extends Comparable<T>> Termination<T> ofFitnessAtLeast(final T threshold) {
		Validate.notNull(threshold);
		return new Termination<T>() {

			@Override
			public boolean isDone(final AbstractEAConfiguration<T> eaConfiguration, final long generation,
					final List<Genotype> population, final List<T> fitness) {
				Validate.isTrue(generation >= 0);

				return fitness.stream()
						.anyMatch((fitnessValue) -> threshold.compareTo(fitnessValue) <= 0);
			}
		};
	}

	public static <T extends Comparable<T>> Termination<T> ofFitnessAtMost(final T threshold) {
		Validate.notNull(threshold);
		return new Termination<T>() {

			@Override
			public boolean isDone(final AbstractEAConfiguration<T> eaConfiguration, final long generation,
					final List<Genotype> population, final List<T> fitness) {
				Validate.isTrue(generation >= 0);

				return fitness.stream()
						.anyMatch((fitnessValue) -> threshold.compareTo(fitnessValue) >= 0);
			}
		};
	}

	/**
	 * Will terminate if the fitness does not improve over a specified number of
	 * generations
	 *
	 * @param <T>
	 * @param stableGenerationsCount
	 * @return
	 */
	public static <T extends Comparable<T>> Termination<T> ofStableFitness(final int stableGenerationsCount) {
		Validate.isTrue(stableGenerationsCount > 0);

		return new Termination<T>() {

			private long lastImprovedGeneration = -1;
			private T lastBestFitness = null;

			@Override
			public boolean isDone(final AbstractEAConfiguration<T> eaConfiguration, final long generation,
					final List<Genotype> population, final List<T> fitness) {
				Validate.isTrue(generation >= 0);

				final Comparator<T> fitnessComparator = eaConfiguration.fitnessComparator();

				final Optional<T> bestFitnessOpt = fitness.stream()
						.max(fitnessComparator);

				if (lastImprovedGeneration < 0
						|| bestFitnessOpt.map(bestFitness -> fitnessComparator.compare(bestFitness, lastBestFitness) > 0)
								.orElse(false)) {
					lastImprovedGeneration = generation;
					lastBestFitness = bestFitnessOpt.get();
				}

				if (generation - lastImprovedGeneration > stableGenerationsCount) {
					return true;
				}
				return false;
			}
		};
	}
}