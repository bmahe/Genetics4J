package net.bmahe.genetics4j.gp;

import java.util.Arrays;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.program.ImmutableProgram;
import net.bmahe.genetics4j.gp.program.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.program.ProgramGenerator;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.program.StdProgramGenerator;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;

public class SimpleGPTest {
	final static public Logger logger = LogManager.getLogger(SimpleGPTest.class);

	@Test
	public void simple() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final ProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

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
				Terminals.CoefficientRounded(random, -5, 7));

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		programBuilder.maxDepth(4);

		final Program program = programBuilder.build();
		for (int i = 0; i < 10; i++) {
			final TreeNode<Operation<?>> operation = programGenerator.generate(program);
			TreeChromosome<Operation<?>> treeChromosome = new TreeChromosome<>(operation);
			logger.info(TreeNodeUtils.toStringTreeNode(treeChromosome.getRoot()));
		}
	}
}