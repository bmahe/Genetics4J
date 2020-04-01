package net.bmahe.genetics4j.gp.combination;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.gp.Operation;

final class ProgramChromosomeCombinator implements ChromosomeCombinator {

	private final Random random;

	public ProgramChromosomeCombinator(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@SuppressWarnings("rawtypes")
	protected Map<Class, List<TreeNode<Operation<?>>>> returnedTypeToNode(final TreeNode<Operation<?>> root) {
		Validate.notNull(root);

		final Map<Class, List<TreeNode<Operation<?>>>> returnedTypeIndex = new HashMap<>();

		final Deque<TreeNode<Operation<?>>> nodes = new ArrayDeque<>();
		nodes.add(root);

		while (nodes.isEmpty() == false) {
			final TreeNode<Operation<?>> node = nodes.remove();

			final Operation<?> operation = node.getData();
			final Class returnedType = operation.returnedType();

			returnedTypeIndex.computeIfAbsent(returnedType, k -> new ArrayList<>());
			returnedTypeIndex.get(returnedType)
					.add(node);

			if (node.getChildren() != null && node.getChildren()
					.isEmpty() == false) {
				nodes.addAll(node.getChildren());
			}
		}

		return returnedTypeIndex;
	}

	protected TreeNode<Operation<?>> copyAndReplace(final TreeNode<Operation<?>> root,
			final TreeNode<Operation<?>> replaced, final TreeNode<Operation<?>> replacement) {
		Validate.notNull(root);
		Validate.notNull(replaced);
		Validate.notNull(replacement);

		if (root == replaced) {
			return copyAndReplace(replacement, replaced, replacement);
		}

		final Operation<?> data = root.getData();
		final List<TreeNode<Operation<?>>> children = root.getChildren();

		final List<TreeNode<Operation<?>>> copiedChildren = children == null ? null
				: children.stream()
						.map(child -> copyAndReplace(child, replaced, replacement))
						.collect(Collectors.toList());

		final TreeNode<Operation<?>> copy = new TreeNode<>(data);
		if (children.isEmpty() == false) {
			copy.addChildren(copiedChildren);
		}

		return copy;
	}

	@SuppressWarnings("rawtypes")
	private final TreeNode<Operation<?>> mix(final TreeNode<Operation<?>> rootA, final TreeNode<Operation<?>> rootB,
			final Set<Class> acceptableClasses, final Map<Class, List<TreeNode<Operation<?>>>> returnedTypeToNodeA,
			final Map<Class, List<TreeNode<Operation<?>>>> returnedTypeToNodeB) {
		Validate.notNull(rootA);
		Validate.notNull(rootB);
		Validate.notNull(acceptableClasses);
		Validate.isTrue(acceptableClasses.isEmpty() == false);
		Validate.notNull(returnedTypeToNodeA);
		Validate.notNull(returnedTypeToNodeB);

		final int targetClassIndex = random.nextInt(acceptableClasses.size());
		final Class targetClass = acceptableClasses.stream()
				.skip(targetClassIndex)
				.findFirst()
				.get();

		final List<TreeNode<Operation<?>>> candidateReplacedNodes = returnedTypeToNodeA.get(targetClass);
		final TreeNode<Operation<?>> replacedNode = candidateReplacedNodes
				.get(random.nextInt(candidateReplacedNodes.size()));

		final List<TreeNode<Operation<?>>> candidateReplacementNodes = returnedTypeToNodeB.get(targetClass);
		final TreeNode<Operation<?>> replacementNode = candidateReplacementNodes
				.get(random.nextInt(candidateReplacementNodes.size()));

		return copyAndReplace(rootA, replacedNode, replacementNode);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);

		if (chromosome1 instanceof TreeChromosome<?> == false) {
			throw new IllegalArgumentException("This mutator does not support chromosome of type " + chromosome1.getClass()
					.getSimpleName());
		}

		if (chromosome2 instanceof TreeChromosome<?> == false) {
			throw new IllegalArgumentException("This mutator does not support chromosome of type " + chromosome2.getClass()
					.getSimpleName());
		}

		if (chromosome1 == chromosome2) {
			return Collections.emptyList();
		}

		final TreeChromosome<Operation<?>> treeChromosome1 = (TreeChromosome<Operation<?>>) chromosome1;
		final TreeNode<Operation<?>> root1 = treeChromosome1.getRoot();
		final Map<Class, List<TreeNode<Operation<?>>>> returnedTypeToNode1 = returnedTypeToNode(root1);

		final TreeChromosome<Operation<?>> treeChromosome2 = (TreeChromosome<Operation<?>>) chromosome2;
		final TreeNode<Operation<?>> root2 = treeChromosome2.getRoot();
		final Map<Class, List<TreeNode<Operation<?>>>> returnedTypeToNode2 = returnedTypeToNode(root2);

		final Set<Class> acceptableClasses = new HashSet<>();
		acceptableClasses.addAll(returnedTypeToNode1.keySet());
		acceptableClasses.retainAll(returnedTypeToNode2.keySet());

		final List<Chromosome> children = new ArrayList<>();

		if (acceptableClasses.isEmpty() == false) {

			final TreeNode<Operation<?>> child1 = mix(root1,
					root2,
					acceptableClasses,
					returnedTypeToNode1,
					returnedTypeToNode2);
			final TreeChromosome<Operation<?>> child1Chromosome = new TreeChromosome<Operation<?>>(child1);

			final TreeNode<Operation<?>> child2 = mix(root2,
					root1,
					acceptableClasses,
					returnedTypeToNode2,
					returnedTypeToNode1);
			final TreeChromosome<Operation<?>> child2Chromosome = new TreeChromosome<Operation<?>>(child2);

			children.add(child1Chromosome);
			children.add(child2Chromosome);
		}

		return children;
	}
}