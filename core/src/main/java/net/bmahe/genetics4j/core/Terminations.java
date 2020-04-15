package net.bmahe.genetics4j.core;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.commons.lang3.Validate;

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
}