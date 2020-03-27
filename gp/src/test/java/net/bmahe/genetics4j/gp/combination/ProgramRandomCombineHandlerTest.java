package net.bmahe.genetics4j.gp.combination;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.ImmutableProgram;
import net.bmahe.genetics4j.gp.Program;
import net.bmahe.genetics4j.gp.ProgramGenerator;
import net.bmahe.genetics4j.gp.StdProgramGenerator;
import net.bmahe.genetics4j.gp.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.chromosomes.factory.ProgramTreeChromosomeFactory;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomMutatePolicyHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomPrunePolicyHandler;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.combination.ProgramRandomCombine;

public class ProgramRandomCombineHandlerTest {

	@Test(expected = NullPointerException.class)
	public void noRandomParameter() {
		new ProgramRandomCombineHandler(null);
	}

	@Test
	public void canHandle() {
		final Random random = new Random();
		final ProgramGenerator programGenerator = new StdProgramGenerator(random);

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

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(500);
		geneticSystemDescriptorBuilder
				.addMutationPolicyHandlers(new ProgramRandomPrunePolicyHandler(random, programGenerator));
		geneticSystemDescriptorBuilder
				.addMutationPolicyHandlers(new ProgramRandomMutatePolicyHandler(random, programGenerator));

		geneticSystemDescriptorBuilder.addChromosomeCombinatorHandlers(new ProgramRandomCombineHandler(random));

		net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder chromosomeFactoryProviderBuilder = ImmutableChromosomeFactoryProvider
				.builder();
		chromosomeFactoryProviderBuilder.random(random);
		chromosomeFactoryProviderBuilder.addChromosomeFactories(new ProgramTreeChromosomeFactory(programGenerator));
		geneticSystemDescriptorBuilder.chromosomeFactoryProvider(chromosomeFactoryProviderBuilder.build());
		final GeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final ChromosomeCombinatorResolver chromosomeCombinatorResolver = new ChromosomeCombinatorResolver(
				geneticSystemDescriptor);

		final ProgramRandomCombine programRandomCombine = ProgramRandomCombine.build();
		final ProgramTreeChromosomeSpec programTreeChromosomeSpec = ProgramTreeChromosomeSpec.of(program);

		final ProgramRandomCombineHandler programRandomCombineHandler = new ProgramRandomCombineHandler(random);

		assertTrue(programRandomCombineHandler
				.canHandle(chromosomeCombinatorResolver, programRandomCombine, programTreeChromosomeSpec));
		assertFalse(programRandomCombineHandler
				.canHandle(chromosomeCombinatorResolver, programRandomCombine, IntChromosomeSpec.of(10, 0, 100)));
		assertFalse(programRandomCombineHandler
				.canHandle(chromosomeCombinatorResolver, MultiPointCrossover.of(10), programTreeChromosomeSpec));

	}
}