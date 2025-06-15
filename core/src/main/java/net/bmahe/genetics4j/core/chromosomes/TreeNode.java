package net.bmahe.genetics4j.core.chromosomes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

/**
 * Represents a node in a tree structure used for genetic programming and tree-based chromosomes.
 * 
 * <p>TreeNode provides a flexible, generic tree data structure that forms the foundation for
 * tree-based genetic programming operations. Each node contains data of a specified type and
 * can have zero or more child nodes, enabling the representation of complex hierarchical
 * structures such as mathematical expressions, decision trees, or program syntax trees.
 * 
 * <p>Key features include:
 * <ul>
 * <li><strong>Generic data storage</strong>: Can hold any type of data (functions, terminals, values)</li>
 * <li><strong>Dynamic structure</strong>: Supports adding, removing, and modifying children</li>
 * <li><strong>Tree metrics</strong>: Provides size and depth calculations for analysis</li>
 * <li><strong>Hierarchical operations</strong>: Enables tree traversal and manipulation</li>
 * </ul>
 * 
 * <p>Common uses in genetic programming:
 * <ul>
 * <li><strong>Expression trees</strong>: Mathematical or logical expressions with operators and operands</li>
 * <li><strong>Program trees</strong>: Structured representation of executable code or algorithms</li>
 * <li><strong>Decision trees</strong>: Conditional logic structures for classification or control</li>
 * <li><strong>Grammar trees</strong>: Parse trees representing valid grammatical constructs</li>
 * </ul>
 * 
 * <p>Tree structure characteristics:
 * <ul>
 * <li><strong>Mutable structure</strong>: Children can be added, removed, or replaced during evolution</li>
 * <li><strong>Type safety</strong>: Generic parameterization ensures consistent data types</li>
 * <li><strong>Recursive operations</strong>: Size and depth calculations traverse the entire subtree</li>
 * <li><strong>Equality semantics</strong>: Two trees are equal if their structure and data match</li>
 * </ul>
 * 
 * <p>Example usage in genetic programming:
 * <pre>{@code
 * // Creating a simple mathematical expression: (x + 2) * 3
 * TreeNode<String> multiply = new TreeNode<>("*");
 * TreeNode<String> add = new TreeNode<>("+");
 * TreeNode<String> x = new TreeNode<>("x");
 * TreeNode<String> two = new TreeNode<>("2");
 * TreeNode<String> three = new TreeNode<>("3");
 * 
 * add.addChild(x);
 * add.addChild(two);
 * multiply.addChild(add);
 * multiply.addChild(three);
 * 
 * // Tree metrics
 * int treeSize = multiply.getSize();   // Total nodes in tree
 * int treeDepth = multiply.getDepth(); // Maximum depth from root
 * 
 * // Factory method for creating nodes with children
 * TreeNode<String> expression = TreeNode.of("*", List.of(
 *     TreeNode.of("+", List.of(
 *         new TreeNode<>("x"),
 *         new TreeNode<>("2")
 *     )),
 *     new TreeNode<>("3")
 * ));
 * }</pre>
 * 
 * <p>Integration with genetic operations:
 * <ul>
 * <li><strong>Crossover</strong>: Subtree exchange between parent trees</li>
 * <li><strong>Mutation</strong>: Replacement or modification of individual nodes or subtrees</li>
 * <li><strong>Size constraints</strong>: Enforcement of maximum tree size or depth limits</li>
 * <li><strong>Evaluation</strong>: Tree traversal for fitness computation</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Memory usage</strong>: Each node maintains a list of children references</li>
 * <li><strong>Tree traversal</strong>: Size and depth calculations have O(n) complexity</li>
 * <li><strong>Structural sharing</strong>: Nodes can be shared between trees with care</li>
 * <li><strong>Deep trees</strong>: Very deep trees may cause stack overflow in recursive operations</li>
 * </ul>
 * 
 * @param <T> the type of data stored in each node
 * @see TreeChromosome
 * @see TreeChromosome
 */
public class TreeNode<T> {

	private final T data;

	private ArrayList<TreeNode<T>> children;

	/**
	 * Constructs a new tree node with the specified data and no children.
	 * 
	 * <p>Creates a leaf node that can later have children added to it. The data
	 * provided becomes the payload for this node and cannot be changed after construction.
	 * 
	 * @param _data the data to store in this node
	 * @throws IllegalArgumentException if data is null
	 */
	public TreeNode(final T _data) {
		Validate.notNull(_data);

		this.data = _data;
		this.children = new ArrayList<>();
	}

	/**
	 * Returns the data stored in this node.
	 * 
	 * @return the data payload of this node
	 */
	public T getData() {
		return data;
	}

	/**
	 * Returns the list of direct children of this node.
	 * 
	 * <p>The returned list is the actual internal list used by this node.
	 * Modifications to the returned list will affect this node's structure.
	 * 
	 * @return the mutable list of child nodes
	 */
	public List<TreeNode<T>> getChildren() {
		return children;
	}

	/**
	 * Returns the child node at the specified index.
	 * 
	 * @param childIndex the index of the child to retrieve (0-based)
	 * @return the child node at the specified index
	 * @throws IllegalArgumentException if childIndex is negative
	 * @throws IndexOutOfBoundsException if childIndex is >= number of children
	 */
	public TreeNode<T> getChild(final int childIndex) {
		Validate.isTrue(childIndex >= 0);

		return children.get(childIndex);
	}

	/**
	 * Replaces the child node at the specified index with a new node.
	 * 
	 * <p>This operation modifies the tree structure by replacing an existing
	 * child with a new subtree rooted at the provided node.
	 * 
	 * @param childIndex the index of the child to replace (0-based)
	 * @param childData the new child node to set at the specified index
	 * @throws IllegalArgumentException if childIndex is negative
	 * @throws IndexOutOfBoundsException if childIndex is >= number of children
	 */
	public void setChild(final int childIndex, final TreeNode<T> childData) {
		Validate.isTrue(childIndex >= 0);

		children.set(childIndex, childData);
	}

	/**
	 * Adds a new child node to this node.
	 * 
	 * <p>The new child is appended to the end of the children list.
	 * This operation increases the arity of this node by one.
	 * 
	 * @param childData the child node to add
	 * @throws IllegalArgumentException if childData is null
	 */
	public void addChild(final TreeNode<T> childData) {
		Validate.notNull(childData);

		children.add(childData);
	}

	/**
	 * Adds multiple child nodes to this node.
	 * 
	 * <p>All nodes in the provided collection are appended to the children list
	 * in the order they appear in the collection.
	 * 
	 * @param childrenNodes the collection of child nodes to add
	 * @throws IllegalArgumentException if childrenNodes is null or empty
	 */
	public void addChildren(final Collection<TreeNode<T>> childrenNodes) {
		Validate.notNull(childrenNodes);
		Validate.isTrue(childrenNodes.isEmpty() == false);

		children.addAll(childrenNodes);
	}

	/**
	 * Returns the total number of nodes in the subtree rooted at this node.
	 * 
	 * <p>This method recursively counts all nodes in the subtree, including
	 * this node and all its descendants. The size is useful for analyzing
	 * tree complexity and implementing size-based genetic operations.
	 * 
	 * @return the total number of nodes in this subtree (always >= 1)
	 */
	public int getSize() {
		return 1 + children.stream().map(TreeNode::getSize).collect(Collectors.summingInt(x -> x));
	}

	/**
	 * Returns the maximum depth of the subtree rooted at this node.
	 * 
	 * <p>The depth is defined as the length of the longest path from this node
	 * to any leaf node in the subtree. A leaf node has depth 1.
	 * 
	 * @return the maximum depth of this subtree (always >= 1)
	 */
	public int getDepth() {
		return 1 + children.stream().map(TreeNode::getDepth).max(Comparator.naturalOrder()).orElse(0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		TreeNode other = (TreeNode) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "TreeNode [data=" + data + ", children=" + children + "]";
	}

	/**
	 * Creates a new tree node with the specified data and children.
	 * 
	 * <p>This factory method provides a convenient way to construct trees
	 * with a specific structure in a single operation. All provided children
	 * are added to the new node.
	 * 
	 * @param <U> the type of data stored in the nodes
	 * @param data the data to store in the root node
	 * @param children the collection of child nodes to add
	 * @return a new tree node with the specified data and children
	 * @throws IllegalArgumentException if data is null, children is null, or children is empty
	 */
	public static <U> TreeNode<U> of(final U data, final Collection<TreeNode<U>> children) {
		Validate.notNull(data);
		Validate.notNull(children);
		Validate.isTrue(children.size() > 0);

		final TreeNode<U> rootNode = new TreeNode<>(data);
		for (TreeNode<U> childNode : children) {
			rootNode.addChild(childNode);
		}

		return rootNode;
	}
}