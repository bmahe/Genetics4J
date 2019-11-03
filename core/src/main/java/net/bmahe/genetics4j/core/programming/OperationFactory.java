package net.bmahe.genetics4j.core.programming;

import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;

@SuppressWarnings("rawtypes")
public interface OperationFactory {

	Class[] acceptedTypes();

	Class returnedType();

	Operation build(final Operation[] input);

	static OperationFactory of(final Class[] acceptedTypes, final Class returnedType,
			final Function<Operation[], Operation> build) {
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
			public Operation build(Operation[] input) {
				return build.apply(input);
			}
		};
	}

	static OperationFactory of(final String name, final Class[] acceptedTypes, final Class returnedType,
			final Supplier<Object> compute) {
		return new OperationFactory() {

			@Override
			public Class[] acceptedTypes() {
				return acceptedTypes;
			}

			@Override
			public Class returnedType() {
				return returnedType;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Operation build(final Operation[] input) {
				Validate.notNull(input);
				Validate.isTrue(input.length == acceptedTypes.length);
				for (int i = 0; i < input.length; i++) {
					final Operation operation = input[i];
					acceptedTypes[i].isAssignableFrom(operation.returnedType());
				}

				return ImmutableOperation.of(name, acceptedTypes.length, returnedType, input, compute);
			}
		};
	}

	static <T> OperationFactory ofTerminal(final Class<T> returnedType, final Function<Operation[], Operation> build) {
		return of(new Class[] {}, returnedType, build);
	}

	static <T> OperationFactory ofTerminal(final String name, final Class<T> returnedType,
			final Supplier<Object> compute) {
		return of(name, new Class[] {}, returnedType, compute);
	}

}