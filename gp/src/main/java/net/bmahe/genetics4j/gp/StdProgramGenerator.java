package net.bmahe.genetics4j.gp;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;

public class StdProgramGenerator implements ProgramGenerator {

	private final Random random;

	public OperationFactory pickRandomFunction(final Program program) {

		final Set<OperationFactory> functions = program.functions();
		return functions.stream()
				.skip(random.nextInt(functions.size()))
				.findFirst()
				.get();
	}

	public <T> OperationFactory pickRandomFunction(final Program program, final Class<T> requiredClass) {

		final Set<OperationFactory> functions = program.functions();
		@SuppressWarnings("unchecked")
		final List<OperationFactory> candidates = functions.stream()
				.filter((operationFactory) -> operationFactory.returnedType()
						.isAssignableFrom(requiredClass))
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable function returning a " + requiredClass);
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	public <T> OperationFactory pickRandomTerminal(final Program program, final Class<T> requiredClass) {

		final Set<OperationFactory> terminals = program.terminal();
		@SuppressWarnings("unchecked")
		final List<OperationFactory> candidates = terminals.stream()
				.filter((operationFactory) -> operationFactory.returnedType()
						.isAssignableFrom(requiredClass))
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable terminal returning a " + requiredClass);
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	public OperationFactory pickRandomTerminal(final Program program) {

		final Set<OperationFactory> terminals = program.terminal();
		final List<OperationFactory> candidates = terminals.stream()
				.collect(Collectors.toList());

		if (candidates == null || candidates.isEmpty()) {
			throw new IllegalArgumentException("Could not find a suitable terminal ");
		}

		return candidates.get(random.nextInt(candidates.size()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T, U> TreeNode<Operation<T>> generate(final Program program, Class<U> acceptedType, int maxDepth,
			int depth) {

		OperationFactory currentNode = depth < maxDepth && random.nextDouble() < 0.5
				? pickRandomFunction(program, acceptedType)
				: pickRandomTerminal(program, acceptedType);

		final Operation<T> currentOperation = currentNode.build(program.inputSpec());
		final TreeNode<Operation<T>> currentTreeNode = new TreeNode<>(currentOperation);

		Class[] acceptedTypes = currentNode.acceptedTypes();

		for (int i = 0; i < acceptedTypes.length; i++) {
			final Class childAcceptedType = acceptedTypes[i];
			final TreeNode<Operation<T>> operation = generate(program, childAcceptedType, maxDepth, depth + 1);

			currentTreeNode.addChild(operation);
		}

		return currentTreeNode;
	}

	public StdProgramGenerator(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public TreeNode<Operation> generate(final Program program, final int maxDepth) {
		Validate.notNull(program);
		Validate.isTrue(maxDepth > 0);

		OperationFactory currentNode = random.nextDouble() < 0.98 ? pickRandomFunction(program)
				: pickRandomTerminal(program);

		final Operation currentOperation = currentNode.build(program.inputSpec());
		final TreeNode<Operation> currentTreeNode = new TreeNode<>(currentOperation);

		Class[] acceptedTypes = currentNode.acceptedTypes();

		for (int i = 0; i < acceptedTypes.length; i++) {
			final Class acceptedType = acceptedTypes[i];
			final TreeNode<Operation> operation = generate(program, acceptedType, maxDepth, 1);

			currentTreeNode.addChild(operation);
		}

		return currentTreeNode;
	}

	@Override
	public TreeNode<Operation> generate(final Program program) {
		return generate(program, program.maxDepth());
	}

	@Override
	public <T, U> TreeNode<Operation<T>> generate(final Program program, final int maxDepth, final Class<U> rootType) {
		Validate.notNull(program);
		Validate.notNull(rootType);
		Validate.isTrue(maxDepth > 0);

		return generate(program, rootType, maxDepth, 0);
	}

}