package net.bmahe.genetics4j.gp.combination;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinatorResolver;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptors;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.chromosomes.factory.ProgramTreeChromosomeFactory;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomMutatePolicyHandler;
import net.bmahe.genetics4j.gp.mutation.ProgramRandomPrunePolicyHandler;
import net.bmahe.genetics4j.gp.program.ImmutableProgram;
import net.bmahe.genetics4j.gp.program.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.program.StdProgramGenerator;
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
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

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

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<?> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(500);
		geneticSystemDescriptorBuilder
				.addDefaultMutationPolicyHandlers(new ProgramRandomPrunePolicyHandler(random, programHelper));
		geneticSystemDescriptorBuilder
				.addDefaultMutationPolicyHandlers(new ProgramRandomMutatePolicyHandler(random, programGenerator));

		geneticSystemDescriptorBuilder.addDefaultChromosomeCombinatorHandlers(new ProgramRandomCombineHandler(random));

		net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder chromosomeFactoryProviderBuilder = ImmutableChromosomeFactoryProvider
				.builder();
		chromosomeFactoryProviderBuilder.random(random);
		chromosomeFactoryProviderBuilder.addDefaultChromosomeFactories(new ProgramTreeChromosomeFactory(programGenerator));
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
		assertFalse(programRandomCombineHandler
				.canHandle(chromosomeCombinatorResolver, MultiPointCrossover.of(10), IntChromosomeSpec.of(10, 0, 100)));
	}

	@Test
	public void resolve() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

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

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<?> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(500);
		geneticSystemDescriptorBuilder
				.addDefaultMutationPolicyHandlers(new ProgramRandomPrunePolicyHandler(random, programHelper));
		geneticSystemDescriptorBuilder
				.addDefaultMutationPolicyHandlers(new ProgramRandomMutatePolicyHandler(random, programGenerator));

		geneticSystemDescriptorBuilder.addDefaultChromosomeCombinatorHandlers(new ProgramRandomCombineHandler(random));

		net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder chromosomeFactoryProviderBuilder = ImmutableChromosomeFactoryProvider
				.builder();
		chromosomeFactoryProviderBuilder.random(random);
		chromosomeFactoryProviderBuilder.addDefaultChromosomeFactories(new ProgramTreeChromosomeFactory(programGenerator));
		geneticSystemDescriptorBuilder.chromosomeFactoryProvider(chromosomeFactoryProviderBuilder.build());
		final GeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final ChromosomeCombinatorResolver chromosomeCombinatorResolver = new ChromosomeCombinatorResolver(
				geneticSystemDescriptor);

		final ProgramRandomCombine programRandomCombine = ProgramRandomCombine.build();
		final ProgramTreeChromosomeSpec programTreeChromosomeSpec = ProgramTreeChromosomeSpec.of(program);

		final ProgramRandomCombineHandler programRandomCombineHandler = new ProgramRandomCombineHandler(random);

		final ChromosomeCombinator chromosomeCombinator = programRandomCombineHandler
				.resolve(chromosomeCombinatorResolver, programRandomCombine, programTreeChromosomeSpec);
		assertNotNull(chromosomeCombinator);
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoResolver() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD, Functions.SIN, Functions.EXP);
		programBuilder.addTerminal(Terminals.InputDouble(random), Terminals.PI);

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		final ProgramRandomCombine programRandomCombine = ProgramRandomCombine.build();
		final ProgramTreeChromosomeSpec programTreeChromosomeSpec = ProgramTreeChromosomeSpec.of(program);

		final ProgramRandomCombineHandler programRandomCombineHandler = new ProgramRandomCombineHandler(random);

		final ChromosomeCombinator chromosomeCombinator = programRandomCombineHandler
				.resolve(null, programRandomCombine, programTreeChromosomeSpec);
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoCombinationPolicy() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD, Functions.SIN, Functions.EXP);
		programBuilder.addTerminal(Terminals.InputDouble(random), Terminals.PI);

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<?> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(500);
		geneticSystemDescriptorBuilder
				.addDefaultMutationPolicyHandlers(new ProgramRandomPrunePolicyHandler(random, programHelper));
		geneticSystemDescriptorBuilder
				.addDefaultMutationPolicyHandlers(new ProgramRandomMutatePolicyHandler(random, programGenerator));

		geneticSystemDescriptorBuilder.addDefaultChromosomeCombinatorHandlers(new ProgramRandomCombineHandler(random));

		net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder chromosomeFactoryProviderBuilder = ImmutableChromosomeFactoryProvider
				.builder();
		chromosomeFactoryProviderBuilder.random(random);
		chromosomeFactoryProviderBuilder.addDefaultChromosomeFactories(new ProgramTreeChromosomeFactory(programGenerator));
		geneticSystemDescriptorBuilder.chromosomeFactoryProvider(chromosomeFactoryProviderBuilder.build());
		final GeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final ChromosomeCombinatorResolver chromosomeCombinatorResolver = new ChromosomeCombinatorResolver(
				geneticSystemDescriptor);

		final ProgramTreeChromosomeSpec programTreeChromosomeSpec = ProgramTreeChromosomeSpec.of(program);

		final ProgramRandomCombineHandler programRandomCombineHandler = new ProgramRandomCombineHandler(random);

		final ChromosomeCombinator chromosomeCombinator = programRandomCombineHandler
				.resolve(chromosomeCombinatorResolver, null, programTreeChromosomeSpec);
	}

	@Test(expected = NullPointerException.class)
	public void resolveNoChromosomeSpec() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder<?> geneticSystemDescriptorBuilder = GeneticSystemDescriptors
				.standard();
		geneticSystemDescriptorBuilder.populationSize(500);
		geneticSystemDescriptorBuilder
				.addDefaultMutationPolicyHandlers(new ProgramRandomPrunePolicyHandler(random, programHelper));
		geneticSystemDescriptorBuilder
				.addDefaultMutationPolicyHandlers(new ProgramRandomMutatePolicyHandler(random, programGenerator));

		geneticSystemDescriptorBuilder.addDefaultChromosomeCombinatorHandlers(new ProgramRandomCombineHandler(random));

		net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder chromosomeFactoryProviderBuilder = ImmutableChromosomeFactoryProvider
				.builder();
		chromosomeFactoryProviderBuilder.random(random);
		chromosomeFactoryProviderBuilder.addDefaultChromosomeFactories(new ProgramTreeChromosomeFactory(programGenerator));
		geneticSystemDescriptorBuilder.chromosomeFactoryProvider(chromosomeFactoryProviderBuilder.build());
		final GeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final ChromosomeCombinatorResolver chromosomeCombinatorResolver = new ChromosomeCombinatorResolver(
				geneticSystemDescriptor);

		final ProgramRandomCombine programRandomCombine = ProgramRandomCombine.build();

		final ProgramRandomCombineHandler programRandomCombineHandler = new ProgramRandomCombineHandler(random);

		final ChromosomeCombinator chromosomeCombinator = programRandomCombineHandler
				.resolve(chromosomeCombinatorResolver, programRandomCombine, null);
	}
}