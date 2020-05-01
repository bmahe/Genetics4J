package net.bmahe.genetics4j.core.termination;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;

public class Terminations {

	public static <T extends Comparable<T>> Termination<T> ofMaxGeneration(final long maxGeneration) {
		Validate.isTrue(maxGeneration > 0);

		return new Termination<T>() {

			@Override
			public boolean isDone(final long generation, final Genotype[] population, final List<T> fitness) {
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
			public boolean isDone(final long generation, final Genotype[] population, final List<T> fitness) {
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
	public static <T extends Comparable<T>> Termination<T> or(final Termination<T>... terminations) {
		Validate.notNull(terminations);
		Validate.isTrue(terminations.length > 0);

		return new Termination<T>() {

			@Override
			public boolean isDone(final long generation, final Genotype[] population, final List<T> fitness) {
				return Arrays.stream(terminations)
						.anyMatch((termination) -> termination.isDone(generation, population, fitness));
			}

		};
	}

	public static <T extends Comparable<T>> Termination<T> ofFitnessAtLeast(final T threshold) {
		Validate.notNull(threshold);
		return new Termination<T>() {

			@Override
			public boolean isDone(final long generation, final Genotype[] population, final List<T> fitness) {
				Validate.isTrue(generation >= 0);

				return fitness.stream().anyMatch((fitnessValue) -> threshold.compareTo(fitnessValue) <= 0);
			}
		};
	}

	public static <T extends Comparable<T>> Termination<T> ofFitnessAtMost(final T threshold) {
		Validate.notNull(threshold);
		return new Termination<T>() {

			@Override
			public boolean isDone(final long generation, final Genotype[] population, final List<T> fitness) {
				Validate.isTrue(generation >= 0);

				return fitness.stream().anyMatch((fitnessValue) -> threshold.compareTo(fitnessValue) >= 0);
			}
		};
	}

}