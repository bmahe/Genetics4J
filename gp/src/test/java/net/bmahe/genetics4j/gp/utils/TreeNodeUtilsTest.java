package net.bmahe.genetics4j.gp.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.InputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;

public class TreeNodeUtilsTest {

	@Test
	public void areSame() {

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

	@SuppressWarnings("unchecked")
	@Test
	public void compare() {
		final InputSpec inputSpec = ImmutableInputSpec.of(Arrays.asList(Double.class, String.class));

		assertEquals(0, TreeNodeUtils.compare(null, null));
		assertTrue(0 > TreeNodeUtils.compare((TreeNode<Operation<Double>>) null,
				new TreeNode<Operation<Double>>(Terminals.PI.build(inputSpec))));
		assertTrue(0 < TreeNodeUtils.compare(new TreeNode<Operation<Double>>(Terminals.PI.build(inputSpec)),
				(TreeNode<Operation<Double>>) null));

		assertEquals(0,
				TreeNodeUtils.compare(new TreeNode<Operation<Double>>(Terminals.PI.build(inputSpec)),
						new TreeNode<Operation<Double>>(Terminals.PI.build(inputSpec))));
		assertTrue(0 < TreeNodeUtils.compare(new TreeNode<Operation<Double>>(Terminals.PI.build(inputSpec)),
				new TreeNode<Operation<Double>>(Terminals.E.build(inputSpec))));

		assertEquals(0,
				TreeNodeUtils.compare(
						TreeNode.<Operation<Double>>of(Functions.ADD.build(inputSpec),
								List.of(new TreeNode<>(Terminals.PI.build(inputSpec)),
										new TreeNode<>(Terminals.E.build(inputSpec)))),
						TreeNode.<Operation<Double>>of(Functions.ADD.build(inputSpec),
								List.of(new TreeNode<>(Terminals.PI.build(inputSpec)),
										new TreeNode<>(Terminals.E.build(inputSpec))))));

		assertTrue(0 > TreeNodeUtils.compare(
				TreeNode.<Operation<Double>>of(Functions.ADD.build(inputSpec),
						List.of(new TreeNode<>(Terminals.PI.build(inputSpec)),
								new TreeNode<>(Terminals.E.build(inputSpec)))),
				TreeNode.<Operation<Double>>of(Functions.ADD.build(inputSpec),
						List.of(new TreeNode<>(Terminals.PI.build(inputSpec)),
								new TreeNode<>(Terminals.PI.build(inputSpec))))));

		assertTrue(0 > TreeNodeUtils.compare(
				TreeNode.<Operation<Double>>of(Functions.ADD.build(inputSpec),
						List.of(new TreeNode<>(Terminals.E.build(inputSpec)),
								new TreeNode<>(Terminals.PI.build(inputSpec)))),
				TreeNode.<Operation<Double>>of(Functions.ADD.build(inputSpec),
						List.of(new TreeNode<>(Terminals.PI.build(inputSpec)),
								new TreeNode<>(Terminals.E.build(inputSpec))))));
	}

}