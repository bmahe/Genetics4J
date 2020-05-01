package net.bmahe.genetics4j.core.spec;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.RouletteWheelSelectionPolicyHandler;
import net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder;

public class EAExecutionContexts {

	private EAExecutionContexts() {
	}

	public static <T extends Comparable<T>> Builder<T> standard() {
		return ImmutableEAExecutionContext.<T>builder();
	}

	public static <T extends Number & Comparable<T>> Builder<T> enrichForScalarFitness(final Builder<T> builder) {
		Validate.notNull(builder);

		builder.addSelectionPolicyHandlerFactories(gsd -> new RouletteWheelSelectionPolicyHandler<T>(gsd.random()));
		return builder;
	}

	public static <T extends Number & Comparable<T>> Builder<T> forScalarFitness() {

		final Builder<T> builder = ImmutableEAExecutionContext.<T>builder();
		return enrichForScalarFitness(builder);
	}

}