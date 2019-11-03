package net.bmahe.genetics4j.core.programming;

import java.util.function.Supplier;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

@Value.Immutable
public abstract class Operation {
	@Parameter
	public abstract String getName();

	@Parameter
	public abstract int getArity();

	@Parameter
	public abstract Class returnedType();

	@Parameter
	public abstract Operation[] input();

	@Parameter
	public abstract Supplier<Object> compute();

	public Object apply() {
		return compute().get();
	}

	public boolean isTerminal() {
		return getArity() == 0;
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getName());

		final Operation[] children = input();

		if (children.length > 0) {
			stringBuilder.append("(");
			for (int i = 0; i < children.length; i++) {
				final Operation operation = children[i];
				final String operationStr = operation.toString();

				stringBuilder.append(operationStr);

				if (i < children.length - 1) {
					stringBuilder.append(", ");

				}
			}
			stringBuilder.append(")");
		}

		return stringBuilder.toString();
	}
}