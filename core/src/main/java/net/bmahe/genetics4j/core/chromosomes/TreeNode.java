package net.bmahe.genetics4j.core.chromosomes;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public String toString() {
		return "TreeNode [data=" + data + ", children=" + children + "]";
	}
}