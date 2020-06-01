package net.bmahe.genetics4j.core.chromosomes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

public class TreeNode<T> {

	private final T data;

	private ArrayList<TreeNode<T>> children;

	public TreeNode(final T _data) {
		Validate.notNull(_data);

		this.data = _data;
		this.children = new ArrayList<>();
	}

	public T getData() {
		return data;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public TreeNode<T> getChild(final int childIndex) {
		Validate.isTrue(childIndex >= 0);

		return children.get(childIndex);
	}

	public void setChild(final int childIndex, final TreeNode<T> childData) {
		Validate.isTrue(childIndex >= 0);

		children.set(childIndex, childData);
	}

	public void addChild(final TreeNode<T> childData) {
		Validate.notNull(childData);

		children.add(childData);
	}

	public void addChildren(final Collection<TreeNode<T>> childrenNodes) {
		Validate.notNull(childrenNodes);
		Validate.isTrue(childrenNodes.isEmpty() == false);

		children.addAll(childrenNodes);
	}

	public int getSize() {
		return 1 + children.stream().map(TreeNode::getSize).collect(Collectors.summingInt(x -> x));
	}

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