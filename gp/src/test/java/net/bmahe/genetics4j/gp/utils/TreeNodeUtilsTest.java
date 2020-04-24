package net.bmahe.genetics4j.gp.utils;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;

public class TreeNodeUtilsTest {

	@Test
	public void simple() {

		assertEquals(true, TreeNodeUtils.areSame(null, null));
		assertEquals(false, TreeNodeUtils.areSame(null, new TreeNode<>("test")));
		assertEquals(false, TreeNodeUtils.areSame(new TreeNode<>("test"), null));

		assertEquals(true, TreeNodeUtils.areSame(new TreeNode<>("test"), new TreeNode<>("test")));
		assertEquals(false, TreeNodeUtils.areSame(new TreeNode<>("test"), new TreeNode<>("tesbt")));

		assertEquals(true,
				TreeNodeUtils.areSame(TreeNode.of("A", List.of(new TreeNode<>("B"), new TreeNode<>("C"))),
						TreeNode.of("A", List.of(new TreeNode<>("B"), new TreeNode<>("C")))));
		assertEquals(false,
				TreeNodeUtils.areSame(TreeNode.of("A", List.of(new TreeNode<>("d"), new TreeNode<>("C"))),
						TreeNode.of("A", List.of(new TreeNode<>("B"), new TreeNode<>("C")))));

		assertEquals(true,
				TreeNodeUtils.areSame(
						TreeNode.of("A",
								List.of(TreeNode.of("inner", List.of(new TreeNode<>("cat"))), new TreeNode<>("C"))),
						TreeNode.of("A",
								List.of(TreeNode.of("inner", List.of(new TreeNode<>("cat"))), new TreeNode<>("C")))));

		assertEquals(false,
				TreeNodeUtils.areSame(
						TreeNode.of("A",
								List.of(TreeNode.of("inner", List.of(new TreeNode<>("cat"))), new TreeNode<>("C"))),
						TreeNode.of("A",
								List.of(TreeNode.of("inner", List.of(new TreeNode<>("C"))), new TreeNode<>("C")))));

		assertEquals(false,
				TreeNodeUtils.areSame(
						TreeNode.of("A",
								List.of(TreeNode.of("inner", List.of(new TreeNode<>("cat"))), new TreeNode<>("C"))),
						TreeNode.of("A",
								List.of(TreeNode.of("inner", List.of(new TreeNode<>("C"))), new TreeNode<>("cat")))));

		assertEquals(false,
				TreeNodeUtils
						.areSame(
								TreeNode.of("A",
										List.of(TreeNode.of("inner", List.of(new TreeNode<>("cat"))),
												new TreeNode<>("C"))),
								TreeNode.of("A", List.of(new TreeNode<>("C"), new TreeNode<>("B")))));
		assertEquals(false,
				TreeNodeUtils
						.areSame(
								TreeNode.of("A",
										List.of(TreeNode.of("inner", List.of(new TreeNode<>("cat"))),
												new TreeNode<>("C"))),
								TreeNode.of("A", List.of(new TreeNode<>("B"), new TreeNode<>("C")))));

	}
}