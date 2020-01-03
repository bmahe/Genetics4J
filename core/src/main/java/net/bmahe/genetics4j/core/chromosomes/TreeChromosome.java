package net.bmahe.genetics4j.core.chromosomes;

import org.apache.commons.lang3.Validate;

public class TreeChromosome<T> implements Chromosome {

	private final TreeNode<T> root;

	public TreeChromosome(final TreeNode<T> _root) {
		Validate.notNull(_root);

		this.root = _root;
	}

	@Override
	public int getNumAlleles() {
		return 0;
	}

	public TreeNode<T> getRoot() {
		return root;
	}

	public int getSize() {
		if (root == null) {
			return 0;
		}

		return root.getSize();
	}
}