package net.bmahe.genetics4j.gp;

import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;

@SuppressWarnings("rawtypes")
public interface OperationFactory {

	//TODO make a List<Class>
	Class[] acceptedTypes();

	Class returnedType();

	Operation build(final InputSpec inputSpec);

	static OperationFactory of(final Class[] acceptedTypes, final Class returnedType,
			final Supplier<Operation> buildSupplier) {
		Validate.notNull(acceptedTypes);
		Validate.notNull(returnedType);
		Validate.notNull(buildSupplier);

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
}