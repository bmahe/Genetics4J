package net.bmahe.genetics4j.core.chromosomes;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TreeNodeTest {

	@Test(expected = NullPointerException.class)
	public void staticFactoryAllNull() {
		TreeNode.of(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void staticFactoryNullData() {
		TreeNode.of(null, List.of(new TreeNode<>(2)));
	}

	@Test(expected = NullPointerException.class)
	public void staticFactoryNullChildren() {
		TreeNode.of(2, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void staticFactoryEmptyChildren() {
		TreeNode.of(2, Collections.emptyList());
	}

	@Test
	public void staticFactoryOneChild() {
		final TreeNode<Integer> treeNode = TreeNode.of(2, List.of(new TreeNode<>(3)));

		assertNotNull(treeNode);
		assertEquals(Integer.valueOf(2), treeNode.getData());
		assertEquals(2, treeNode.getDepth());
		assertEquals(2, treeNode.getSize());
		assertNotNull(treeNode.getChildren());
		assertNotNull(treeNode.getChild(0));
		assertEquals(Integer.valueOf(3), treeNode.getChild(0).getData());
		assertEquals(Integer.valueOf(3), treeNode.getChildren().get(0).getData());
	}

	@Test
	public void staticFactoryMultipleChildren() {
		final TreeNode<Integer> treeNode = TreeNode.of(2,
				List.of(TreeNode.of(4, List.of(new TreeNode<>(6), new TreeNode<>(1))),
						TreeNode.of(7, List.of(new TreeNode<>(9)))));

		assertNotNull(treeNode);
		assertEquals(Integer.valueOf(2), treeNode.getData());
		assertEquals(3, treeNode.getDepth());
		assertEquals(6, treeNode.getSize());
		assertNotNull(treeNode.getChildren());
		assertNotNull(treeNode.getChild(0));
		assertEquals(Integer.valueOf(4), treeNode.getChild(0).getData());
		assertEquals(Integer.valueOf(4), treeNode.getChild(0).getData());
		assertEquals(2, treeNode.getChild(0).getDepth());
		assertEquals(1, treeNode.getChild(0).getChild(0).getDepth());
		assertEquals(3, treeNode.getChild(0).getSize());
		assertEquals(1, treeNode.getChild(0).getChild(0).getSize());
		assertEquals(Integer.valueOf(6), treeNode.getChildren().get(0).getChild(0).getData());
		assertEquals(Integer.valueOf(6), treeNode.getChildren().get(0).getChild(0).getData());
		assertEquals(Integer.valueOf(9), treeNode.getChildren().get(1).getChild(0).getData());

	}
}