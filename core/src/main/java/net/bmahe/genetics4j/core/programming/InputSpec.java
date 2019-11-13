package net.bmahe.genetics4j.core.programming;

import java.util.List;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

@Value.Immutable
public abstract class InputSpec {

	@SuppressWarnings("rawtypes")
	@Parameter
	public abstract List<Class> types();

	public int inputSize() {
		return types().size();
	}

}