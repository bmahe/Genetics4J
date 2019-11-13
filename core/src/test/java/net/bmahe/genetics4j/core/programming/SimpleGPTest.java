package net.bmahe.genetics4j.core.programming;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.programming.ImmutableProgram.Builder;
import net.bmahe.genetics4j.core.programming.math.Functions;
import net.bmahe.genetics4j.core.programming.math.Terminals;

public class SimpleGPTest {

	private OperationFactory pickRandomFunction(final Random random, Program program) {

		final Set<OperationFactory> functions = program.functions();
		return functions.stream()
				.skip(random.nextInt(functions.size()))
				.findFirst()
				.get();
	}

	@SuppressWarnings("unchecked")
	private OperationFactory pickRandomFunction(final Random random, final Program program, final Class requiredClass) {

		final Set<OperationFactory> functions = program.functions();
		final List<OperationFactory> candidates = functions.stream()
				.filter((operationFactory) -> operationFactory.returnedType()
						.isAssignableFrom(requiredClass))
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable function returning a " + requiredClass);
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	@SuppressWarnings("unchecked")
	private OperationFactory pickRandomTerminal(final Random random, final Program program, final Class requiredClass) {

		final Set<OperationFactory> terminals = program.terminal();
		final List<OperationFactory> candidates = terminals.stream()
				.filter((operationFactory) -> operationFactory.returnedType()
						.isAssignableFrom(requiredClass))
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable terminal returning a " + requiredClass);
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	@SuppressWarnings("unchecked")
	private OperationFactory pickRandomTerminal(final Random random, final Program program) {

		final Set<OperationFactory> terminals = program.terminal();
		final List<OperationFactory> candidates = terminals.stream()
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable terminal ");
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	private <T> TreeNode<Operation<T>> generate(final Random random, final Program program, Class acceptedType,
			int maxHeight, int height) {

		OperationFactory currentNode = height < maxHeight && random.nextDouble() < 0.5
				? pickRandomFunction(random, program, acceptedType)
				: pickRandomTerminal(random, program, acceptedType);

		final Operation<T> currentOperation = currentNode.build(program.inputSpec());
		final TreeNode<Operation<T>> currentTreeNode = new TreeNode<>(currentOperation);

		Class[] acceptedTypes = currentNode.acceptedTypes();

		for (int i = 0; i < acceptedTypes.length; i++) {
			final Class childAcceptedType = acceptedTypes[i];
			final TreeNode<Operation<T>> operation = generate(random, program, childAcceptedType, maxHeight, height + 1);

			currentTreeNode.addChild(operation);
		}

		return currentTreeNode;
	}

	private <T> TreeNode<Operation<T>> generate(final Random random, final Program program, int maxHeight) {

		OperationFactory currentNode = random.nextDouble() < 0.98 ? pickRandomFunction(random, program)
				: pickRandomTerminal(random, program);

		final Operation<T> currentOperation = currentNode.build(program.inputSpec());
		final TreeNode<Operation<T>> currentTreeNode = new TreeNode<>(currentOperation);

		Class[] acceptedTypes = currentNode.acceptedTypes();

		for (int i = 0; i < acceptedTypes.length; i++) {
			final Class acceptedType = acceptedTypes[i];
			final TreeNode<Operation<T>> operation = generate(random, program, acceptedType, maxHeight, 1);

			currentTreeNode.addChild(operation);
		}

		return currentTreeNode;
	}

	public String toStringTreeNode(final TreeNode<Operation<Double>> node) {

		final Operation<Double> operation = node.getData();
		final List<TreeNode<Operation<Double>>> children = node.getChildren();

		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(operation.getName());
		if (children != null && children.isEmpty() == false) {
			stringBuilder.append("(");

			final Iterator<TreeNode<Operation<Double>>> iterator = children.iterator();
			while (iterator.hasNext()) {
				final TreeNode<Operation<Double>> treeNode = iterator.next();

				stringBuilder.append(toStringTreeNode(treeNode));

				if (iterator.hasNext()) {
					stringBuilder.append(", ");
				}
			}

			stringBuilder.append(")");
		}
		return stringBuilder.toString();
	}

	@Test
	public void simple() {
		final Random random = new Random();
		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD,
				Functions.MUL,
				Functions.DIV,
				Functions.SUB,
				Functions.COS,
				Functions.SIN,
				Functions.EXP);
		programBuilder.addTerminal(Terminals.INPUT(random),
				Terminals.PI,
				Terminals.E,
				Terminals.Coefficient(random, -50, 100),
				Terminals.CoefficientRounded(random, -5, 7));

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));

		final ImmutableProgram program = programBuilder.build();
		for (int i = 0; i < 10; i++) {
			final TreeNode<Operation<Double>> operation = generate(random, program, 3);
			TreeChromosome<Operation<Double>> treeChromosome = new TreeChromosome<>(operation);
			System.out.println(toStringTreeNode(treeChromosome.getRoot()));
		}
	}

}