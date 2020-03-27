package net.bmahe.genetics4j.gp;

import java.util.List;
import java.util.function.BiFunction;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

@Value.Immutable
public abstract class Operation<T> {

	@Parameter
	public abstract String getName();

	@SuppressWarnings("rawtypes")
	@Parameter
	public abstract List<Class> acceptedTypes();

	@SuppressWarnings("rawtypes")
	@Parameter
	public abstract Class returnedType();

	@Parameter
	public abstract BiFunction<T[], Object[], Object> compute();

	@Value.Default
	public String getPrettyName() {
		return getName();
	}

	public Object apply(final T[] input, final Object[] parameters) {
		final BiFunction<T[], Object[], Object> function = compute();
		return function.apply(input, parameters);
	}

	public int getArity() {
		return acceptedTypes().size();
	}

	public boolean isTerminal() {
		return getArity() == 0;
	}
}