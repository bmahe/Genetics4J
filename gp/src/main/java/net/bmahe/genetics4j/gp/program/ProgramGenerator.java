package net.bmahe.genetics4j.gp.program;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.Operation;

public interface ProgramGenerator {

	TreeNode<Operation<?>> generate(final Program program);

	<T> TreeNode<Operation<T>> generate(final Program program, final int maxDepth);

	<T, U> TreeNode<Operation<T>> generate(final Program program, final int maxDepth, final Class<U> rootType);
}