package net.bmahe.genetics4j.core.programming.math;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.programming.ImmutableOperation;
import net.bmahe.genetics4j.core.programming.OperationFactories;
import net.bmahe.genetics4j.core.programming.OperationFactory;

public class Terminals {
	public static OperationFactory PI = OperationFactories.ofTerminal("PI", Double.class, () -> Math.PI);

	public static OperationFactory E = OperationFactories.ofTerminal("E", Double.class, () -> Math.E);

	public static OperationFactory Coefficient(final Random random, final double min, final double max) {
		return OperationFactory.of(new Class[] {}, Double.class, () -> {

			final double value = random.nextDouble() * (max - min) + min;

			return ImmutableOperation
					.of("Coefficient[" + value + "]", Collections.emptyList(), Double.class, (input, parameter) -> value);
		});
	}

	public static OperationFactory CoefficientRounded(final Random random, final int min, final int max) {
		return OperationFactory.of(new Class[] {}, Double.class, () -> {

			final double value = random.nextInt(max - min) + min;

			return ImmutableOperation.of("CoefficientRounded[" + value + "]",
					Collections.emptyList(),
					Double.class,
					(input, parameter) -> value);
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> OperationFactory Input(final Random random, final Class<T> clazz) {
		Validate.notNull(random);
		Validate.notNull(clazz);

		return OperationFactories.of(new Class[] {}, clazz, (inputSpec) -> {
			final List<Class> types = inputSpec.types();
			final List<Integer> candidates = IntStream.range(0, types.size())
					.filter((i) -> types.get(i)
							.isAssignableFrom(clazz))
					.boxed()
					.collect(Collectors.toList());

			if (candidates.isEmpty()) {
				throw new IllegalArgumentException("No input with type " + clazz + " found");
			}

			final Integer inputIdx = candidates.get(random.nextInt(candidates.size()));

			return ImmutableOperation.of("Input[" + inputIdx + "]", Collections.emptyList(), clazz, (input, parameter) -> {
				return input[inputIdx];
			});
		});
	}

	public static OperationFactory InputDouble(final Random random) {
		return Input(random, Double.class);
	}

	public static OperationFactory InputString(final Random random) {
		return Input(random, String.class);
	}

}