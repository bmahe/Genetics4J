package net.bmahe.genetics4j.gp.utils;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.Operation;

public class TreeNodeUtils {

	private TreeNodeUtils() {

	}

	public static <T> boolean areSame(final TreeNode<T> rootA, final TreeNode<T> rootB) {
		if (rootA == null && rootB == null) {
			return true;
		}

		if (rootA == null || rootB == null) {
			return false;
		}

		final T dataA = rootA.getData();
		final T dataB = rootB.getData();

		if (dataA.equals(dataB) == false) {
			return false;
		}

		final List<TreeNode<T>> childrenA = rootA.getChildren();
		final List<TreeNode<T>> childrenB = rootB.getChildren();

		if (childrenA.size() != childrenB.size()) {
			return false;
		}

		boolean isSame = true;
		for (int i = 0; i < childrenA.size() && isSame == true; i++) {

			final TreeNode<T> childA = childrenA.get(i);
			final TreeNode<T> childB = childrenB.get(i);

			isSame = areSame(childA, childB);
		}

		return isSame;
	}

	public static String toStringTreeNode(final TreeNode<Operation<?>> node) {
		Validate.notNull(node);

		final Operation<?> operation = node.getData();
		final List<TreeNode<Operation<?>>> children = node.getChildren();

		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(operation.getPrettyName());
		if (children != null && children.isEmpty() == false) {
			stringBuilder.append("(");

			final Iterator<TreeNode<Operation<?>>> iterator = children.iterator();
			while (iterator.hasNext()) {
				final TreeNode<Operation<?>> treeNode = iterator.next();

				stringBuilder.append(toStringTreeNode(treeNode));

				if (iterator.hasNext()) {
					stringBuilder.append(", ");
				}
			}

			stringBuilder.append(")");
		}
		return stringBuilder.toString();
	}

}