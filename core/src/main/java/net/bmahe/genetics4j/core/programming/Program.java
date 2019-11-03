package net.bmahe.genetics4j.core.programming;

import java.util.Set;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

@Value.Immutable
public abstract class Program {

	@Parameter
	public abstract Set<OperationFactory> functions();

	@Parameter
	public abstract Set<OperationFactory> terminal();

	@Parameter
	public abstract InputSpec inputSpec();
}