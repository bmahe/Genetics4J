package net.bmahe.genetics4j.core.programming.math;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.bmahe.genetics4j.core.programming.ImmutableOperation;
import net.bmahe.genetics4j.core.programming.OperationFactories;
import net.bmahe.genetics4j.core.programming.OperationFactory;

public class Terminals {
	public static OperationFactory PI = OperationFactories.ofTerminal("PI", Double.class, () -> Math.PI);

	public static OperationFactory E = OperationFactories.ofTerminal("E", Double.class, () -> Math.E);

	public static OperationFactory Coefficient(final Random random, final double min, final double max) {
		return OperationFactory.of(new Class[] {}, Double.class, () -> {

			final double value = random.nextDouble() * (max - min) + min;

			return ImmutableOperation.of("Coefficient[" + value + "]", 0, Double.class, (input, parameter) -> value);
		});
	}

	public static OperationFactory CoefficientRounded(final Random random, final int min, final int max) {
		return OperationFactory.of(new Class[] {}, Double.class, () -> {

			final double value = random.nextInt(max - min) + min;

			return ImmutableOperation.of("CoefficientRounded[" + value + "]", 0, Double.class, (input, parameter) -> value);
		});
	}

	public static OperationFactory INPUT(final Random random) {
		return OperationFactories.of(new Class[] {}, Double.class, (inputSpec) -> {
			final List<Class> types = inputSpec.types();
			final List<Integer> candidates = IntStream.range(0, types.size())
					.filter((i) -> types.get(i)
							.isAssignableFrom(Double.class))
					.boxed()
					.collect(Collectors.toList());

			if (candidates.isEmpty()) {
				throw new IllegalArgumentException("No input with type Double found");
			}

			final Integer inputIdx = candidates.get(random.nextInt(candidates.size()));

			return ImmutableOperation.of("Input[" + inputIdx + "]", 0, Double.class, (input, parameter) -> {
				return input[inputIdx];
			});
		});
	}
}