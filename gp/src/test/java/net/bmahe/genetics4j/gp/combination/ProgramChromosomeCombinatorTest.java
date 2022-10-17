package net.bmahe.genetics4j.gp.combination;

import static net.bmahe.genetics4j.gp.math.Functions.ADD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.InputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;

public class ProgramChromosomeCombinatorTest {

	@Test
	public void noRandomParameter() {
		assertThrows(NullPointerException.class, () -> new ProgramChromosomeCombinator<Integer>(null));
	}

	@Test
	public void returnedTypeToNode() {
		final Random random = new Random();
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		// =======================

		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		root.addChild(new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec)));

		final TreeNode<Operation<?>> nodeStrToDouble = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random)
				.build(inputSpec)));
		root.addChild(nodeStrToDouble);

		final ProgramChromosomeCombinator<Integer> programChromosomeCombinator = new ProgramChromosomeCombinator<>(
				random);
		final Map<Class, List<TreeNode<Operation<?>>>> nodeMap = programChromosomeCombinator.returnedTypeToNode(root);

		assertNotNull(nodeMap);
		assertEquals(2,
				nodeMap.keySet()
						.size());
		assertTrue(nodeMap.containsKey(Double.class));
		assertEquals(3,
				nodeMap.get(Double.class)
						.size());
		assertTrue(nodeMap.containsKey(String.class));
		assertEquals(1,
				nodeMap.get(String.class)
						.size());

	}

	@Test
	public void copyAndReplace() {
		final Random random = new Random();
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		// =======================

		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		final TreeNode<Operation<?>> piNode = new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec));
		root.addChild(piNode);

		final TreeNode<Operation<?>> nodeStrToDouble = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random)
				.build(inputSpec)));
		root.addChild(nodeStrToDouble);

		// =======================

		final TreeNode<Operation<?>> rootReplacement = new TreeNode<Operation<?>>(Terminals.E.build(inputSpec));

		// =======================

		final ProgramChromosomeCombinator<Integer> programChromosomeCombinator = new ProgramChromosomeCombinator<>(
				random);
		final TreeNode<Operation<?>> newTreeNode = programChromosomeCombinator
				.copyAndReplace(root, nodeStrToDouble, rootReplacement);

		assertNotNull(newTreeNode);
		assertEquals(2,
				newTreeNode.getChildren()
						.size());
		assertEquals(piNode.getData(),
				newTreeNode.getChild(0)
						.getData());
		assertEquals(rootReplacement.getData(),
				newTreeNode.getChild(1)
						.getData());
	}

	@Test
	public void simpleCombine() {
		final Random random = new Random();
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		// =======================

		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		root.addChild(new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec)));

		final TreeNode<Operation<?>> nodeStrToDouble = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random)
				.build(inputSpec)));
		root.addChild(nodeStrToDouble);
		final TreeChromosome<Operation<?>> chromosomeA = new TreeChromosome<>(root);

		// =======================

		final TreeNode<Operation<?>> rootB = new TreeNode<Operation<?>>(Functions.MUL.build(inputSpec));
		rootB.addChild(new TreeNode<Operation<?>>(Terminals.E.build(inputSpec)));

		final TreeNode<Operation<?>> nodeStrToDouble2 = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble2.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random)
				.build(inputSpec)));
		rootB.addChild(nodeStrToDouble2);
		final TreeChromosome<Operation<?>> chromosomeB = new TreeChromosome<>(rootB);

		// =======================

		final ProgramChromosomeCombinator<Integer> programChromosomeCombinator = new ProgramChromosomeCombinator<>(
				random);
		final List<Chromosome> combinedChromosomes = programChromosomeCombinator.combine(null, chromosomeA, 1, chromosomeB, 1);

		assertNotNull(combinedChromosomes);
		assertEquals(2, combinedChromosomes.size());

	}

	@Test
	public void combineNoCommonTypes() {
		final Random random = new Random();
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		// =======================

		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		root.addChild(new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec)));
		root.addChild(new TreeNode<Operation<?>>(Terminals.E.build(inputSpec)));

		final TreeChromosome<Operation<?>> chromosomeA = new TreeChromosome<>(root);

		// =======================

		final TreeNode<Operation<?>> rootB = new TreeNode<Operation<?>>(Terminals.InputString(random)
				.build(inputSpec));
		final TreeChromosome<Operation<?>> chromosomeB = new TreeChromosome<>(rootB);

		// =======================

		final ProgramChromosomeCombinator<Integer> programChromosomeCombinator = new ProgramChromosomeCombinator<>(
				random);
		final List<Chromosome> combinedChromosomes = programChromosomeCombinator.combine(null, chromosomeA, 1, chromosomeB, 1);

		assertNotNull(combinedChromosomes);
		assertTrue(combinedChromosomes.isEmpty());
	}
}