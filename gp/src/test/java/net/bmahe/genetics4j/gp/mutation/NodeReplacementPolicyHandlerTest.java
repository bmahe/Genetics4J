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
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.spec.mutation.NodeReplacement;

public class NodeReplacementPolicyHandlerTest {

	@Test
	public void noCtorArguments() {
		assertThrows(NullPointerException.class, () -> new NodeReplacementPolicyHandler<>(null, null));
	}

	@Test
	public void noRandomCtorArguments() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		assertThrows(NullPointerException.class, () -> new NodeReplacementPolicyHandler<>(null, programHelper));
	}

	@Test
	public void noProgramGeneratorCtorArguments() {
		assertThrows(NullPointerException.class, () -> new NodeReplacementPolicyHandler<>(new Random(), null));
	}

	@Test
	public void canHandleNoMutationPolicyHandlerResolver() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		final NodeReplacement mutationPolicy = NodeReplacement.of(0.12);

		final NodeReplacementPolicyHandler<Integer> nodeReplacementPolicyHandler = new NodeReplacementPolicyHandler<>(
				random,
				programHelper);

		assertThrows(NullPointerException.class, () -> nodeReplacementPolicyHandler.canHandle(null, mutationPolicy));
	}

	@Test
	public void canHandleNoMutationPolicy() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		final var nodeReplacementPolicyHandler = new NodeReplacementPolicyHandler<Integer>(random, programHelper);

		final MutationPolicyHandlerResolver<Integer> mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		assertThrows(NullPointerException.class,
				() -> nodeReplacementPolicyHandler.canHandle(mockMutationPolicyHandlerResolver, null));
	}

	@Test
	public void canHandle() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		final NodeReplacement mutationPolicy = NodeReplacement.of(0.12);
		final var nodeReplacementPolicyHandler = new NodeReplacementPolicyHandler<Integer>(random, programHelper);

		final MutationPolicyHandlerResolver<Integer> mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);

		assertTrue(nodeReplacementPolicyHandler.canHandle(mockMutationPolicyHandlerResolver, mutationPolicy));
		assertFalse(
				nodeReplacementPolicyHandler.canHandle(mockMutationPolicyHandlerResolver, SwapMutation.of(0.2, 10, true)));
	}

	@Test
	public void createMutator() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);

		final NodeReplacement mutationPolicy = NodeReplacement.of(0.12);
		final var nodeReplacementPolicyHandler = new NodeReplacementPolicyHandler<Integer>(random, programHelper);

		final MutationPolicyHandlerResolver<Integer> mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		final EAExecutionContext<Integer> mockEaExecutionContext = mock(EAExecutionContext.class);
		final AbstractEAConfiguration<Integer> mockEaConfiguration = mock(AbstractEAConfiguration.class);

		final Mutator mutator = nodeReplacementPolicyHandler.createMutator(mockEaExecutionContext,
				mockEaConfiguration,
				mockMutationPolicyHandlerResolver,
				mutationPolicy);
		assertNotNull(mutator);
		assertTrue(mutator instanceof NodeReplacementMutator);
	}
}