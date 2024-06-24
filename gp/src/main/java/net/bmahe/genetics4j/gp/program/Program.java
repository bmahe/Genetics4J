package net.bmahe.genetics4j.gp.program;

import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import net.bmahe.genetics4j.gp.InputSpec;
import net.bmahe.genetics4j.gp.OperationFactory;

@Value.Immutable
public abstract class Program {

	@Parameter
	public abstract Set<OperationFactory> functions();

	@Parameter
	public abstract Set<OperationFactory> terminal();

	@Parameter
	public abstract InputSpec inputSpec();

	@Parameter
	public abstract int maxDepth();

	@Value.Check
	protected void check() {
		Objects.requireNonNull(functions());
		Validate.isTrue(functions().size() > 0);

		Objects.requireNonNull(terminal());
		Validate.isTrue(terminal().size() > 0);
	}
}