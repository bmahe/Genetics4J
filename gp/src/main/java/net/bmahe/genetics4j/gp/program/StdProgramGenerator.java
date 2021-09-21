package net.bmahe.genetics4j.gp.program;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.OperationFactory;

public class StdProgramGenerator implements ProgramGenerator {

	private final ProgramHelper programHelper;
	private final RandomGenerator randomGenerator;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T, U> TreeNode<Operation<T>> generate(final Program program, Class<U> acceptedType, int maxDepth,
			int depth) {

		OperationFactory currentNode = depth < maxDepth - 1 && randomGenerator.nextDouble() < 0.5
				? programHelper.pickRandomFunction(program, acceptedType)
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

	public StdProgramGenerator(final ProgramHelper _programHelper, final RandomGenerator _randomGenerator) {
		Validate.notNull(_programHelper);
		Validate.notNull(_randomGenerator);

		this.programHelper = _programHelper;
		this.randomGenerator = _randomGenerator;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public TreeNode<Operation<?>> generate(final Program program, final int maxDepth) {
		Validate.notNull(program);
		Validate.isTrue(maxDepth > 0);

		final OperationFactory currentNode = randomGenerator.nextDouble() < 0.98
				? programHelper.pickRandomFunction(program)
				: programHelper.pickRandomTerminal(program);

		final Operation currentOperation = currentNode.build(program.inputSpec());
		final TreeNode<Operation<?>> currentTreeNode = new TreeNode<>(currentOperation);

		Class[] acceptedTypes = currentNode.acceptedTypes();

		for (int i = 0; i < acceptedTypes.length; i++) {
			final Class acceptedType = acceptedTypes[i];
			final TreeNode<Operation<?>> operation = generate(program, acceptedType, maxDepth, 1);

			currentTreeNode.addChild(operation);
		}

		return currentTreeNode;
	}

	@Override
	public TreeNode<Operation<?>> generate(final Program program) {
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