package net.bmahe.genetics4j.samples.symbolicregression;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.program.ImmutableProgram;
import net.bmahe.genetics4j.gp.program.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.program.Program;

public class SymbolicRegressionUtils {

	private SymbolicRegressionUtils() {
	}

	public static Program buildProgram(final Random random) {
		Validate.notNull(random);

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD, Functions.MUL, Functions.DIV, Functions.SUB, Functions.POW);
		programBuilder.addTerminal(Terminals.InputDouble(random), Terminals.CoefficientRounded(random, -10, 10));

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		return program;
	}
}