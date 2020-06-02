package net.bmahe.genetics4j.gp.utils;

import java.util.Comparator;
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

	public static Comparator<TreeNode<Operation<?>>> TREE_NODE_OPERATION_COMPARATOR = new Comparator<>() {

		@Override
		public int compare(final TreeNode<Operation<?>> o1, final TreeNode<Operation<?>> o2) {
			return compare(o1, o2);
		}
	};

	/**
	 * Simple strict comparison.
	 * <p>
	 * Does not take in account commutativity or rotations
	 * 
	 * @param <T>
	 * @param rootA
	 * @param rootB
	 * @return
	 */
	public static <T> int compare(final TreeNode<Operation<T>> rootA, final TreeNode<Operation<T>> rootB) {
		if (rootA == null && rootB == null) {
			return 0;
		}

		if (rootA == null) {
			return -1;
		}

		if (rootB == null) {
			return 1;
		}

		final Operation<T> dataA = rootA.getData();
		final Operation<T> dataB = rootB.getData();

		final int opNameCompare = dataA.getName().compareTo(dataB.getName());
		if (opNameCompare != 0) {
			return opNameCompare;
		}

		final int opPrettyNameCompare = dataA.getPrettyName().compareTo(dataB.getPrettyName());
		if (opPrettyNameCompare != 0) {
			return opPrettyNameCompare;
		}

		if (dataA.getArity() < dataB.getArity()) {
			return -1;
		}

		if (dataA.getArity() > dataB.getArity()) {
			return 1;
		}

		for (int i = 0; i < dataA.getArity(); i++) {
			final int classCompare = dataA.acceptedTypes()
					.get(i)
					.getCanonicalName()
					.compareTo(dataB.acceptedTypes().get(i).getCanonicalName());

			if (classCompare != 0) {
				return classCompare;
			}
		}

		final int returnClassCompare = dataA.returnedType()
				.getCanonicalName()
				.compareTo(dataB.returnedType().getCanonicalName());

		if (returnClassCompare != 0) {
			return returnClassCompare;
		}

		final var childrenA = rootA.getChildren();
		final var childrenB = rootB.getChildren();

		if (childrenA.size() < childrenB.size()) {
			return -1;
		}

		if (childrenA.size() > childrenB.size()) {
			return 1;
		}

		for (int i = 0; i < childrenA.size(); i++) {
			final TreeNode<Operation<T>> childA = childrenA.get(i);
			final TreeNode<Operation<T>> childB = childrenB.get(i);

			final int childCompare = compare(childA, childB);
			if (childCompare != 0) {
				return childCompare;
			}
		}

		return 0;
	}

	public static <T> int compare(final Genotype genotypeA, final Genotype genotypeB, final int chromosomeIndex) {
		Validate.notNull(genotypeA);
		Validate.notNull(genotypeB);
		Validate.isTrue(chromosomeIndex >= 0);
		Validate.isTrue(chromosomeIndex < genotypeA.getSize());
		Validate.isTrue(chromosomeIndex < genotypeB.getSize());

		@SuppressWarnings("unchecked")
		final var chromosomeA = (TreeChromosome<Operation<T>>) genotypeA.getChromosome(chromosomeIndex);
		final TreeNode<Operation<T>> rootNodeA = chromosomeA.getRoot();

		@SuppressWarnings("unchecked")
		final var chromosomeB = (TreeChromosome<Operation<T>>) genotypeB.getChromosome(chromosomeIndex);
		final TreeNode<Operation<T>> rootNodeB = chromosomeB.getRoot();

		return compare(rootNodeA, rootNodeB);
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

	public static <T> boolean areSame(final Genotype genotypeA, final Genotype genotypeB, final int chromosomeIndex) {
		Validate.notNull(genotypeA);
		Validate.notNull(genotypeB);
		Validate.isTrue(chromosomeIndex >= 0);
		Validate.isTrue(chromosomeIndex < genotypeA.getSize());
		Validate.isTrue(chromosomeIndex < genotypeB.getSize());

		@SuppressWarnings("unchecked")
		final var chromosomeA = (TreeChromosome<T>) genotypeA.getChromosome(chromosomeIndex);
		final TreeNode<T> rootNodeA = chromosomeA.getRoot();

		@SuppressWarnings("unchecked")
		final var chromosomeB = (TreeChromosome<T>) genotypeB.getChromosome(chromosomeIndex);
		final TreeNode<T> rootNodeB = chromosomeB.getRoot();

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