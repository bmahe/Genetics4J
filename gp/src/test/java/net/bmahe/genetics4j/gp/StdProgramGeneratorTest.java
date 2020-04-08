package net.bmahe.genetics4j.gp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;

public class StdProgramGeneratorTest {

	@Test(expected = NullPointerException.class)
	public void checkRandomCtor() {

		final StdProgramGenerator programGenerator = new StdProgramGenerator(null);
	}

	@Test(expected = NullPointerException.class)
	public void pickRandomFunctionNoProgram() {

		final Random random = new Random();
		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		programGenerator.pickRandomFunction(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void pickRandomFunctionButNoFunction() {

		final Random random = new Random();
		final Program mockProgram = mock(Program.class);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> functions = new LinkedHashSet<>();
		when(mockProgram.functions()).thenReturn(functions);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.PI);
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		final OperationFactory randomFunction = programGenerator.pickRandomFunction(mockProgram);
	}

	@Test
	public void pickRandomFunctionSingleFunction() {

		final Random random = new Random();
		final Program mockProgram = mock(Program.class);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> functions = new LinkedHashSet<>();
		functions.add(Functions.ADD);
		when(mockProgram.functions()).thenReturn(functions);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.PI);
		terminals.add(Terminals.E);
		terminals.add(Terminals.Coefficient(random, -50, 100));
		terminals.add(Terminals.CoefficientRounded(random, -5, 7));
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		final OperationFactory randomFunction = programGenerator.pickRandomFunction(mockProgram);
		assertEquals(Functions.ADD, randomFunction);
	}

	@Test
	public void pickRandomFunction() {

		final Random mockRandom = mock(Random.class);
		when(mockRandom.nextInt(anyInt())).thenReturn(2);

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

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.PI);
		terminals.add(Terminals.E);
		terminals.add(Terminals.Coefficient(mockRandom, -50, 100));
		terminals.add(Terminals.CoefficientRounded(mockRandom, -5, 7));
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final StdProgramGenerator programGenerator = new StdProgramGenerator(mockRandom);

		final OperationFactory randomFunction = programGenerator.pickRandomFunction(mockProgram);
		assertEquals(Functions.DIV, randomFunction);
	}

	@Test(expected = IllegalArgumentException.class)
	public void pickRandomFunctionWithConstraintButNoFunctionAvailable() {

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

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.PI);
		terminals.add(Terminals.E);
		terminals.add(Terminals.Coefficient(random, -50, 100));
		terminals.add(Terminals.CoefficientRounded(random, -5, 7));
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		final OperationFactory randomFunction = programGenerator.pickRandomFunction(mockProgram, String.class);
	}

	@Test
	public void pickRandomFunctionWithConstraint() {

		final Random random = new Random();

		final Program mockProgram = mock(Program.class);

		final OperationFactory doubleToStringOperationFactory = OperationFactories
				.ofUnary("DoubleToString", Double.class, String.class, String::valueOf);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> functions = new LinkedHashSet<>();
		functions.add(Functions.ADD);
		functions.add(Functions.MUL);
		functions.add(Functions.DIV);
		functions.add(Functions.SUB);
		functions.add(doubleToStringOperationFactory);
		functions.add(Functions.COS);
		functions.add(Functions.SIN);
		functions.add(Functions.EXP);
		when(mockProgram.functions()).thenReturn(functions);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.PI);
		terminals.add(Terminals.E);
		terminals.add(Terminals.Coefficient(random, -50, 100));
		terminals.add(Terminals.CoefficientRounded(random, -5, 7));
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		/**
		 * There is only one operation factory defined as returning a String. So it must
		 * pick that one
		 */
		final OperationFactory randomFunction = programGenerator.pickRandomFunction(mockProgram, String.class);
		assertEquals(doubleToStringOperationFactory, randomFunction);
	}

	@Test(expected = NullPointerException.class)
	public void pickRandomTerminalNoProgram() {

		final Random random = new Random();
		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		programGenerator.pickRandomTerminal(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void pickRandomTerminalButNoTerminal() {

		final Random random = new Random();
		final Program mockProgram = mock(Program.class);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> functions = new LinkedHashSet<>();
		when(mockProgram.functions()).thenReturn(functions);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		final OperationFactory randomTerminal = programGenerator.pickRandomTerminal(mockProgram);
	}

	@Test
	public void pickRandomTerminalSingleFunction() {

		final Random random = new Random();
		final Program mockProgram = mock(Program.class);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> functions = new LinkedHashSet<>();
		functions.add(Functions.ADD);
		when(mockProgram.functions()).thenReturn(functions);

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.CoefficientRounded(random, -5, 7));
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		final OperationFactory randomTerminal = programGenerator.pickRandomTerminal(mockProgram);
		assertEquals(terminals.iterator()
				.next(), randomTerminal);
	}

	@Test
	public void pickRandomTerminal() {

		final Random mockRandom = mock(Random.class);
		when(mockRandom.nextInt(anyInt())).thenReturn(2);

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

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.PI);
		terminals.add(Terminals.Coefficient(mockRandom, -50, 100));
		terminals.add(Terminals.E);
		terminals.add(Terminals.CoefficientRounded(mockRandom, -5, 7));
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final StdProgramGenerator programGenerator = new StdProgramGenerator(mockRandom);

		final OperationFactory randomTerminal = programGenerator.pickRandomTerminal(mockProgram);
		assertEquals(Terminals.E, randomTerminal);
	}

	@Test(expected = IllegalArgumentException.class)
	public void pickRandomTerminalWithConstraintButNoTerminalAvailable() {

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

		// Used a linkedhashset to have a predictable iteration
		final LinkedHashSet<OperationFactory> terminals = new LinkedHashSet<>();
		terminals.add(Terminals.PI);
		terminals.add(Terminals.E);
		terminals.add(Terminals.Coefficient(random, -50, 100));
		terminals.add(Terminals.CoefficientRounded(random, -5, 7));
		when(mockProgram.terminal()).thenReturn(terminals);

		when(mockProgram.inputSpec()).thenReturn(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		when(mockProgram.maxDepth()).thenReturn(4);

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		final OperationFactory randomTerminal = programGenerator.pickRandomTerminal(mockProgram, String.class);
	}

	@Test
	public void pickRandomTerminalWithConstraint() {

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

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		/**
		 * There is only one operation factory defined as returning a String. So it must
		 * pick that one
		 */
		final OperationFactory randomTerminal = programGenerator.pickRandomTerminal(mockProgram, String.class);
		assertEquals(spaceStringTerminalFactory, randomTerminal);
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

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		final TreeNode<Operation<?>> program = programGenerator.generate(mockProgram, 3);
		assertTrue("Message longer than expected. Depth: " + program.getDepth() + "; Program: " + program,
				program.getDepth() <= 3);
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

		final StdProgramGenerator programGenerator = new StdProgramGenerator(random);

		final TreeNode<Operation<String>> program = programGenerator.generate(mockProgram, 3, String.class);
		assertTrue("Message longer than expected. Depth: " + program.getDepth() + "; Program: " + program,
				program.getDepth() <= 3);
		assertEquals(String.class,
				program.getData()
						.returnedType());
	}
}