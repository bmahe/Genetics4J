package net.bmahe.genetics4j.gp.utils;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
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

	public static boolean areSame(final Genotype genotypeA, final Genotype genotypeB, final int chromosomeIndex) {
		Validate.notNull(genotypeA);
		Validate.notNull(genotypeB);
		Validate.isTrue(chromosomeIndex >= 0);
		Validate.isTrue(chromosomeIndex < genotypeA.getSize());
		Validate.isTrue(chromosomeIndex < genotypeB.getSize());

		@SuppressWarnings("unchecked")
		final var chromosomeA = (TreeChromosome<Operation<?>>) genotypeA.getChromosome(chromosomeIndex);
		final TreeNode<Operation<?>> rootNodeA = chromosomeA.getRoot();

		@SuppressWarnings("unchecked")
		final var chromosomeB = (TreeChromosome<Operation<?>>) genotypeB.getChromosome(chromosomeIndex);
		final TreeNode<Operation<?>> rootNodeB = chromosomeB.getRoot();

		return areSame(rootNodeA, rootNodeB);
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

	public static String toStringTreeNode(final Genotype genotype, final int chromosomeIndex) {
		Validate.notNull(genotype);
		Validate.isTrue(chromosomeIndex >= 0);
		Validate.isTrue(chromosomeIndex < genotype.getSize());

		@SuppressWarnings("unchecked")
		final var chromosome = (TreeChromosome<Operation<?>>) genotype.getChromosome(chromosomeIndex);
		final TreeNode<Operation<?>> rootNode = chromosome.getRoot();

		return TreeNodeUtils.toStringTreeNode(rootNode);
	}
}