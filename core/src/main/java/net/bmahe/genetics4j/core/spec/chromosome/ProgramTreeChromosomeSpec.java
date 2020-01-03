package net.bmahe.genetics4j.core.spec.chromosome;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.programming.Program;

@Value.Immutable
public abstract class ProgramTreeChromosomeSpec implements ChromosomeSpec {

	@Value.Parameter
	public abstract Program program();

	public static ProgramTreeChromosomeSpec of(final Program program) {
		return ImmutableProgramTreeChromosomeSpec.of(program);
	}
}