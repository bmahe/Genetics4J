package net.bmahe.genetics4j.core.programming;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;

public interface ProgramGenerator {

	OperationFactory pickRandomFunction(final Program program);

	<T> OperationFactory pickRandomFunction(final Program program, final Class<T> requiredClass);

	<T> OperationFactory pickRandomTerminal(final Program program, final Class<T> requiredClass);

	OperationFactory pickRandomTerminal(final Program program);

	TreeNode<Operation> generate(final Program program);

	<T> TreeNode<Operation<T>> generate(final Program program, final int maxDepth);

	<T, U> TreeNode<Operation<T>> generate(final Program program, final int maxDepth, final Class<U> rootType);
}