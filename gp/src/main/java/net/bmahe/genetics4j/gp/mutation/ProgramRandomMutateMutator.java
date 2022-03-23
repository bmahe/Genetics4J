package net.bmahe.genetics4j.gp.mutation;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.program.ProgramGenerator;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;

public class ProgramRandomMutateMutator implements Mutator {
	final static public Logger logger = LogManager.getLogger(ProgramRandomMutateMutator.class);

	private final ProgramGenerator programGenerator;
	private final RandomGenerator randomGenerator;
	private final AbstractEAConfiguration eaConfiguration;
	private final double populationMutationProbability;

	public ProgramRandomMutateMutator(final ProgramGenerator _programGenerator, final RandomGenerator _randomGenerator,
			final AbstractEAConfiguration _eaConfiguration, final double _populationMutationProbability) {
		Validate.notNull(_programGenerator);
		Validate.notNull(_randomGenerator);
		Validate.notNull(_eaConfiguration);
		Validate.inclusiveBetween(0.0, 1.0, _populationMutationProbability);

		this.programGenerator = _programGenerator;
		this.randomGenerator = _randomGenerator;
		this.eaConfiguration = _eaConfiguration;
		this.populationMutationProbability = _populationMutationProbability;
	}

	protected TreeNode<Operation<?>> duplicateAndMutate(final Program program, final TreeNode<Operation<?>> root,
			final int cutPoint, final int nodeIndex, final int currentDepth) {
		Validate.notNull(program);
		Validate.notNull(root);
		Validate.isTrue(cutPoint >= 0);

		final Operation<?> rootData = root.getData();

		if (nodeIndex == cutPoint) {
			final int depth = Math.max(1, program.maxDepth() - currentDepth);
			final int maxSubtreeDepth = depth > 1 ? randomGenerator.nextInt(depth - 1) + 1 : depth;
			return programGenerator.generate(program, maxSubtreeDepth, rootData.returnedType());
		}

		final List<TreeNode<Operation<?>>> children = root.getChildren();

		final TreeNode<Operation<?>> duplicateRoot = new TreeNode<Operation<?>>(rootData);

		int currentIndex = nodeIndex + 1;
		for (int i = 0; i < children.size(); i++) {
			final TreeNode<Operation<?>> treeNode = children.get(i);
			final int childSize = treeNode.getSize();

			final TreeNode<Operation<?>> childCopy = duplicateAndMutate(program,
					treeNode,
					cutPoint,
					currentIndex,
					currentDepth + 1);
			duplicateRoot.addChild(childCopy);
			currentIndex += childSize;
		}

		return duplicateRoot;
	}

	@Override
	public Genotype mutate(final Genotype originalGenotype) {
		Validate.notNull(originalGenotype);

		if (randomGenerator.nextDouble() >= populationMutationProbability) {
			return originalGenotype;
		}

		logger.trace("Mutating genotype {}", originalGenotype);

		final Chromosome[] newChromosomes = new Chromosome[originalGenotype.getSize()];
		final Chromosome[] chromosomes = originalGenotype.getChromosomes();
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
				final int cutPoint = randomGenerator.nextInt(chromosomeSize - 1);

				final TreeNode<Operation<?>> root = treeChromosome.getRoot();
				final TreeNode<Operation<?>> newRoot = duplicateAndMutate(programTreeChromosomeSpec
						.program(), root, cutPoint, 0, 0);
				final TreeChromosome<Operation<?>> newTreeChromosome = new TreeChromosome<>(newRoot);
				newChromosomes[chromosomeIndex] = newTreeChromosome;
			} else {
				final TreeNode<Operation<Object>> newRoot = programGenerator.generate(programTreeChromosomeSpec.program(),
						programTreeChromosomeSpec.program().maxDepth());
				final TreeChromosome<Operation<Object>> newTreeChromosome = new TreeChromosome<>(newRoot);

				newChromosomes[chromosomeIndex] = newTreeChromosome;
			}

			logger.trace("\tChromosome {} - {}", chromosomeIndex, newChromosomes[chromosomeIndex]);
		}

		return new Genotype(newChromosomes);
	}
}