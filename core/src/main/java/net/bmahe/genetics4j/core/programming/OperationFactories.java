package net.bmahe.genetics4j.core.programming;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;

public final class OperationFactories {

	@SuppressWarnings("rawtypes")
	public static OperationFactory of(final Class[] acceptedTypes, final Class returnedType,
			final Function<InputSpec, Operation> build) {
		return new OperationFactory() {
			@Override
			public Class[] acceptedTypes() {
				return acceptedTypes;
			}

			@Override
			public Class returnedType() {
				return returnedType;
			}

			@Override
			public Operation build(final InputSpec inputSpec) {
				return build.apply(inputSpec);
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public static OperationFactory of(final String name, final Class[] acceptedTypes, final Class returnedType,
			final BiFunction<Object[], Object[], Object> compute) {
		return new OperationFactory() {

			@Override
			public Class[] acceptedTypes() {
				return acceptedTypes;
			}

			@Override
			public Class returnedType() {
				return returnedType;
			}

			@Override
			public Operation build(final InputSpec inputSpec) {
				return ImmutableOperation.of(name, Arrays.asList(acceptedTypes), returnedType, compute);
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public static OperationFactory ofTerminal(final String name, final Class returnedType,
			final Supplier<Object> compute) {
		return of(name, new Class[] {}, returnedType, (input, parameter) -> {
			return compute.get();
		});
	}

	@SuppressWarnings("unchecked")
	public static <T, U> OperationFactory ofUnary(final String name, final Class<T> acceptedType,
			final Class<U> returnedType, final Function<T, U> compute) {
		return of(name, new Class[] { acceptedType }, returnedType, (input, parameters) -> {
			Validate.notNull(parameters);

			final Object parameter1 = parameters[0];
			Validate.notNull(parameter1);
			Validate.isInstanceOf(acceptedType, parameter1);
			final T operand = (T) parameter1;

			return compute.apply(operand);
		});
	}

	@SuppressWarnings("unchecked")
	public static <T, U, V> OperationFactory ofBinary(final String name, final Class<T> acceptedType1,
			final Class<U> acceptedType2, final Class<V> returnedType, final BiFunction<T, U, V> compute) {
		return of(name, new Class[] { acceptedType1, acceptedType2 }, returnedType, (input, parameters) -> {
			Validate.notNull(parameters);

			final Object parameter1 = parameters[0];
			Validate.notNull(parameter1);
			Validate.isInstanceOf(acceptedType1, parameter1);
			final T operand1 = (T) parameter1;

			final Object parameter2 = parameters[1];
			Validate.notNull(parameter2);
			Validate.isInstanceOf(acceptedType2, parameter2);
			final U operand2 = (U) parameter2;

			return compute.apply(operand1, operand2);
		});
	}
}