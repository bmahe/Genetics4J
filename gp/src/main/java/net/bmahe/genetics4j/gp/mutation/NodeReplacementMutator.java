package net.bmahe.genetics4j.gp.mutation;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.OperationFactory;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;

public class NodeReplacementMutator implements Mutator {

	private final ProgramHelper programHelper;
	private final Random random;
	private final EAConfiguration eaConfiguration;
	private final double populationMutationProbability;

	public NodeReplacementMutator(final ProgramHelper _programHelper, final Random _random,
			final EAConfiguration _eaConfiguration, final double populationMutationProbability) {
		Validate.notNull(_programHelper);
		Validate.notNull(_random);
		Validate.notNull(_eaConfiguration);
		Validate.inclusiveBetween(0.0, 1.0, populationMutationProbability);

		this.programHelper = _programHelper;
		this.random = _random;
		this.eaConfiguration = _eaConfiguration;
		this.populationMutationProbability = populationMutationProbability;
	}

	protected TreeNode<Operation<?>> duplicateNode(final Program program, final TreeNode<Operation<?>> root,
			final int cutPoint, final int nodeIndex) {
		Validate.notNull(root);
		Validate.isTrue(cutPoint >= 0);

		final Operation<?> rootData = root.getData();
		final List<TreeNode<Operation<?>>> children = root.getChildren();

		final TreeNode<Operation<?>> duplicateRoot = new TreeNode<Operation<?>>(rootData);

		int currentIndex = nodeIndex + 1;
		for (int i = 0; i < children.size(); i++) {
			final TreeNode<Operation<?>> treeNode = children.get(i);
			final int childSize = treeNode.getSize();

			final TreeNode<Operation<?>> childCopy = duplicateAndReplaceNode(program, treeNode, cutPoint, currentIndex);
			duplicateRoot.addChild(childCopy);
			currentIndex += childSize;
		}

		return duplicateRoot;
	}

	protected List<OperationFactory> findReplacementCandidates(final Program program,
			final TreeNode<Operation<?>> root) {
		Validate.notNull(root);

		final Operation<?> rootData = root.getData();

		final Class returnedType = rootData.returnedType();
		final List<Class> acceptedTypes = rootData.acceptedTypes();

		final Set<OperationFactory> functions = program.functions();
		final Set<OperationFactory> terminals = program.terminal();

		final List<OperationFactory> candidates = Stream.concat(functions.stream(), terminals.stream())
				.filter(opFactory -> {
					final boolean b = returnedType.isAssignableFrom(opFactory.returnedType());
					return b;
				})
				.filter(opFactory -> {

					if (opFactory.acceptedTypes().length != acceptedTypes.size()) {
						return false;
					}

					for (int i = 0; i < acceptedTypes.size(); i++) {

						if (acceptedTypes.get(i).isAssignableFrom(opFactory.acceptedTypes()[i]) == false) {
							return false;
						}
					}

					return true;
				})
				.collect(Collectors.toList());

		return candidates;
	}

	protected TreeNode<Operation<?>> duplicateAndReplaceNode(final Program program, final TreeNode<Operation<?>> root,
			final int cutPoint, final int nodeIndex) {
		Validate.notNull(root);
		Validate.isTrue(cutPoint >= 0);

		final Operation<?> rootData = root.getData();

		if (nodeIndex == cutPoint) {

			final List<OperationFactory> candidates = findReplacementCandidates(program, root);

			if (candidates.size() > 0) {

				final OperationFactory chosenOperationFactory = candidates.get(random.nextInt(candidates.size()));
				final Operation operation = chosenOperationFactory.build(program.inputSpec());

				final TreeNode<Operation<?>> replacedNode = new TreeNode<Operation<?>>(operation);
				int currentIndex = nodeIndex + 1;
				for (final TreeNode<Operation<?>> child : root.getChildren()) {
					final int childSize = child.getSize();

					final TreeNode<Operation<?>> childCopy = duplicateAndReplaceNode(program,
							child,
							cutPoint,
							currentIndex);
					replacedNode.addChild(childCopy);
					currentIndex += childSize;

				}

				return replacedNode;
			} else {
				return duplicateNode(program, root, cutPoint, nodeIndex);
			}
		} else {
			return duplicateNode(program, root, cutPoint, nodeIndex);
		}
	}

	@Override
	public Genotype mutate(final Genotype original) {
		Validate.notNull(original);

		if (random.nextDouble() < populationMutationProbability == false) {
			return original;
		}

		final Chromosome[] newChromosomes = new Chromosome[original.getSize()];
		final Chromosome[] chromosomes = original.getChromosomes();
		for (int chromosomeIndex = 0; chromosomeIndex < chromosomes.length; chromosomeIndex++) {
			final ChromosomeSpec chromosomeSpec = eaConfiguration.getChromosomeSpec(chromosomeIndex);
			final Chromosome chromosome = chromosomes[chromosomeIndex];

			if (chromosomeSpec instanceof ProgramTreeChromosomeSpec == false) {
				throw new IllegalArgumentException("This mutator does not support chromosome specs " + chromosomeSpec);
			}

			if (chromosome instanceof TreeChromosome<?> == false) {
				throw new IllegalArgumentException(
						"This mutator does not support chromosome of type " + chromosome.getClass().getSimpleName());
			}

			final ProgramTreeChromosomeSpec programTreeChromosomeSpec = (ProgramTreeChromosomeSpec) chromosomeSpec;

			final TreeChromosome<Operation<?>> treeChromosome = (TreeChromosome<Operation<?>>) chromosome;
			final int chromosomeSize = treeChromosome.getSize();

			if (chromosomeSize > 2) {
				final int cutPoint = random.nextInt(chromosomeSize - 1) + 1;

				final TreeNode<Operation<?>> root = treeChromosome.getRoot();
				final TreeNode<Operation<?>> newRoot = duplicateAndReplaceNode(programTreeChromosomeSpec.program(),
						root,
						cutPoint,
						0);

				final TreeChromosome<Operation<?>> newTreeChromosome = new TreeChromosome<>(newRoot);
				newChromosomes[chromosomeIndex] = newTreeChromosome;
			} else {
				newChromosomes[chromosomeIndex] = chromosome;
			}

		}

		return new Genotype(newChromosomes);
	}
}