package net.bmahe.genetics4j.core.chromosomes;

import org.apache.commons.lang3.Validate;

/**
 * A chromosome implementation that represents genetic information as a tree structure.
 * 
 * <p>TreeChromosome is essential for genetic programming and other evolutionary approaches
 * that require hierarchical or nested representations. The tree structure allows for
 * variable-length solutions and naturally supports recursive data types and operations.
 * 
 * <p>This chromosome type is particularly suitable for:
 * <ul>
 * <li><strong>Genetic Programming</strong>: Evolving mathematical expressions, programs, and functions</li>
 * <li><strong>Decision trees</strong>: Classification and regression tree learning</li>
 * <li><strong>Parse trees</strong>: Grammar evolution and language processing</li>
 * <li><strong>Hierarchical structures</strong>: Neural network topologies, circuit designs</li>
 * <li><strong>Symbolic regression</strong>: Finding mathematical models that fit data</li>
 * <li><strong>Rule evolution</strong>: Evolving conditional logic and decision rules</li>
 * </ul>
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Variable structure</strong>: Tree depth and branching can vary between individuals</li>
 * <li><strong>Hierarchical representation</strong>: Natural support for nested and recursive structures</li>
 * <li><strong>Type-safe nodes</strong>: Generic type parameter ensures consistent node data types</li>
 * <li><strong>Immutable backbone</strong>: Tree structure is fixed after construction</li>
 * </ul>
 * 
 * <p>The chromosome contains a single root node that represents the entire tree structure.
 * The tree size (total number of nodes) is calculated recursively from the root node.
 * For the purpose of the Chromosome interface, this counts as one "allele" since it
 * represents a single cohesive genetic unit.
 * 
 * <p>Tree genetic operators typically work by:
 * <ul>
 * <li><strong>Crossover</strong>: Exchanging subtrees between parent chromosomes</li>
 * <li><strong>Mutation</strong>: Replacing subtrees or individual nodes</li>
 * <li><strong>Growth/Pruning</strong>: Adding or removing branches to control complexity</li>
 * </ul>
 * 
 * @param <T> the type of data stored in tree nodes
 * @see TreeNode
 * @see Chromosome
 * @see TreeNode
 */
public class TreeChromosome<T> implements Chromosome {

	private final TreeNode<T> root;

	/**
	 * Creates a new tree chromosome with the specified root node.
	 * 
	 * @param _root the root node of the tree structure
	 * @throws IllegalArgumentException if root is null
	 */
	public TreeChromosome(final TreeNode<T> _root) {
		Validate.notNull(_root);

		this.root = _root;
	}

	/**
	 * Returns the number of alleles in this chromosome.
	 * 
	 * <p>For tree chromosomes, this always returns 1 since the entire tree
	 * is considered a single genetic unit (allele). To get the total number
	 * of nodes in the tree, use {@link #getSize()}.
	 * 
	 * @return 1, representing the tree as a single allele
	 */
	@Override
	public int getNumAlleles() {
		return 1;
	}

	/**
	 * Returns the root node of this tree chromosome.
	 * 
	 * @return the root node containing the entire tree structure
	 */
	public TreeNode<T> getRoot() {
		return root;
	}

	/**
	 * Returns the total number of nodes in the tree.
	 * 
	 * <p>This method recursively counts all nodes in the tree starting
	 * from the root node, including internal nodes and leaves.
	 * 
	 * @return the total number of nodes in the tree
	 */
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