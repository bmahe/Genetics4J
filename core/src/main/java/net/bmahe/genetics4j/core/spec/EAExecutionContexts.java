package net.bmahe.genetics4j.core.spec;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.RouletteWheelSelectionPolicyHandler;
import net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder;

/**
 * Defines multiple factory and helper methods to create and manage
 * EAExecutionContexts
 */
public class EAExecutionContexts {

	private EAExecutionContexts() {
	}

	public static <T extends Comparable<T>> Builder<T> standard() {
		return ImmutableEAExecutionContext.<T>builder();
	}

	/**
	 * Enrich an EAExecutionContext builder based on the knowledge of the fitness
	 * function returning a scalar value.
	 * <p>
	 * This will enrich it with additional implementations for selecting, combining
	 * and mutating individuals.
	 * 
	 * @param <T>     Type of the fitness measurement
	 * @param builder
	 * @return Enriched EAExecutionContext builder
	 */
	public static <T extends Number & Comparable<T>> Builder<T> enrichForScalarFitness(final Builder<T> builder) {
		Validate.notNull(builder);

		builder.addSelectionPolicyHandlerFactories(gsd -> new RouletteWheelSelectionPolicyHandler<T>(gsd.random()));
		return builder;
	}

	/**
	 * Create an EAExecutionContext builder based on the knowledge of the fitness
	 * function returning a scalar value.
	 * <p>
	 * This will enrich it with additional implementations for selecting, combining
	 * and mutating individuals.
	 * 
	 * @param <T>
	 * @return
	 */
	public static <T extends Number & Comparable<T>> Builder<T> forScalarFitness() {

		final Builder<T> builder = ImmutableEAExecutionContext.<T>builder();
		return enrichForScalarFitness(builder);
	}
}