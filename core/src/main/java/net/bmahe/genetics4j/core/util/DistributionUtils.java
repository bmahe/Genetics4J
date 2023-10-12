package net.bmahe.genetics4j.core.util;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.spec.statistics.distributions.NormalDistribution;
import net.bmahe.genetics4j.core.spec.statistics.distributions.UniformDistribution;

public class DistributionUtils {

	public static Supplier<Double> distributionValueSupplier(final RandomGenerator randomGenerator,
			final double minValue, final double maxValue, final Distribution distribution) {
		Objects.requireNonNull(randomGenerator);
		Objects.requireNonNull(distribution);
		Validate.isTrue(minValue <= maxValue);

		return switch (distribution) {
			case UniformDistribution ud -> {
				final double valueRange = maxValue - minValue;
				yield () -> minValue + randomGenerator.nextDouble() * valueRange;
			}
			case NormalDistribution normalDistribution -> {
				final double mean = normalDistribution.mean();
				final double standardDeviation = normalDistribution.standardDeviation();

				yield () -> {
					final double value = mean + randomGenerator.nextGaussian() * standardDeviation;

					if (value < minValue) {
						return minValue;
					} else if (value > maxValue) {
						return maxValue;
					}

					return value;
				};
			}
			default -> throw new IllegalArgumentException(String.format("Distribution not supported: %s", distribution));
		};
	}

	public static Supplier<Float> distributionFloatValueSupplier(final RandomGenerator randomGenerator,
			final float minValue, final float maxValue, final Distribution distribution) {
		Objects.requireNonNull(randomGenerator);
		Objects.requireNonNull(distribution);
		Validate.isTrue(minValue <= maxValue);

		return switch (distribution) {
			case UniformDistribution ud -> {
				final float valueRange = maxValue - minValue;

				yield () -> minValue + randomGenerator.nextFloat() * valueRange;
			}
			case NormalDistribution normalDistribution -> {
				final double mean = normalDistribution.mean();
				final double standardDeviation = normalDistribution.standardDeviation();

				yield () -> {
					final float value = (float) (mean + randomGenerator.nextGaussian() * standardDeviation);

					if (value < minValue) {
						return minValue;
					} else if (value > maxValue) {
						return maxValue;
					}

					return value;
				};
			}
			default -> throw new IllegalArgumentException(String.format("Distribution not supported: %s", distribution));
		};
	}
}