package net.bmahe.genetics4j.gp.mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.mutation.Rule;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;

public class ProgramRulesApplicatorMutator implements Mutator {
	final static public Logger logger = LogManager.getLogger(ProgramRulesApplicatorMutator.class);

	private final List<Rule> rules;
	private final EAConfiguration eaConfiguration;

	public ProgramRulesApplicatorMutator(final List<Rule> _rules, final EAConfiguration _eaConfiguration) {
		Validate.notNull(_rules);
		Validate.isTrue(_rules.isEmpty() == false);
		Validate.notNull(_eaConfiguration);

		this.rules = _rules;
		this.eaConfiguration = _eaConfiguration;
	}

	protected TreeNode<Operation<?>> duplicateAndApplyRule(final Program program, final TreeNode<Operation<?>> root) {
		Validate.notNull(root);

		logger.trace("A - {}", TreeNodeUtils.toStringTreeNode(root));

		final Operation<?> rootOperation = root.getData();
		final List<TreeNode<Operation<?>>> children = root.getChildren();

		TreeNode<Operation<?>> currentRoot = new TreeNode<Operation<?>>(rootOperation);

		if (children.size() > 0) {
			final List<TreeNode<Operation<?>>> newChildren = new ArrayList<>();
			for (int i = 0; i < children.size(); i++) {
				final TreeNode<Operation<?>> childNode = children.get(i);

				final TreeNode<Operation<?>> childCopy = duplicateAndApplyRule(program, childNode);
				newChildren.add(childCopy);

			}
			currentRoot.addChildren(newChildren);
		}

		boolean done = false;
		while (done == false) {
			final TreeNode<Operation<?>> localRoot = currentRoot;

			logger.trace("B - {}", TreeNodeUtils.toStringTreeNode(localRoot));

			final Optional<Rule> applicableRule = rules.stream().filter((rule) -> rule.test(localRoot)).findFirst();
			final Optional<TreeNode<Operation<?>>> newRootOpt = applicableRule.map(x -> x.apply(program, localRoot));

			done = newRootOpt.map(newRoot -> TreeNodeUtils.areSame(newRoot, localRoot)).orElse(true);

			if (newRootOpt.isPresent()) {
				currentRoot = newRootOpt.get();
			}
		}

		return currentRoot;
	}

	@Override
	public Genotype mutate(final Genotype original) {
		Validate.notNull(original);

		logger.trace("Mutating genotype: {}", original);

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
			final TreeNode<Operation<?>> root = treeChromosome.getRoot();

			if (logger.isTraceEnabled()) {
				logger.trace("Original chromosome {} - {}", chromosomeIndex, TreeNodeUtils.toStringTreeNode(root));
			}

			final TreeNode<Operation<?>> newRoot = duplicateAndApplyRule(programTreeChromosomeSpec.program(), root);
			final TreeChromosome<Operation<?>> newTreeChromosome = new TreeChromosome<>(newRoot);
			newChromosomes[chromosomeIndex] = newTreeChromosome;

		}

		return new Genotype(newChromosomes);
	}
}