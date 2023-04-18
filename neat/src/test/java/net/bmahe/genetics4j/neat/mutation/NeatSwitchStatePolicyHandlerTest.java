package net.bmahe.genetics4j.neat.mutation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.mutation.MutationPolicyHandlerResolver;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.neat.spec.mutation.SwitchStateMutation;

public class NeatSwitchStatePolicyHandlerTest {

	@Test
	public void constructorNullParam() {
		assertThrows(NullPointerException.class, () -> new NeatSwitchStatePolicyHandler<>(null));
	}

	@Test
	public void canHandleRequireMutation() {
		final NeatSwitchStatePolicyHandler<Integer> neatSwitchStateMutationPolicyHandler = new NeatSwitchStatePolicyHandler<>(
				RandomGenerator.getDefault());

		final var mutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);

		assertThrows(NullPointerException.class, () -> neatSwitchStateMutationPolicyHandler.canHandle(null, null));
		assertThrows(NullPointerException.class,
				() -> neatSwitchStateMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, null));
		assertThrows(NullPointerException.class,
				() -> neatSwitchStateMutationPolicyHandler.canHandle(null, RandomMutation.of(0.2)));
		assertFalse(
				neatSwitchStateMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, RandomMutation.of(0.2)));
		assertTrue(
				neatSwitchStateMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, SwitchStateMutation.of(0.2)));
	}

	@Test
	public void createMutator() {
		final RandomGenerator randomGenerator = RandomGenerator.getDefault();

		final MutationPolicyHandlerResolver<Integer> mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		final EAExecutionContext<Integer> mockEaExecutionContext = mock(EAExecutionContext.class);
		final AbstractEAConfiguration<Integer> mockEaConfiguration = mock(AbstractEAConfiguration.class);

		final NeatSwitchStatePolicyHandler<Integer> neatSwitchStatePolicyHandler = new NeatSwitchStatePolicyHandler<>(
				randomGenerator);

		final var mutator = neatSwitchStatePolicyHandler.createMutator(mockEaExecutionContext,
				mockEaConfiguration,
				mockMutationPolicyHandlerResolver,
				SwitchStateMutation.of(0.2));
		assertNotNull(mutator);
	}
}