package net.bmahe.genetics4j.gp.program;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.OperationFactory;

public class GrowProgramGenerator implements ProgramGenerator {

	private final ProgramHelper programHelper;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T, U> TreeNode<Operation<T>> generate(final Program program, final Class<U> acceptedType,
			final int maxDepth, final int depth) {

		OperationFactory currentNode = depth < maxDepth - 1
				? programHelper.pickRandomFunctionOrTerminal(program, acceptedType)
				: programHelper.pickRandomTerminal(program, acceptedType);

		final Operation<T> currentOperation = currentNode.build(program.inputSpec());
		final TreeNode<Operation<T>> currentTreeNode = new TreeNode<>(currentOperation);

		final Class[] acceptedTypes = currentNode.acceptedTypes();

		for (int i = 0; i < acceptedTypes.length; i++) {
			final Class childAcceptedType = acceptedTypes[i];
			final TreeNode<Operation<T>> operation = generate(program, childAcceptedType, maxDepth, depth + 1);

			currentTreeNode.addChild(operation);
		}

		return currentTreeNode;
	}

	public GrowProgramGenerator(final ProgramHelper _programHelper) {
		Validate.notNull(_programHelper);

		this.programHelper = _programHelper;
	}

	@Override
	public TreeNode<Operation<?>> generate(final Program program) {
		return generate(program, program.maxDepth());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public TreeNode<Operation<?>> generate(final Program program, final int maxDepth) {
		Validate.notNull(program);
		Validate.isTrue(maxDepth > 0);

		final OperationFactory currentNode = programHelper.pickRandomFunctionOrTerminal(program);

		final Operation currentOperation = currentNode.build(program.inputSpec());
		final TreeNode<Operation<?>> currentTreeNode = new TreeNode<>(currentOperation);

		final Class[] acceptedTypes = currentNode.acceptedTypes();

		for (int i = 0; i < acceptedTypes.length; i++) {
			final Class acceptedType = acceptedTypes[i];
			final TreeNode<Operation<?>> operation = generate(program, acceptedType, maxDepth, 1);

			currentTreeNode.addChild(operation);
		}

		return currentTreeNode;
	}

	@Override
	public <T, U> TreeNode<Operation<T>> generate(final Program program, final int maxDepth, final Class<U> rootType) {
		Validate.notNull(program);
		Validate.notNull(rootType);
		Validate.isTrue(maxDepth > 0);

		return generate(program, rootType, maxDepth, 0);
	}
}
