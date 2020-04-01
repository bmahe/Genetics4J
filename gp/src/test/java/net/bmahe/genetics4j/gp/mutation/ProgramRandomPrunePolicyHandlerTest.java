package net.bmahe.genetics4j.gp.mutation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.gp.ProgramGenerator;
import net.bmahe.genetics4j.gp.StdProgramGenerator;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;

public class ProgramRandomPrunePolicyHandlerTest {

	@Test(expected = NullPointerException.class)
	public void noCtorArguments() {
		new ProgramRandomPrunePolicyHandler(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void noRandomCtorArguments() {
		new ProgramRandomPrunePolicyHandler(null, new StdProgramGenerator(new Random()));
	}

	@Test(expected = NullPointerException.class)
	public void noProgramGeneratorCtorArguments() {
		new ProgramRandomPrunePolicyHandler(new Random(), null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleNoMutationPolicyHandlerResolver() {
		final Random random = new Random();
		final ProgramGenerator programGenerator = new StdProgramGenerator(random);

		final ProgramRandomPrune mutationPolicy = ProgramRandomPrune.of(0.12);

		final ProgramRandomPrunePolicyHandler programRandomPrunePolicyHandler = new ProgramRandomPrunePolicyHandler(
				random, programGenerator);

		programRandomPrunePolicyHandler.canHandle(null, mutationPolicy);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleNoMutationPolicy() {
		final Random random = new Random();
		final ProgramGenerator programGenerator = new StdProgramGenerator(random);

		final ProgramRandomPrunePolicyHandler programRandomPrunePolicyHandler = new ProgramRandomPrunePolicyHandler(
				random, programGenerator);

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);
		programRandomPrunePolicyHandler.canHandle(mockMutationPolicyHandlerResolver, null);
	}

	@Test
	public void canHandle() {
		final Random random = new Random();
		final ProgramGenerator programGenerator = new StdProgramGenerator(random);

		final ProgramRandomPrune mutationPolicy = ProgramRandomPrune.of(0.12);
		final ProgramRandomPrunePolicyHandler programRandomPrunePolicyHandler = new ProgramRandomPrunePolicyHandler(
				random, programGenerator);

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);

		assertTrue(programRandomPrunePolicyHandler.canHandle(mockMutationPolicyHandlerResolver, mutationPolicy));
		assertFalse(programRandomPrunePolicyHandler.canHandle(mockMutationPolicyHandlerResolver,
				SwapMutation.of(0.2, 10, true)));
	}

	@Test
	public void createMutator() {
		final Random random = new Random();
		final ProgramGenerator programGenerator = new StdProgramGenerator(random);

		final ProgramRandomPrune mutationPolicy = ProgramRandomPrune.of(0.12);
		final ProgramRandomPrunePolicyHandler programRandomPrunePolicyHandler = new ProgramRandomPrunePolicyHandler(
				random, programGenerator);

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);
		final GeneticSystemDescriptor mockGeneticSystemDescriptor = mock(GeneticSystemDescriptor.class);
		final GenotypeSpec mockGenotypeSpec = mock(GenotypeSpec.class);

		final Mutator mutator = programRandomPrunePolicyHandler.createMutator(mockGeneticSystemDescriptor,
				mockGenotypeSpec,
				mockMutationPolicyHandlerResolver,
				mutationPolicy);
		assertNotNull(mutator);
		assertTrue(mutator instanceof ProgramRandomPruneMutator);
	}
}