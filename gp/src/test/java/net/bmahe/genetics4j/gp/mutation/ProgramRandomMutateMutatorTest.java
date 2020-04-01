package net.bmahe.genetics4j.gp.mutation;

import static net.bmahe.genetics4j.gp.math.Functions.ADD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.ImmutableProgram;
import net.bmahe.genetics4j.gp.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.InputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.Program;
import net.bmahe.genetics4j.gp.ProgramGenerator;
import net.bmahe.genetics4j.gp.StdProgramGenerator;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;

public class ProgramRandomMutateMutatorTest {

	@Test
	public void noMutate() {
		final Random random = new Random();
		final ProgramGenerator programGenerator = new StdProgramGenerator(random);
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		///////////////////////
		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		final TreeNode<Operation<?>> PINode = new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec));
		root.addChild(PINode);

		final TreeNode<Operation<?>> nodeStrToDouble = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random)
				.build(inputSpec)));
		root.addChild(nodeStrToDouble);
		///////////////////////

		final GenotypeSpec mockGenotypeSpec = mock(GenotypeSpec.class);

		// 0% chance of mutation
		final ProgramRandomMutateMutator programRandomMutateMutator = new ProgramRandomMutateMutator(programGenerator,
				random, mockGenotypeSpec, 0.0);

		final Genotype genotype = new Genotype(new TreeChromosome<Operation<?>>(root));
		final Genotype notMutatedGenotype = programRandomMutateMutator.mutate(genotype);
		assertNotNull(notMutatedGenotype);
		assertEquals(genotype, notMutatedGenotype);
	}

	@Test
	public void simple() {
		final Random random = new Random();
		final ProgramGenerator programGenerator = new StdProgramGenerator(random);
		final InputSpec inputSpec = ImmutableInputSpec.of(List.of(Double.class, String.class));

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD,
				Functions.MUL,
				Functions.DIV,
				Functions.SUB,
				Functions.COS,
				Functions.SIN,
				Functions.EXP);
		programBuilder.addTerminal(Terminals.InputDouble(random),
				Terminals.PI,
				Terminals.E,
				Terminals.Coefficient(random, -50, 100),
				Terminals.CoefficientRounded(random, -25, 25));

		programBuilder.inputSpec(inputSpec);
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		///////////////////////
		final TreeNode<Operation<?>> root = new TreeNode<Operation<?>>(ADD.build(inputSpec));
		final TreeNode<Operation<?>> PINode = new TreeNode<Operation<?>>(Terminals.PI.build(inputSpec));
		root.addChild(PINode);

		final TreeNode<Operation<?>> nodeStrToDouble = new TreeNode<Operation<?>>(
				Functions.STR_TO_DOUBLE.build(inputSpec));
		nodeStrToDouble.addChild(new TreeNode<Operation<?>>(Terminals.InputString(random)
				.build(inputSpec)));
		root.addChild(nodeStrToDouble);
		///////////////////////

		final GenotypeSpec mockGenotypeSpec = mock(GenotypeSpec.class);

		final ProgramRandomMutateMutator programRandomMutateMutator = new ProgramRandomMutateMutator(programGenerator,
				random, mockGenotypeSpec, 1.0);

		final TreeNode<Operation<?>> duplicateAndMutate = programRandomMutateMutator
				.duplicateAndMutate(program, root, 2, 0, 0);
		assertNotNull(duplicateAndMutate);
		assertEquals(2,
				duplicateAndMutate.getChildren()
						.size());
		assertEquals(PINode.getData(),
				duplicateAndMutate.getChild(0)
						.getData());
		assertNotEquals(nodeStrToDouble.getData(),
				duplicateAndMutate.getChild(1)
						.getData());
		assertTrue(duplicateAndMutate.getDepth() <= program.maxDepth());
	}
}