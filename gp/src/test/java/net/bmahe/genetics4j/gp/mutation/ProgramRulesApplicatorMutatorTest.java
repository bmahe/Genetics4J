package net.bmahe.genetics4j.gp.mutation;

import static net.bmahe.genetics4j.gp.math.Functions.ADD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Terminations;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.InputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.program.ImmutableProgram;
import net.bmahe.genetics4j.gp.program.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.program.StdProgramGenerator;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.combination.ProgramRandomCombine;
import net.bmahe.genetics4j.gp.spec.mutation.ImmutableRule;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramApplyRules;
import net.bmahe.genetics4j.gp.spec.mutation.Rule;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;

public class ProgramRulesApplicatorMutatorTest {
	final static public Logger logger = LogManager.getLogger(ProgramRulesApplicatorMutatorTest.class);

	@Test(expected = NullPointerException.class)
	public void ctorNoRules() {
		final GenotypeSpec genotypeSpec = mock(GenotypeSpec.class);
		new ProgramRulesApplicatorMutator(null, genotypeSpec);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorEmptyRules() {
		final GenotypeSpec genotypeSpec = mock(GenotypeSpec.class);
		final List<Rule> rules = Collections.emptyList();
		new ProgramRulesApplicatorMutator(rules, genotypeSpec);
	}

	@Test
	public void dupplicateAndApplyNoApplicableRule() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD, Functions.MUL, Functions.DIV, Functions.SUB, Functions.COS,
				Functions.SIN, Functions.EXP);
		programBuilder.addTerminal(Terminals.InputDouble(random), Terminals.PI, Terminals.E,
				Terminals.Coefficient(random, -50, 100), Terminals.CoefficientRounded(random, -25, 25));

		programBuilder.inputSpec(inputSpec);
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		///////////////////////
		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		final TreeNode<Operation<?>> PINode = new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec));
		root.addChild(PINode);

		final TreeNode<Operation<?>> nodeStrToDouble = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random).build(inputSpec)));
		root.addChild(nodeStrToDouble);
		///////////////////////

		final GenotypeSpec mockGenotypeSpec = mock(GenotypeSpec.class);

		final List<Rule> rules = List
				.of(ImmutableRule.of((node) -> false, (p, n) -> new TreeNode<>(Terminals.E.build(inputSpec))));
		final ProgramRulesApplicatorMutator programRulesApplicatorMutator = new ProgramRulesApplicatorMutator(rules,
				mockGenotypeSpec);

		final TreeNode<Operation<?>> outputRule = programRulesApplicatorMutator.duplicateAndApplyRule(program, root);
		assertNotNull(outputRule);
		assertEquals(root.getData(), outputRule.getData());
		assertEquals(root.getSize(), outputRule.getSize());
	}

	@Test
	public void dupplicateAndApplyWithOneApplicableRule() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD, Functions.MUL, Functions.DIV, Functions.SUB, Functions.COS,
				Functions.SIN, Functions.EXP);
		programBuilder.addTerminal(Terminals.InputDouble(random), Terminals.PI, Terminals.E,
				Terminals.Coefficient(random, -50, 100), Terminals.CoefficientRounded(random, -25, 25));

		programBuilder.inputSpec(inputSpec);
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		///////////////////////
		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		final TreeNode<Operation<?>> PINode = new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec));
		root.addChild(PINode);

		final TreeNode<Operation<?>> nodeStrToDouble = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random).build(inputSpec)));
		root.addChild(nodeStrToDouble);
		///////////////////////

		final GenotypeSpec mockGenotypeSpec = mock(GenotypeSpec.class);
		final TreeNode<Operation<?>> replacement = new TreeNode<>(Terminals.E.build(inputSpec));

		final List<Rule> rules = List.of(ImmutableRule
				.of((node) -> Functions.NAME_STR_TO_DOUBLE.equals(node.getData().getName()), (p, n) -> replacement));
		final ProgramRulesApplicatorMutator programRulesApplicatorMutator = new ProgramRulesApplicatorMutator(rules,
				mockGenotypeSpec);

		final TreeNode<Operation<?>> outputRule = programRulesApplicatorMutator.duplicateAndApplyRule(program, root);
		assertNotNull(outputRule);
		assertEquals(root.getData(), outputRule.getData());
		assertEquals(root.getSize() - 1, outputRule.getSize());
		assertEquals(root.getChild(0).getData(), outputRule.getChild(0).getData());
		assertEquals(PINode.getData(), outputRule.getChild(0).getData());
		assertEquals(replacement.getData(), outputRule.getChild(1).getData());
	}

	@Test
	public void mutateNoApplicableRule() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD, Functions.MUL, Functions.DIV, Functions.SUB, Functions.COS,
				Functions.SIN, Functions.EXP);
		programBuilder.addTerminal(Terminals.InputDouble(random), Terminals.PI, Terminals.E,
				Terminals.Coefficient(random, -50, 100), Terminals.CoefficientRounded(random, -25, 25));

		programBuilder.inputSpec(inputSpec);
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		///////////////////////
		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		final TreeNode<Operation<?>> PINode = new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec));
		root.addChild(PINode);

		final TreeNode<Operation<?>> nodeStrToDouble = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random).build(inputSpec)));
		root.addChild(nodeStrToDouble);
		///////////////////////

		final List<Rule> rules = List
				.of(ImmutableRule.of((node) -> false, (p, n) -> new TreeNode<>(Terminals.E.build(inputSpec))));

		final net.bmahe.genetics4j.core.spec.GenotypeSpec.Builder genotypeSpecBuilder = new GenotypeSpec.Builder();
		genotypeSpecBuilder.chromosomeSpecs(ProgramTreeChromosomeSpec.of(program))
				.parentSelectionPolicy(TournamentSelection.build(3))
				.survivorSelectionPolicy(TournamentSelection.build(3))
				.offspringRatio(0.90d)
				.combinationPolicy(ProgramRandomCombine.build())
				.mutationPolicies(ProgramApplyRules.of(rules))
				.optimization(Optimization.MINIMIZE)
				.termination(Terminations.ofMaxGeneration(1000))
				.fitness((genoType) -> {
					return 0.0d;
				});
		final GenotypeSpec genotypeSpec = genotypeSpecBuilder.build();

		final ProgramRulesApplicatorMutator programRulesApplicatorMutator = new ProgramRulesApplicatorMutator(rules,
				genotypeSpec);

		final Genotype genotype = new Genotype(new TreeChromosome<Operation<?>>(root));
		final Genotype mutated = programRulesApplicatorMutator.mutate(genotype);
		assertNotNull(mutated);

		final TreeChromosome<Operation<?>> mutatedChromosome = mutated.getChromosome(0, TreeChromosome.class);
		final TreeNode<Operation<?>> outputRule = mutatedChromosome.getRoot();
		assertNotNull(outputRule);
		assertEquals(root.getData(), outputRule.getData());
		assertEquals(root.getSize(), outputRule.getSize());
	}

	@Test
	public void mutateWithOneApplicableRule() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD, Functions.MUL, Functions.DIV, Functions.SUB, Functions.COS,
				Functions.SIN, Functions.EXP);
		programBuilder.addTerminal(Terminals.InputDouble(random), Terminals.PI, Terminals.E,
				Terminals.Coefficient(random, -50, 100), Terminals.CoefficientRounded(random, -25, 25));

		programBuilder.inputSpec(inputSpec);
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		///////////////////////
		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		final TreeNode<Operation<?>> PINode = new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec));
		root.addChild(PINode);

		final TreeNode<Operation<?>> nodeStrToDouble = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random).build(inputSpec)));
		root.addChild(nodeStrToDouble);
		///////////////////////

		final TreeNode<Operation<?>> replacement = new TreeNode<>(Terminals.E.build(inputSpec));

		final List<Rule> rules = List.of(ImmutableRule
				.of((node) -> Functions.NAME_STR_TO_DOUBLE.equals(node.getData().getName()), (p, n) -> replacement));
		final net.bmahe.genetics4j.core.spec.GenotypeSpec.Builder genotypeSpecBuilder = new GenotypeSpec.Builder();
		genotypeSpecBuilder.chromosomeSpecs(ProgramTreeChromosomeSpec.of(program))
				.parentSelectionPolicy(TournamentSelection.build(3))
				.survivorSelectionPolicy(TournamentSelection.build(3))
				.offspringRatio(0.90d)
				.combinationPolicy(ProgramRandomCombine.build())
				.mutationPolicies(ProgramApplyRules.of(rules))
				.optimization(Optimization.MINIMIZE)
				.termination(Terminations.ofMaxGeneration(1000))
				.fitness((genoType) -> {
					return 0.0d;
				});
		final GenotypeSpec genotypeSpec = genotypeSpecBuilder.build();

		final ProgramRulesApplicatorMutator programRulesApplicatorMutator = new ProgramRulesApplicatorMutator(rules,
				genotypeSpec);

		final Genotype genotype = new Genotype(new TreeChromosome<Operation<?>>(root));
		final Genotype mutated = programRulesApplicatorMutator.mutate(genotype);
		assertNotNull(mutated);

		final TreeChromosome<Operation<?>> mutatedChromosome = mutated.getChromosome(0, TreeChromosome.class);
		final TreeNode<Operation<?>> outputRule = mutatedChromosome.getRoot();
		assertNotNull(outputRule);
		assertEquals(root.getData(), outputRule.getData());

		logger.info("Root: {}", TreeNodeUtils.toStringTreeNode(root));
		logger.info("OutputRoot: {}", TreeNodeUtils.toStringTreeNode(outputRule));

		assertEquals(root.getSize() - 1, outputRule.getSize());
		assertEquals(root.getChild(0).getData(), outputRule.getChild(0).getData());
		assertEquals(PINode.getData(), outputRule.getChild(0).getData());
		assertEquals(replacement.getData(), outputRule.getChild(1).getData());
	}
}