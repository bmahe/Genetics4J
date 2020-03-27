package net.bmahe.genetics4j.gp.math;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import net.bmahe.genetics4j.gp.Operation;

@Value.Immutable
public abstract class CoefficientOperation<T> extends Operation<T> {

	@Override
	public List<Class> acceptedTypes() {
		return Collections.emptyList();
	}

	@Override
	public BiFunction<T[], Object[], Object> compute() {
		return (input, parameter) -> value();
	}

	@Parameter
	public abstract T value();

}