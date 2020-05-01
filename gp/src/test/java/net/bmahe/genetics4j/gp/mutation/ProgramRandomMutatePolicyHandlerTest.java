package net.bmahe.genetics4j.gp.mutation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.program.StdProgramGenerator;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomMutate;

public class ProgramRandomMutatePolicyHandlerTest {

	@Test(expected = NullPointerException.class)
	public void noCtorArguments() {
		new ProgramRandomMutatePolicyHandler(null, null);
	}

	@Test(expected = NullPointerException.class)
	public void noRandomCtorArguments() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);
		new ProgramRandomMutatePolicyHandler(null, programGenerator);
	}

	@Test(expected = NullPointerException.class)
	public void noProgramGeneratorCtorArguments() {
		new ProgramRandomMutatePolicyHandler(new Random(), null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleNoMutationPolicyHandlerResolver() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramRandomMutate mutationPolicy = ProgramRandomMutate.of(0.12);

		final ProgramRandomMutatePolicyHandler programRandomMutatePolicyHandler = new ProgramRandomMutatePolicyHandler(
				random, programGenerator);

		programRandomMutatePolicyHandler.canHandle(null, mutationPolicy);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleNoMutationPolicy() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramRandomMutatePolicyHandler programRandomMutatePolicyHandler = new ProgramRandomMutatePolicyHandler(
				random, programGenerator);

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		programRandomMutatePolicyHandler.canHandle(mockMutationPolicyHandlerResolver, null);
	}

	@Test
	public void canHandle() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramRandomMutate mutationPolicy = ProgramRandomMutate.of(0.12);
		final ProgramRandomMutatePolicyHandler programRandomMutatePolicyHandler = new ProgramRandomMutatePolicyHandler(
				random, programGenerator);

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);

		assertTrue(programRandomMutatePolicyHandler.canHandle(mockMutationPolicyHandlerResolver, mutationPolicy));
		assertFalse(programRandomMutatePolicyHandler.canHandle(mockMutationPolicyHandlerResolver,
				SwapMutation.of(0.2, 10, true)));
	}

	@Test
	public void createMutator() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final StdProgramGenerator programGenerator = new StdProgramGenerator(programHelper, random);

		final ProgramRandomMutate mutationPolicy = ProgramRandomMutate.of(0.12);
		final ProgramRandomMutatePolicyHandler programRandomMutatePolicyHandler = new ProgramRandomMutatePolicyHandler(
				random, programGenerator);

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		final EAExecutionContext mockEaExecutionContext = mock(EAExecutionContext.class);
		final EAConfiguration mockEaConfiguration = mock(EAConfiguration.class);

		final Mutator mutator = programRandomMutatePolicyHandler.createMutator(mockEaExecutionContext,
				mockEaConfiguration,
				mockMutationPolicyHandlerResolver,
				mutationPolicy);
		assertNotNull(mutator);
		assertTrue(mutator instanceof ProgramRandomMutateMutator);
	}
}