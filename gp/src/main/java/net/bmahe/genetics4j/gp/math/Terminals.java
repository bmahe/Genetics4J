package net.bmahe.genetics4j.gp.math;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.gp.OperationFactories;
import net.bmahe.genetics4j.gp.OperationFactory;
import net.bmahe.genetics4j.gp.math.ImmutableCoefficientOperation.Builder;

public class Terminals {

	public final static String TYPE_COEFFICIENT = "Coefficient";
	public final static String TYPE_INPUT = "Input";

	public static OperationFactory PI = OperationFactories.ofTerminal("PI", Double.class, () -> Math.PI);

	public static OperationFactory E = OperationFactories.ofTerminal("E", Double.class, () -> Math.E);

	public static OperationFactory Coefficient(final Random random, final double min, final double max) {
		return OperationFactories.ofOperationSupplier(new Class[] {}, Double.class, () -> {

			final double value = random.nextDouble() * (max - min) + min;

			final Builder<Double> operationBuilder = ImmutableCoefficientOperation.builder();

			operationBuilder.name(TYPE_COEFFICIENT)
					.prettyName(TYPE_COEFFICIENT + "[" + value + "]")
					.returnedType(Double.class)
					.value(value);

			return operationBuilder.build();
		});
	}

	public static OperationFactory CoefficientRounded(final Random random, final int min, final int max) {
		return OperationFactories.ofOperationSupplier(new Class[] {}, Double.class, () -> {

			final double value = random.nextInt(max - min) + min;

			final Builder<Double> operationBuilder = ImmutableCoefficientOperation.builder();
			operationBuilder.name(TYPE_COEFFICIENT)
					.prettyName("CoefficientRounded[" + value + "]")
					.returnedType(Double.class)
					.value(value);

			return operationBuilder.build();
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

			final net.bmahe.genetics4j.gp.math.ImmutableInputOperation.Builder<T> operationBuilder = ImmutableInputOperation
					.builder();
			operationBuilder.name(TYPE_INPUT)
					.prettyName(TYPE_INPUT + "[" + inputIdx + "]")
					.returnedType(clazz)
					.index(inputIdx);

			return operationBuilder.build();
		});
	}

	public static OperationFactory InputDouble(final Random random) {
		return Input(random, Double.class);
	}

	public static OperationFactory InputString(final Random random) {
		return Input(random, String.class);
	}
}