package net.bmahe.genetics4j.gp.mutation;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.OperationFactory;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;

public class ProgramRandomPruneMutator implements Mutator {

	private final ProgramHelper programHelper;
	private final Random random;
	private final GenotypeSpec genotypeSpec;
	private final double populationMutationProbability;

	public ProgramRandomPruneMutator(final ProgramHelper _programHelper, final Random _random,
			final GenotypeSpec _genotypeSpec, final double populationMutationProbability) {
		Validate.notNull(_programHelper);
		Validate.notNull(_random);
		Validate.notNull(_genotypeSpec);
		Validate.inclusiveBetween(0.0, 1.0, populationMutationProbability);

		this.programHelper = _programHelper;
		this.random = _random;
		this.genotypeSpec = _genotypeSpec;
		this.populationMutationProbability = populationMutationProbability;
	}

	protected TreeNode<Operation<?>> duplicateAndCut(final Program program, final TreeNode<Operation<?>> root,
			final int cutPoint, final int nodeIndex) {
		Validate.notNull(root);
		Validate.isTrue(cutPoint >= 0);

		final Operation<?> rootData = root.getData();

		if (nodeIndex == cutPoint) {
			final OperationFactory randomTerminal = programHelper.pickRandomTerminal(program, rootData.returnedType());
			final Operation<?> operation = randomTerminal.build(program.inputSpec());
			return new TreeNode<Operation<?>>(operation);
		} else {
			final List<TreeNode<Operation<?>>> children = root.getChildren();

			final TreeNode<Operation<?>> duplicateRoot = new TreeNode<Operation<?>>(rootData);

			int currentIndex = nodeIndex + 1;
			for (int i = 0; i < children.size(); i++) {
				final TreeNode<Operation<?>> treeNode = children.get(i);
				final int childSize = treeNode.getSize();

				final TreeNode<Operation<?>> childCopy = duplicateAndCut(program, treeNode, cutPoint, currentIndex);
				duplicateRoot.addChild(childCopy);
				currentIndex += childSize;
			}

			return duplicateRoot;
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
			final ChromosomeSpec chromosomeSpec = genotypeSpec.getChromosomeSpec(chromosomeIndex);
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
				final TreeNode<Operation<?>> newRoot = duplicateAndCut(programTreeChromosomeSpec.program(), root,
						cutPoint, 0);
				final TreeChromosome<Operation<?>> newTreeChromosome = new TreeChromosome<>(newRoot);
				newChromosomes[chromosomeIndex] = newTreeChromosome;
			} else {
				newChromosomes[chromosomeIndex] = chromosome;
			}

		}

		return new Genotype(newChromosomes);
	}
}