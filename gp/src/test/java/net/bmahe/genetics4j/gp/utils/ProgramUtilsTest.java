package net.bmahe.genetics4j.gp.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.InputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.OperationFactories;
import net.bmahe.genetics4j.gp.OperationFactory;
import net.bmahe.genetics4j.gp.math.ImmutableInputOperation;

public class ProgramUtilsTest {

	final public static OperationFactory AND = OperationFactories
			.ofBinary("AND", Boolean.class, Boolean.class, Boolean.class, (a, b) -> a && b);
	final public static OperationFactory OR = OperationFactories
			.ofBinary("OR", Boolean.class, Boolean.class, Boolean.class, (a, b) -> a || b);

	final public static OperationFactory NOT = OperationFactories
			.ofUnary("NOT", Boolean.class, Boolean.class, (a) -> !a);

	final public static OperationFactory TRUE = OperationFactories.ofTerminal("True", Boolean.class, () -> true);
	final public static OperationFactory FALSE = OperationFactories.ofTerminal("False", Boolean.class, () -> false);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> OperationFactory Input(final int inputIdx, final Class<T> clazz) {
		Validate.notNull(clazz);

		return OperationFactories.of(new Class[] {}, clazz, (inputSpec) -> {
			final List<Class> types = inputSpec.types();

			if (inputIdx < 0 || inputIdx >= types.size() || types.get(inputIdx).isAssignableFrom(clazz) == false) {
				throw new IllegalArgumentException("No input with type " + clazz + " found");
			}

			final net.bmahe.genetics4j.gp.math.ImmutableInputOperation.Builder<T> operationBuilder = ImmutableInputOperation
					.builder();
			operationBuilder.name("Input").prettyName("Input[" + inputIdx + "]").returnedType(clazz).index(inputIdx);

			return operationBuilder.build();
		});
	}

	@Test
	public void simpleTerminal() {
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Boolean.class, Boolean.class, String.class));

		final TreeNode<Operation<?>> treeNode = new TreeNode<>(TRUE.build(inputSpec));
		final TreeChromosome<Operation<?>> treeChromosome = new TreeChromosome<>(treeNode);

		final Object outputChromosome = ProgramUtils.execute(treeChromosome, new Object[] { true, true, "toto" });
		assertNotNull(outputChromosome);
		assertTrue(outputChromosome instanceof Boolean);
		assertEquals(true, (Boolean) outputChromosome);

		final Object output = ProgramUtils.execute(treeNode, new Object[] { false, false, "toto" });
		assertNotNull(output);
		assertTrue(output instanceof Boolean);
		assertEquals(true, (Boolean) output);
	}

	public void simpleInput(final int inputIdx, final boolean value0, final boolean value1) {
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Boolean.class, Boolean.class, String.class));

		final TreeNode<Operation<?>> treeNode = new TreeNode<>(Input(inputIdx, Boolean.class).build(inputSpec));
		final TreeChromosome<Operation<?>> treeChromosome = new TreeChromosome<>(treeNode);

		final Object outputChromosome = ProgramUtils.execute(treeChromosome, new Object[] { value0, value1, "toto" });
		assertNotNull(outputChromosome);
		assertTrue(outputChromosome instanceof Boolean);
		assertEquals(inputIdx == 0 ? value0 : value1, (Boolean) outputChromosome);

		final Object output = ProgramUtils.execute(treeNode, new Object[] { value0, value1, "toto" });
		assertNotNull(output);
		assertTrue(output instanceof Boolean);
		assertEquals(inputIdx == 0 ? value0 : value1, (Boolean) output);
	}

	@Test
	public void checkInput() {
		simpleInput(0, false, true);
		simpleInput(1, false, true);
		simpleInput(0, true, true);
		simpleInput(1, true, true);
		simpleInput(0, true, false);
		simpleInput(1, true, false);
		simpleInput(0, false, false);
		simpleInput(1, false, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void incompatibleInput() {
		simpleInput(2, false, true);
	}

	public void complexOperators(final boolean value0, final boolean value1, final boolean outputValue) {
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Boolean.class, Boolean.class, String.class));

		final TreeNode<Operation<?>> treeNode = TreeNode.of(AND.build(inputSpec),
				List.of(TreeNode.of(NOT.build(inputSpec),
						List.of(new TreeNode<>(Input(0, Boolean.class).build(inputSpec)))),
						new TreeNode<>(Input(1, Boolean.class).build(inputSpec))));

		final TreeChromosome<Operation<?>> treeChromosome = new TreeChromosome<>(treeNode);

		final Object outputChromosome = ProgramUtils.execute(treeChromosome, new Object[] { value0, value1, "toto" });
		assertNotNull(outputChromosome);
		assertTrue(outputChromosome instanceof Boolean);
		assertEquals(outputValue, (Boolean) outputChromosome);

		final Object output = ProgramUtils.execute(treeNode, new Object[] { value0, value1, "toto" });
		assertNotNull(output);
		assertTrue(output instanceof Boolean);
		assertEquals(outputValue, (Boolean) output);
	}

	@Test
	public void complexTest() {
		complexOperators(false, false, false);
		complexOperators(false, true, true);
		complexOperators(true, false, false);
		complexOperators(true, true, false);
	}
}