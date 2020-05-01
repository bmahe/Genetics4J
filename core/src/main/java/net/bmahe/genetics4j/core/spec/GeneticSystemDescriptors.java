package net.bmahe.genetics4j.core.spec;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.selection.RouletteWheelSelectionPolicyHandler;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder;

public class GeneticSystemDescriptors {

	private GeneticSystemDescriptors() {
	}

	public static <T extends Comparable<T>> Builder<T> standard() {
		return ImmutableGeneticSystemDescriptor.<T>builder();
	}

	public static <T extends Number & Comparable<T>> Builder<T> enrichForScalarFitness(final Builder<T> builder) {
		Validate.notNull(builder);

		builder.addSelectionPolicyHandlerFactories(gsd -> new RouletteWheelSelectionPolicyHandler<T>(gsd.random()));
		return builder;
	}

	public static <T extends Number & Comparable<T>> Builder<T> forScalarFitness() {

		final Builder<T> builder = ImmutableGeneticSystemDescriptor.<T>builder();
		return enrichForScalarFitness(builder);
	}

}