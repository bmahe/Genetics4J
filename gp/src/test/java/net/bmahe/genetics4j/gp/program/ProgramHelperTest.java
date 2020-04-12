package net.bmahe.genetics4j.gp.program;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.OperationFactories;
import net.bmahe.genetics4j.gp.OperationFactory;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;

public class ProgramHelperTest {

	@Test(expected = NullPointerException.class)
	public void checkRandomCtor() {

		final ProgramHelper programHelper = new ProgramHelper(null);
	}

	@Test(expected = NullPointerException.class)
	public void pickRandomFunctionNoProgram() {

		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		programHelper.pickRandomFunction(null);
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

		final ProgramHelper programHelper = new ProgramHelper(random);

		final OperationFactory randomFunction = programHelper.pickRandomFunction(mockProgram);
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

		final ProgramHelper programHelper = new ProgramHelper(random);

		final OperationFactory randomFunction = programHelper.pickRandomFunction(mockProgram);
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

		final ProgramHelper programHelper = new ProgramHelper(mockRandom);

		final OperationFactory randomFunction = programHelper.pickRandomFunction(mockProgram);
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

		final ProgramHelper programHelper = new ProgramHelper(random);

		final OperationFactory randomFunction = programHelper.pickRandomFunction(mockProgram, String.class);
	}

	@Test
	public void pickRandomFunctionWithConstraint() {

		final Random random = new Random();

		final Program mockProgram = mock(Program.class);

		final OperationFactory doubleToStringOperationFactory = OperationFactories.ofUnary("DoubleToString",
				Double.class, String.class, String::valueOf);

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

		final ProgramHelper programHelper = new ProgramHelper(random);

		/**
		 * There is only one operation factory defined as returning a String. So it must
		 * pick that one
		 */
		final OperationFactory randomFunction = programHelper.pickRandomFunction(mockProgram, String.class);
		assertEquals(doubleToStringOperationFactory, randomFunction);
	}

	@Test(expected = NullPointerException.class)
	public void pickRandomTerminalNoProgram() {

		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		programHelper.pickRandomTerminal(null);
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

		final ProgramHelper programHelper = new ProgramHelper(random);

		final OperationFactory randomTerminal = programHelper.pickRandomTerminal(mockProgram);
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

		final ProgramHelper programHelper = new ProgramHelper(random);

		final OperationFactory randomTerminal = programHelper.pickRandomTerminal(mockProgram);
		assertEquals(terminals.iterator().next(), randomTerminal);
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

		final ProgramHelper programHelper = new ProgramHelper(mockRandom);

		final OperationFactory randomTerminal = programHelper.pickRandomTerminal(mockProgram);
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

		final ProgramHelper programHelper = new ProgramHelper(random);

		final OperationFactory randomTerminal = programHelper.pickRandomTerminal(mockProgram, String.class);
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

		final OperationFactory spaceStringTerminalFactory = OperationFactories.ofTerminal("SpaceString", String.class,
				() -> StringUtils.SPACE);
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

		/**
		 * There is only one operation factory defined as returning a String. So it must
		 * pick that one
		 */
		final OperationFactory randomTerminal = programHelper.pickRandomTerminal(mockProgram, String.class);
		assertEquals(spaceStringTerminalFactory, randomTerminal);
	}

	@Test
	public void pickRandomTerminalOrFunciton() {

		final Random mockRandom = mock(Random.class);
		when(mockRandom.nextInt(anyInt())).thenReturn(2).thenReturn(6);

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

		final ProgramHelper programHelper = new ProgramHelper(mockRandom);

		final OperationFactory randomTerminal = programHelper.pickRandomFunctionOrTerminal(mockProgram);
		assertEquals(Terminals.E, randomTerminal);

		final OperationFactory randomTerminal2 = programHelper.pickRandomFunctionOrTerminal(mockProgram);
		assertEquals(Functions.DIV, randomTerminal2);
	}

	@Test
	public void pickRandomTerminalOrFunctionWithConstraint() {

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

		final OperationFactory spaceStringTerminalFactory = OperationFactories.ofTerminal("SpaceString", String.class,
				() -> StringUtils.SPACE);
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

		/**
		 * There is only one operation factory defined as returning a String. So it must
		 * pick that one
		 */
		final OperationFactory randomTerminal = programHelper.pickRandomTerminal(mockProgram, String.class);
		assertEquals(spaceStringTerminalFactory, randomTerminal);
	}

}