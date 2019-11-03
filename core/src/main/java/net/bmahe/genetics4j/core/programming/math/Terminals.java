package net.bmahe.genetics4j.core.programming.math;

import java.util.Random;

import net.bmahe.genetics4j.core.programming.ImmutableOperation;
import net.bmahe.genetics4j.core.programming.OperationFactory;

public class Terminals {

	public static OperationFactory PI = OperationFactory.ofTerminal("PI", Double.class, () -> Math.PI);

	public static OperationFactory E = OperationFactory.ofTerminal("E", Double.class, () -> Math.E);

	public static OperationFactory Coefficient(final Random random, final double min, final double max) {

		return OperationFactory.ofTerminal(Double.class, (input) -> {

			final double value = random.nextDouble() * (max - min) + min;

			return ImmutableOperation.of(String.valueOf(value), 0, Double.class, input, () -> value);
		});
	}
}