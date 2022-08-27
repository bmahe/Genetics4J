package net.bmahe.genetics4j.gp.utils;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.Operation;

public class ProgramUtils {

	private ProgramUtils() {
	}

	public static Object execute(final TreeChromosome<Operation<?>> treeChromosome, final Object[] input) {
		Validate.notNull(treeChromosome);

		final TreeNode<Operation<?>> root = treeChromosome.getRoot();

		return execute(root, input);
	}

	public static Object execute(final TreeNode<Operation<?>> node, final Object[] input) {
		Validate.notNull(node);

		final Operation operation = node.getData();
		final List<TreeNode<Operation<?>>> children = node.getChildren();

		final Object[] parameters = children != null ? children.stream()
				.map(child -> execute(child, input))
				.toArray() : new Object[] {};

		return operation.apply(input, parameters);
	}

}