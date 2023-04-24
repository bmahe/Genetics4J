package net.bmahe.genetics4j.core.util;

import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.spec.statistics.distributions.NormalDistribution;
import net.bmahe.genetics4j.core.spec.statistics.distributions.UniformDistribution;

public class DistributionUtils {

	public static Supplier<Double> distributionValueSupplier(final RandomGenerator randomGenerator,
			final double minValue, final double maxValue, final Distribution distribution) {
		Validate.notNull(randomGenerator);
		Validate.notNull(distribution);
		Validate.isTrue(minValue <= maxValue);

		if (distribution instanceof UniformDistribution) {
			final double valueRange = maxValue - minValue;

			return () -> minValue + randomGenerator.nextDouble() * valueRange;
		}

		if (distribution instanceof NormalDistribution) {
			final var normalDistribution = (NormalDistribution) distribution;
			final double mean = normalDistribution.mean();
			final double standardDeviation = normalDistribution.standardDeviation();

			return () -> {
				final double value = mean + randomGenerator.nextGaussian() * standardDeviation;

				if (value < minValue) {
					return minValue;
				} else if (value > maxValue) {
					return maxValue;
				}

				return value;
			};

		}

		throw new IllegalArgumentException(String.format("Distribution not supported: %s", distribution));
	}

	public static Supplier<Float> distributionFloatValueSupplier(final RandomGenerator randomGenerator,
			final float minValue, final float maxValue, final Distribution distribution) {
		Validate.notNull(randomGenerator);
		Validate.notNull(distribution);
		Validate.isTrue(minValue <= maxValue);

		if (distribution instanceof UniformDistribution) {
			final float valueRange = maxValue - minValue;

			return () -> minValue + randomGenerator.nextFloat() * valueRange;
		} else if (distribution instanceof NormalDistribution) {
			final var normalDistribution = (NormalDistribution) distribution;
			final double mean = normalDistribution.mean();
			final double standardDeviation = normalDistribution.standardDeviation();

			return () -> {
				final float value = (float) (mean + randomGenerator.nextGaussian() * standardDeviation);

				if (value < minValue) {
					return minValue;
				} else if (value > maxValue) {
					return maxValue;
				}

				return value;
			};
		}

		throw new IllegalArgumentException(String.format("Distribution not supported: %s", distribution));
	}
}