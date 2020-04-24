package net.bmahe.genetics4j.core.chromosomes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

public class TreeChromosomeTest {

	@Test(expected = NullPointerException.class)
	public void nullRootCtor() {
		new TreeChromosome<>(null);
	}

	@Test
	public void simple() {

		final TreeNode<Integer> treeNode = TreeNode.of(6, List.of(new TreeNode<>(2), new TreeNode<>(3)));
		final TreeChromosome<Integer> treeChromosome = new TreeChromosome<>(treeNode);

		assertNotNull(treeChromosome.getRoot());
		assertEquals(treeNode, treeChromosome.getRoot());
		assertEquals(1, treeChromosome.getNumAlleles());
		assertEquals(treeNode.getSize(), treeChromosome.getSize());
		assertEquals(3, treeChromosome.getSize());
	}
}