package net.bmahe.genetics4j.core.chromosomes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TreeChromosomeTest {

	@Test
	public void nullRootCtor() {
		assertThrows(NullPointerException.class, () -> new TreeChromosome<>(null));
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