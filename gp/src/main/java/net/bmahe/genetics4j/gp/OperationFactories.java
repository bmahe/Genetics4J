package net.bmahe.genetics4j.gp;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.gp.math.ImmutableCoefficientOperation;
import net.bmahe.genetics4j.gp.math.ImmutableCoefficientOperation.Builder;

public final class OperationFactories {

	private OperationFactories() {
	}

	@SuppressWarnings("rawtypes")
	public static OperationFactory ofOperationSupplier(final Class[] acceptedTypes, final Class returnedType,
			final Supplier<Operation> buildSupplier) {
		Objects.requireNonNull(acceptedTypes);
		Objects.requireNonNull(returnedType);
		Objects.requireNonNull(buildSupplier);

		return new OperationFactory() {

			@Override
			public Class returnedType() {
				return returnedType;
			}

			@Override
			public Class[] acceptedTypes() {
				return acceptedTypes;
			}

			@Override
			public Operation build(final InputSpec inputSpec) {
				return buildSupplier.get();
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public static OperationFactory of(final Class[] acceptedTypes, final Class returnedType,
			final Function<InputSpec, Operation> operationBuilder) {
		Objects.requireNonNull(acceptedTypes);
		Objects.requireNonNull(returnedType);
		Objects.requireNonNull(operationBuilder);

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
				return operationBuilder.apply(inputSpec);
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public static OperationFactory of(final String name, final Class[] acceptedTypes, final Class returnedType,
			final BiFunction<Object[], Object[], Object> compute) {
		Validate.notBlank(name);
		Objects.requireNonNull(acceptedTypes);
		Objects.requireNonNull(returnedType);
		Objects.requireNonNull(compute);

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
	public static OperationFactory ofCoefficient(final String name, final Class returnedType, final Object value) {
		Validate.notBlank(name);
		Objects.requireNonNull(returnedType);
		Objects.requireNonNull(value);

		return new OperationFactory() {

			@Override
			public Class[] acceptedTypes() {
				return new Class[] {};
			}

			@Override
			public Class returnedType() {
				return returnedType;
			}

			@Override
			public Operation build(final InputSpec inputSpec) {
				final Builder<Object> operationBuilder = ImmutableCoefficientOperation.builder();

				operationBuilder.name(name)
						.value(value)
						.returnedType(returnedType)
						.prettyName(name + "[" + value + "]");

				return operationBuilder.build();
			}
		};
	}

	public static <T> OperationFactory ofTerminal(final String name, final Class<T> returnedType,
			final Supplier<T> compute) {
		Validate.notBlank(name);
		Objects.requireNonNull(returnedType);
		Objects.requireNonNull(compute);

		return of(name, new Class[] {}, returnedType, (input, parameter) -> {
			return compute.get();
		});
	}

	public static <T, U> OperationFactory ofUnary(final String name, final Class<T> acceptedType,
			final Class<U> returnedType, final Function<T, U> compute) {
		Validate.notBlank(name);
		Objects.requireNonNull(acceptedType);
		Objects.requireNonNull(returnedType);
		Objects.requireNonNull(compute);

		return of(name, new Class[] { acceptedType }, returnedType, (input, parameters) -> {
			Objects.requireNonNull(parameters);

			final Object parameter1 = parameters[0];
			Objects.requireNonNull(parameter1);
			Validate.isInstanceOf(acceptedType, parameter1);

			@SuppressWarnings("unchecked")
			final T operand = (T) parameter1;

			return compute.apply(operand);
		});
	}

	@SuppressWarnings("unchecked")
	public static <T, U, V> OperationFactory ofBinary(final String name, final Class<T> acceptedType1,
			final Class<U> acceptedType2, final Class<V> returnedType, final BiFunction<T, U, V> compute) {
		Validate.notBlank(name);
		Objects.requireNonNull(acceptedType1);
		Objects.requireNonNull(acceptedType2);
		Objects.requireNonNull(returnedType);
		Objects.requireNonNull(compute);

		return of(name, new Class[] { acceptedType1, acceptedType2 }, returnedType, (input, parameters) -> {
			Objects.requireNonNull(parameters);

			final Object parameter1 = parameters[0];
			Objects.requireNonNull(parameter1);
			Validate.isInstanceOf(acceptedType1, parameter1);
			final T operand1 = (T) parameter1;

			final Object parameter2 = parameters[1];
			Objects.requireNonNull(parameter2);
			Validate.isInstanceOf(acceptedType2, parameter2);
			final U operand2 = (U) parameter2;

			return compute.apply(operand1, operand2);
		});
	}
}