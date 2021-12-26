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
import net.bmahe.genetics4j.gp.spec.mutation.TrimTree;

public class TrimTreeMutator implements Mutator {
	final static public Logger logger = LogManager.getLogger(TrimTreeMutator.class);

	private final ProgramGenerator programGenerator;
	private final RandomGenerator randomGenerator;
	private final AbstractEAConfiguration eaConfiguration;
	private final TrimTree trimTree;

	public TrimTreeMutator(final ProgramGenerator _programGenerator, final RandomGenerator _randomGenerator,
			final AbstractEAConfiguration _eaConfiguration, final TrimTree _trimTree) {
		Validate.notNull(_programGenerator);
		Validate.notNull(_randomGenerator);
		Validate.notNull(_eaConfiguration);
		Validate.notNull(_trimTree);

		this.programGenerator = _programGenerator;
		this.randomGenerator = _randomGenerator;
		this.eaConfiguration = _eaConfiguration;
		this.trimTree = _trimTree;
	}

	protected TreeNode<Operation<?>> duplicateAndMutate(final Program program, final TreeNode<Operation<?>> root,
			final int maxDepth, final int currentDepth) {
		Validate.notNull(program);
		Validate.notNull(root);

		final Operation<?> rootData = root.getData();

		if (currentDepth == maxDepth && root.getSize() > 1) {
			return programGenerator.generate(program, 1, rootData.returnedType());
		}

		final List<TreeNode<Operation<?>>> children = root.getChildren();

		final TreeNode<Operation<?>> duplicateRoot = new TreeNode<Operation<?>>(rootData);

		for (int i = 0; i < children.size(); i++) {
			final TreeNode<Operation<?>> treeNode = children.get(i);
			final int childSize = treeNode.getSize();

			final TreeNode<Operation<?>> childCopy = duplicateAndMutate(program, treeNode, maxDepth, currentDepth + 1);
			duplicateRoot.addChild(childCopy);
		}

		return duplicateRoot;
	}

	private int maxDepthValue(final Program program, final TrimTree trimTree) {
		Validate.notNull(program);
		Validate.notNull(trimTree);

		return trimTree.maxDepth().orElseGet(() -> program.maxDepth());
	}

	@Override
	public Genotype mutate(final Genotype originalGenotype) {
		Validate.notNull(originalGenotype);

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
			final Program program = programTreeChromosomeSpec.program();

			final TreeChromosome<Operation<?>> treeChromosome = (TreeChromosome<Operation<?>>) chromosome;

			final int maxDepthValue = maxDepthValue(program, trimTree);

			if (treeChromosome.getRoot().getDepth() > maxDepthValue) {
				final TreeNode<Operation<?>> root = treeChromosome.getRoot();
				final TreeNode<Operation<?>> newRoot = duplicateAndMutate(program, root, maxDepthValue, 0);
				final TreeChromosome<Operation<?>> newTreeChromosome = new TreeChromosome<>(newRoot);
				newChromosomes[chromosomeIndex] = newTreeChromosome;
			} else {
				newChromosomes[chromosomeIndex] = chromosome;
			}

			logger.trace("\tChromosome {} - {}", chromosomeIndex, newChromosomes[chromosomeIndex]);
		}

		return new Genotype(newChromosomes);
	}
}