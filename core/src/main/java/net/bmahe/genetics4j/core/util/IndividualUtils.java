package net.bmahe.genetics4j.core.util;

import java.util.Comparator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

public class IndividualUtils {

	private IndividualUtils() {
	}

	public static <T extends Comparable<T>> Comparator<Individual<T>> fitnessBasedComparator(
			final AbstractEAConfiguration<T> eaConfiguration) {
		Validate.notNull(eaConfiguration);

		final Comparator<Individual<T>> individualComparator = Comparator.comparing(Individual<T>::fitness);

		return switch (eaConfiguration.optimization()) {
			case MAXIMIZE -> individualComparator;
			case MINIMIZE -> individualComparator.reversed();
		};
	}
}