package net.bmahe.genetics4j.core.programming.math;

import java.util.Random;

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

	public static OperationFactory INPUT = OperationFactories.of(new Class[] {}, Double.class, () -> {
		return ImmutableOperation.of("Input", 0, Double.class, (input, parameter) -> {
			return input[0];
		});
	});
}