package net.bmahe.genetics4j.gp.mutation;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandler;
import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.OperationFactory;
import net.bmahe.genetics4j.gp.Program;
import net.bmahe.genetics4j.gp.ProgramGenerator;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;

public class ProgramRandomPrunePolicyHandler implements MutationPolicyHandler {

	private final Random random;
	private final ProgramGenerator programGenerator;

	public ProgramRandomPrunePolicyHandler(final Random _random, final ProgramGenerator _programGenerator) {
		Validate.notNull(_random);
		Validate.notNull(_programGenerator);

		this.random = _random;
		this.programGenerator = _programGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof ProgramRandomPrune;
	}

	@Override
	public Mutator createMutator(final GeneticSystemDescriptor geneticSystemDescriptor, final GenotypeSpec genotypeSpec,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(ProgramRandomPrune.class, mutationPolicy);

		final ProgramRandomPrune programRandomPrune = (ProgramRandomPrune) mutationPolicy;
		final double populationMutationProbability = programRandomPrune.populationMutationProbability();

		return new Mutator() {

			private TreeNode<Operation<?>> duplicateAndCut(final Program program, final TreeNode<Operation<?>> root,
					final int cutPoint, final int nodeIndex) {
				Validate.notNull(root);
				Validate.isTrue(cutPoint >= 0);

				final Operation<?> rootData = root.getData();

				if (nodeIndex == cutPoint) {
					final OperationFactory randomTerminal = programGenerator.pickRandomTerminal(program,
							rootData.returnedType());
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

				if (random.nextDouble() < populationMutationProbability) {
					final Chromosome[] newChromosomes = new Chromosome[original.getSize()];
					final Chromosome[] chromosomes = original.getChromosomes();
					for (int chromosomeIndex = 0; chromosomeIndex < chromosomes.length; chromosomeIndex++) {
						final ChromosomeSpec chromosomeSpec = genotypeSpec.getChromosomeSpec(chromosomeIndex);
						final Chromosome chromosome = chromosomes[chromosomeIndex];

						if (chromosomeSpec instanceof ProgramTreeChromosomeSpec == false) {
							throw new IllegalArgumentException(
									"This mutator does not support chromosome specs " + chromosomeSpec);
						}

						if (chromosome instanceof TreeChromosome<?> == false) {
							throw new IllegalArgumentException(
									"This mutator does not support chromosome of type " + chromosome.getClass()
											.getSimpleName());
						}

						final ProgramTreeChromosomeSpec programTreeChromosomeSpec = (ProgramTreeChromosomeSpec) chromosomeSpec;

						final TreeChromosome<Operation<?>> treeChromosome = (TreeChromosome<Operation<?>>) chromosome;
						final int chromosomeSize = treeChromosome.getSize();

						if (chromosomeSize > 2) {
							final int cutPoint = random.nextInt(chromosomeSize - 1) + 1;

							final TreeNode<Operation<?>> root = treeChromosome.getRoot();
							final TreeNode<Operation<?>> newRoot = duplicateAndCut(programTreeChromosomeSpec.program(),
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
				} else {
					return original;
				}
			}
		};
	}

}