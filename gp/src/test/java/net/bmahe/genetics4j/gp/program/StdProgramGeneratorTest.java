package net.bmahe.genetics4j.gp.program;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.OperationFactories;
import net.bmahe.genetics4j.gp.OperationFactory;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;

public class StdProgramGeneratorTest {

	@Test
	public void checkNoParamCtor() {

		assertThrows(NullPointerException.class, () -> new StdProgramGenerator(null, null));
	}

	@Test
	public void generate() {
		final Random random = new Random();

		final Program mockProgram = mock(Program.class);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> functions = new LinkedHashSet<>();
		functions.add(Functions.ADD);
		functions.add(Functions.MUL);
		functions.add(Functions.DIV);
		functions.add(Functions.SUB);
		functions.add(Functions.COS);
		functions.add(Functions.SIN);
		functions.add(Functions.EXP);
		when(mockProgram.functions()).thenReturn(functions);

		final OperationFactory spaceStringTerminalFactory = OperationFactories
				.ofTerminal("SpaceString", String.class, () -> StringUtils.SPACE);
		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.PI);
		terminals.add(Terminals.E);
		terminals.add(spaceStringTerminalFactory);
		terminals.add(Terminals.Coefficient(random, -50, 100));
		terminals.add(Terminals.CoefficientRounded(random, -5, 7));
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final TreeNode<Operation<?>> program = programGenerator.generate(mockProgram, 3);
		assertTrue(program.getDepth() <= 3,
				"Message longer than expected. Depth: " + program.getDepth() + "; Program: " + program);
	}

	@Test
	public void generateWithRootType() {
		final Random random = new Random();

		final Program mockProgram = mock(Program.class);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> functions = new LinkedHashSet<>();
		functions.add(Functions.ADD);
		functions.add(Functions.MUL);
		functions.add(Functions.DIV);
		functions.add(Functions.SUB);
		functions.add(Functions.COS);
		functions.add(Functions.SIN);
		functions.add(Functions.EXP);
		functions.add(OperationFactories.ofUnary("toString", Double.class, String.class, (d) -> Double.toString(d)));
		when(mockProgram.functions()).thenReturn(functions);

		final OperationFactory spaceStringTerminalFactory = OperationFactories
				.ofTerminal("SpaceString", String.class, () -> StringUtils.SPACE);
		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.PI);
		terminals.add(Terminals.E);
		terminals.add(spaceStringTerminalFactory);
		terminals.add(Terminals.Coefficient(random, -50, 100));
		terminals.add(Terminals.CoefficientRounded(random, -5, 7));
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final TreeNode<Operation<String>> program = programGenerator.generate(mockProgram, 3, String.class);
		assertTrue(program.getDepth() <= 3,
				"Message longer than expected. Depth: " + program.getDepth() + "; Program: " + program);
		assertEquals(String.class, program.getData().returnedType());
	}
}