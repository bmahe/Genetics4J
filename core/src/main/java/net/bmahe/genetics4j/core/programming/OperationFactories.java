package net.bmahe.genetics4j.core.programming;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.Validate;

public final class OperationFactories {

	public static <T, R> OperationFactory ofFunction(final String name, final Class<T> parameter,
			final Class<R> returnedClass, final Function<T, R> compute) {

		final Class[] acceptedTypes = new Class[] { parameter };
		Validate.notNull(acceptedTypes);
		Validate.isTrue(acceptedTypes.length == 1);

		return new OperationFactory() {

			@Override
			public Class[] acceptedTypes() {
				// TODO Auto-generated method stub
				return acceptedTypes;
			}

			@Override
			public Class returnedType() {
				return returnedClass;
			}

			@Override
			public Operation build(Operation[] input) {
				Validate.notNull(input);
				for (int i = 0; i < input.length; i++) {
					final Operation operation = input[i];
					acceptedTypes[i].isAssignableFrom(operation.returnedType());
				}

				return ImmutableOperation.of(name, 1, returnedClass, input, () -> {
					final Operation operation1 = input[0];
					final T result1 = (T) operation1.apply();
					Validate.notNull(result1);

					return compute.apply(result1);
				});
			}

		};
	}

	public static <T, U, R> OperationFactory ofBiFunction(final String name, final Class<T> firstParameter,
			final Class<U> secondParameter, final Class<R> returnedClass, final BiFunction<T, U, R> compute) {

		final Class[] acceptedTypes = new Class[] { firstParameter, secondParameter };
		Validate.notNull(acceptedTypes);
		Validate.isTrue(acceptedTypes.length == 2);

		return new OperationFactory() {

			@Override
			public Class[] acceptedTypes() {
				// TODO Auto-generated method stub
				return acceptedTypes;
			}

			@Override
			public Class returnedType() {
				return returnedClass;
			}

			@Override
			public Operation build(Operation[] input) {
				Validate.notNull(input);
				for (int i = 0; i < input.length; i++) {
					final Operation operation = input[i];
					acceptedTypes[i].isAssignableFrom(operation.returnedType());
				}

				return ImmutableOperation.of(name, 2, returnedClass, input, () -> {
					final Operation operation1 = input[0];
					final T result1 = (T) operation1.apply();
					Validate.notNull(result1);

					final Operation operation2 = input[1];
					final U result2 = (U) operation2.apply();
					Validate.notNull(result2);

					return compute.apply(result1, result2);
				});
			}

		};
	}
}