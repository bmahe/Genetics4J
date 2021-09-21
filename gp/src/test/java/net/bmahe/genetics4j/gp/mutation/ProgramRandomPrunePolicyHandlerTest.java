package net.bmahe.genetics4j.gp.mutation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Random;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.mutation.Mutator;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;

public class ProgramRandomPrunePolicyHandlerTest {

	@Test
	public void noCtorArguments() {
		assertThrows(NullPointerException.class, () -> new ProgramRandomPrunePolicyHandler(null, null));
	}

	@Test
	public void noRandomCtorArguments() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		assertThrows(NullPointerException.class, () -> new ProgramRandomPrunePolicyHandler(null, programHelper));
	}

	@Test
	public void noProgramGeneratorCtorArguments() {
		assertThrows(NullPointerException.class, () -> new ProgramRandomPrunePolicyHandler(new Random(), null));
	}

	@Test
	public void canHandleNoMutationPolicyHandlerResolver() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		final ProgramRandomPrune mutationPolicy = ProgramRandomPrune.of(0.12);

		final ProgramRandomPrunePolicyHandler programRandomPrunePolicyHandler = new ProgramRandomPrunePolicyHandler(
				random, programHelper);

		assertThrows(NullPointerException.class, () -> programRandomPrunePolicyHandler.canHandle(null, mutationPolicy));
	}

	@Test
	public void canHandleNoMutationPolicy() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		final ProgramRandomPrunePolicyHandler programRandomPrunePolicyHandler = new ProgramRandomPrunePolicyHandler(
				random, programHelper);

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);
		assertThrows(NullPointerException.class,
				() -> programRandomPrunePolicyHandler.canHandle(mockMutationPolicyHandlerResolver, null));
	}

	@Test
	public void canHandle() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		final ProgramRandomPrune mutationPolicy = ProgramRandomPrune.of(0.12);
		final ProgramRandomPrunePolicyHandler programRandomPrunePolicyHandler = new ProgramRandomPrunePolicyHandler(
				random, programHelper);

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);

		assertTrue(programRandomPrunePolicyHandler.canHandle(mockMutationPolicyHandlerResolver, mutationPolicy));
		assertFalse(programRandomPrunePolicyHandler.canHandle(mockMutationPolicyHandlerResolver,
				SwapMutation.of(0.2, 10, true)));
	}

	@Test
	public void createMutator() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		final ProgramRandomPrune mutationPolicy = ProgramRandomPrune.of(0.12);
		final ProgramRandomPrunePolicyHandler programRandomPrunePolicyHandler = new ProgramRandomPrunePolicyHandler(
				random, programHelper);

		final MutationPolicyHandlerResolver mockMutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);
		final EAExecutionContext mockEaExecutionContext = mock(EAExecutionContext.class);
		final EAConfiguration mockEaConfiguration = mock(EAConfiguration.class);

		final Mutator mutator = programRandomPrunePolicyHandler.createMutator(mockEaExecutionContext,
				mockEaConfiguration,
				mockMutationPolicyHandlerResolver,
				mutationPolicy);
		assertNotNull(mutator);
		assertTrue(mutator instanceof ProgramRandomPruneMutator);
	}
}