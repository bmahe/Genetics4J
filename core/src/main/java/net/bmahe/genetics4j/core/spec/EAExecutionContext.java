package net.bmahe.genetics4j.core.spec;

import org.immutables.value.Value;

/**
 * Evolutionary Algorithm - Execution Context
 * <p>
 * This defines how the Evolutionary Algorithm will be executed.
 *
 * @param <T> Type of the fitness measurement
 */
@Value.Immutable
public abstract class EAExecutionContext<T extends Comparable<T>> extends AbstractEAExecutionContext<T> {

	@Value.Default
	public int numberOfPartitions() {
		return Runtime.getRuntime()
				.availableProcessors();
	}

	public static <U extends Comparable<U>> ImmutableEAExecutionContext.Builder<U> builder() {
		return ImmutableEAExecutionContext.builder();
	}
}