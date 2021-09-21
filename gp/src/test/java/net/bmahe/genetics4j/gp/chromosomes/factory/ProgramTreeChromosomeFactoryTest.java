package net.bmahe.genetics4j.gp.chromosomes.factory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.program.ImmutableProgram;
import net.bmahe.genetics4j.gp.program.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.program.StdProgramGenerator;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;

public class ProgramTreeChromosomeFactoryTest {

	@Test
	public void mustHaveAProgramGenerator() {
		assertThrows(NullPointerException.class, () -> new ProgramTreeChromosomeFactory(null));
	}

	@Test
	public void mustHaveASpecForCanHandle() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator stdProgramGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramTreeChromosomeFactory programTreeChromosomeFactory = new ProgramTreeChromosomeFactory(
				stdProgramGenerator);

		assertThrows(NullPointerException.class, () -> programTreeChromosomeFactory.canHandle(null));
	}

	@Test
	public void mustHandleProgramTreeChromosomeSpec() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator stdProgramGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramTreeChromosomeFactory programTreeChromosomeFactory = new ProgramTreeChromosomeFactory(
				stdProgramGenerator);

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

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		final ProgramTreeChromosomeSpec programTreeChromosomeSpec = ProgramTreeChromosomeSpec.of(program);

		assertTrue(programTreeChromosomeFactory.canHandle(programTreeChromosomeSpec));
		assertFalse(programTreeChromosomeFactory.canHandle(IntChromosomeSpec.of(10, 0, 100)));
	}

	@Test
	public void generateSimple() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator stdProgramGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramTreeChromosomeFactory programTreeChromosomeFactory = new ProgramTreeChromosomeFactory(
				stdProgramGenerator);

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

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		final ProgramTreeChromosomeSpec programTreeChromosomeSpec = ProgramTreeChromosomeSpec.of(program);

		final TreeChromosome<Operation<?>> chromosome = programTreeChromosomeFactory.generate(programTreeChromosomeSpec);
		assertNotNull(chromosome);
	}
}