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
import net.bmahe.genetics4j.neat.spec.mutation.NeatConnectionWeight;

public class NeatConnectionWeightPolicyHandlerTest {

	@Test
	public void constructorNullParam() {
		assertThrows(NullPointerException.class, () -> new NeatConnectionWeightPolicyHandler<>(null));
	}

	@Test
	public void canHandleRequireMutation() {
		final NeatConnectionWeightPolicyHandler<Integer> neatConnectionWeightMutationPolicyHandler = new NeatConnectionWeightPolicyHandler<>(
				RandomGenerator.getDefault());

		final var mutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);

		assertThrows(NullPointerException.class, () -> neatConnectionWeightMutationPolicyHandler.canHandle(null, null));
		assertThrows(NullPointerException.class,
				() -> neatConnectionWeightMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, null));
		assertThrows(NullPointerException.class,
				() -> neatConnectionWeightMutationPolicyHandler.canHandle(null, RandomMutation.of(0.2)));
		assertFalse(
				neatConnectionWeightMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, RandomMutation.of(0.2)));
		assertTrue(neatConnectionWeightMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver,
				NeatConnectionWeight.build()));
	}

	@Test
	public void createMutator() {
		final RandomGenerator randomGenerator = RandomGenerator.getDefault();

		final MutationPolicyHandlerResolver<Integer> mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		final EAExecutionContext<Integer> mockEaExecutionContext = mock(EAExecutionContext.class);
		final AbstractEAConfiguration<Integer> mockEaConfiguration = mock(AbstractEAConfiguration.class);

		final NeatConnectionWeightPolicyHandler<Integer> neatConnectionWeightPolicyHandler = new NeatConnectionWeightPolicyHandler<>(
				randomGenerator);

		final var mutator = neatConnectionWeightPolicyHandler.createMutator(mockEaExecutionContext,
				mockEaConfiguration,
				mockMutationPolicyHandlerResolver,
				NeatConnectionWeight.build());
		assertNotNull(mutator);
	}
}