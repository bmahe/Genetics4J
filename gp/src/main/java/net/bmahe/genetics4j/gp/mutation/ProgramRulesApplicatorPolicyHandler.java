package net.bmahe.genetics4j.gp.mutation;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.bmahe.genetics4j.gp.Program;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramApplyRules;
import net.bmahe.genetics4j.gp.spec.mutation.Rule;

public class ProgramRulesApplicatorPolicyHandler implements MutationPolicyHandler {
	final static public Logger logger = LogManager.getLogger(ProgramRulesApplicatorPolicyHandler.class);

	@Override
	public boolean canHandle(final MutationPolicyHandlerResolver mutationPolicyHandlerResolver,
			final MutationPolicy mutationPolicy) {
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);

		return mutationPolicy instanceof ProgramApplyRules;
	}

	@Override
	public Mutator createMutator(final GeneticSystemDescriptor geneticSystemDescriptor, final GenotypeSpec genotypeSpec,
			final MutationPolicyHandlerResolver mutationPolicyHandlerResolver, final MutationPolicy mutationPolicy) {
		Validate.notNull(geneticSystemDescriptor);
		Validate.notNull(genotypeSpec);
		Validate.notNull(mutationPolicyHandlerResolver);
		Validate.notNull(mutationPolicy);
		Validate.isInstanceOf(ProgramApplyRules.class, mutationPolicy);

		final ProgramApplyRules programApplyRules = (ProgramApplyRules) mutationPolicy;

		final List<Rule> rules = programApplyRules.rules();

		return new Mutator() {

			private TreeNode<Operation<?>> duplicateAndMutate(final Program program, final TreeNode<Operation<?>> root) {
				Validate.notNull(root);

				final Operation<?> rootData = root.getData();
				final List<TreeNode<Operation<?>>> children = root.getChildren();

				final Optional<Rule> applicableRule = rules.stream()
						.filter((rule) -> rule.predicate()
								.test(root))
						.findFirst();

				if (applicableRule.isPresent()) {

					return applicableRule.get()
							.simplify()
							.apply(program, root);

				} else {

					final TreeNode<Operation<?>> duplicateRoot = new TreeNode<Operation<?>>(rootData);

					for (int i = 0; i < children.size(); i++) {
						final TreeNode<Operation<?>> treeNode = children.get(i);
						final int childSize = treeNode.getSize();

						final TreeNode<Operation<?>> childCopy = duplicateAndMutate(program, treeNode);
						duplicateRoot.addChild(childCopy);
					}

					return duplicateRoot;
				}
			}

			@Override
			public Genotype mutate(final Genotype original) {
				Validate.notNull(original);

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

					final TreeNode<Operation<?>> root = treeChromosome.getRoot();
					final TreeNode<Operation<?>> newRoot = duplicateAndMutate(programTreeChromosomeSpec.program(), root);
					final TreeChromosome<Operation<?>> newTreeChromosome = new TreeChromosome<>(newRoot);
					newChromosomes[chromosomeIndex] = newTreeChromosome;

				}

				return new Genotype(newChromosomes);
			}
		};
	}

}