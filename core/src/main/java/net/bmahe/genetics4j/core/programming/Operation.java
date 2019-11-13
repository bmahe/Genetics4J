package net.bmahe.genetics4j.core.programming;

import java.util.function.BiFunction;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

@Value.Immutable
public abstract class Operation<T> {

	@Parameter
	public abstract String getName();

	@Parameter
	public abstract int getArity();

	@SuppressWarnings("rawtypes")
	@Parameter
	public abstract Class returnedType();

	@Parameter
	public abstract BiFunction<T[], Object[], Object> compute();

	public Object apply(final T[] input, final Object[] parameters) {
		return compute().apply(input, parameters);
	}

	public boolean isTerminal() {
		return getArity() == 0;
	}
}