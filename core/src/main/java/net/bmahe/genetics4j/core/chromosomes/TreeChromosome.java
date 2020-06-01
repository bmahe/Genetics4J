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
		return 1;
	}

	public TreeNode<T> getRoot() {
		return root;
	}

	public int getSize() {
		return root.getSize();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeChromosome other = (TreeChromosome) obj;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TreeChromosome [root=" + root + "]";
	}
}