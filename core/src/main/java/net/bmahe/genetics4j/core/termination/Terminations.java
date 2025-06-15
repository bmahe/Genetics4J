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

/**
 * Utility class providing factory methods for creating common termination conditions in evolutionary algorithms.
 * 
 * <p>Terminations provides a comprehensive set of pre-built termination criteria that can be used individually
 * or combined to create complex stopping conditions for evolutionary algorithms. Each method returns a
 * {@link Termination} instance that encapsulates the specific logic for determining when evolution should stop.
 * 
 * <p>Available termination criteria include:
 * <ul>
 * <li><strong>Generation-based</strong>: Stop after a maximum number of generations</li>
 * <li><strong>Time-based</strong>: Stop after a specified duration has elapsed</li>
 * <li><strong>Fitness-based</strong>: Stop when fitness reaches certain thresholds</li>
 * <li><strong>Convergence-based</strong>: Stop when fitness stops improving for a period</li>
 * <li><strong>Logical combinations</strong>: Combine multiple criteria with AND/OR logic</li>
 * </ul>
 * 
 * <p>Termination criteria can be combined to create sophisticated stopping conditions:
 * <pre>{@code
 * // Stop after 100 generations OR when fitness reaches 0.95 OR after 5 minutes
 * Termination<Double> complexTermination = Terminations.or(
 *     Terminations.ofMaxGeneration(100),
 *     Terminations.ofFitnessAtLeast(0.95),
 *     Terminations.ofMaxTime(Duration.ofMinutes(5))
 * );
 * 
 * // Stop only when BOTH conditions are met: good fitness AND stable evolution
 * Termination<Double> conservativeTermination = Terminations.and(
 *     Terminations.ofFitnessAtLeast(0.9),
 *     Terminations.ofStableFitness(20)
 * );
 * 
 * // Simple generation limit for quick experiments
 * Termination<Double> simpleTermination = Terminations.ofMaxGeneration(50);
 * }</pre>
 * 
 * <p>Common usage patterns:
 * <ul>
 * <li><strong>Development and testing</strong>: Use generation limits for quick experimentation</li>
 * <li><strong>Production systems</strong>: Combine time limits with fitness criteria for reliability</li>
 * <li><strong>Research applications</strong>: Use convergence detection to study algorithm behavior</li>
 * <li><strong>Resource-constrained environments</strong>: Use time-based limits for predictable execution</li>
 * </ul>
 * 
 * <p>Design considerations:
 * <ul>
 * <li><strong>Performance</strong>: Termination checks are called frequently; implementations are optimized</li>
 * <li><strong>Thread safety</strong>: Some termination criteria maintain internal state safely</li>
 * <li><strong>Flexibility</strong>: All criteria can be combined using logical operators</li>
 * <li><strong>Reliability</strong>: Include fallback termination criteria to prevent infinite loops</li>
 * </ul>
 * 
 * @see Termination
 * @see net.bmahe.genetics4j.core.EASystem
 * @see net.bmahe.genetics4j.core.spec.EAExecutionContext
 */
public class Terminations {

	/**
	 * Creates a termination condition that stops evolution after a specified number of generations.
	 * 
	 * <p>This is the most common termination criterion, providing a simple upper bound on the
	 * number of evolutionary cycles. The algorithm will terminate when the generation counter
	 * reaches or exceeds the specified maximum.
	 * 
	 * @param <T> the type of fitness values in the evolutionary algorithm
	 * @param maxGeneration the maximum number of generations to run (must be positive)
	 * @return a termination condition that stops after the specified number of generations
	 * @throws IllegalArgumentException if maxGeneration is not positive
	 */
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

	/**
	 * Creates a termination condition that stops evolution after a specified time duration.
	 * 
	 * <p>This time-based termination is useful for ensuring predictable execution times,
	 * especially in production environments or when computational resources are limited.
	 * The timer starts on the first evaluation and stops when the elapsed time exceeds
	 * the specified duration.
	 * 
	 * @param <T> the type of fitness values in the evolutionary algorithm
	 * @param duration the maximum time to run the algorithm
	 * @return a termination condition that stops after the specified duration
	 * @throws IllegalArgumentException if duration is null
	 */
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

	/**
	 * Creates a termination condition that requires ALL specified conditions to be met.
	 * 
	 * <p>This logical AND operation creates a conservative termination strategy where
	 * evolution continues until every provided termination criterion is satisfied.
	 * Useful for ensuring multiple quality conditions are met before stopping.
	 * 
	 * @param <T> the type of fitness values in the evolutionary algorithm
	 * @param terminations the termination conditions that must all be satisfied
	 * @return a termination condition that stops only when all conditions are met
	 * @throws IllegalArgumentException if terminations is null or empty
	 */
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

	/**
	 * Creates a termination condition that stops when ANY of the specified conditions is met.
	 * 
	 * <p>This logical OR operation creates a flexible termination strategy where
	 * evolution stops as soon as any one of the provided criteria is satisfied.
	 * Commonly used to provide multiple stopping conditions like time limits,
	 * generation limits, or fitness thresholds.
	 * 
	 * @param <T> the type of fitness values in the evolutionary algorithm
	 * @param terminations the termination conditions, any of which can trigger stopping
	 * @return a termination condition that stops when any condition is met
	 * @throws IllegalArgumentException if terminations is null or empty
	 */
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

	/**
	 * Creates a termination condition that stops when any individual reaches a minimum fitness threshold.
	 * 
	 * <p>This fitness-based termination is useful for maximization problems where you want
	 * to stop as soon as a solution of acceptable quality is found. The condition is satisfied
	 * when any individual in the population has a fitness value greater than or equal to the threshold.
	 * 
	 * @param <T> the type of fitness values in the evolutionary algorithm
	 * @param threshold the minimum fitness value required to trigger termination
	 * @return a termination condition that stops when fitness reaches the threshold
	 * @throws IllegalArgumentException if threshold is null
	 */
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

	/**
	 * Creates a termination condition that stops when any individual reaches a maximum fitness threshold.
	 * 
	 * <p>This fitness-based termination is useful for minimization problems where you want
	 * to stop as soon as a solution of acceptable quality is found. The condition is satisfied
	 * when any individual in the population has a fitness value less than or equal to the threshold.
	 * 
	 * @param <T> the type of fitness values in the evolutionary algorithm
	 * @param threshold the maximum fitness value required to trigger termination
	 * @return a termination condition that stops when fitness reaches the threshold
	 * @throws IllegalArgumentException if threshold is null
	 */
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
	 * Creates a termination condition that stops when fitness stops improving for a specified number of generations.
	 * 
	 * <p>This convergence-based termination detects when the evolutionary algorithm has reached
	 * a stable state where further evolution is unlikely to yield significant improvements.
	 * It tracks the best fitness value and stops evolution if no improvement is observed
	 * for the specified number of consecutive generations.
	 * 
	 * <p>This termination criterion is particularly useful for:
	 * <ul>
	 * <li>Preventing unnecessary computation when the algorithm has converged</li>
	 * <li>Automatically adapting to problem difficulty</li>
	 * <li>Research applications studying convergence behavior</li>
	 * </ul>
	 * 
	 * @param <T> the type of fitness values in the evolutionary algorithm
	 * @param stableGenerationsCount the number of generations without improvement required to trigger termination
	 * @return a termination condition that stops when fitness plateaus
	 * @throws IllegalArgumentException if stableGenerationsCount is not positive
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