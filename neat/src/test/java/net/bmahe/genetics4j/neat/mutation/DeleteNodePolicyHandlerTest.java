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
import net.bmahe.genetics4j.neat.spec.mutation.DeleteNode;

public class DeleteNodePolicyHandlerTest {

	@Test
	public void constructorNullParam() {
		assertThrows(NullPointerException.class, () -> new DeleteNodePolicyHandler<>(null));
	}

	@Test
	public void canHandleRequireMutation() {
		final DeleteNodePolicyHandler<Integer> deleteNodeMutationPolicyHandler = new DeleteNodePolicyHandler<>(
				RandomGenerator.getDefault());

		final var mutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);

		assertThrows(NullPointerException.class, () -> deleteNodeMutationPolicyHandler.canHandle(null, null));
		assertThrows(NullPointerException.class,
				() -> deleteNodeMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, null));
		assertThrows(NullPointerException.class,
				() -> deleteNodeMutationPolicyHandler.canHandle(null, RandomMutation.of(0.2)));
		assertFalse(deleteNodeMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, RandomMutation.of(0.2)));
		assertTrue(deleteNodeMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, DeleteNode.of(0.2)));
	}

	@Test
	public void createMutator() {
		final RandomGenerator randomGenerator = RandomGenerator.getDefault();

		final MutationPolicyHandlerResolver<Integer> mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		final EAExecutionContext<Integer> mockEaExecutionContext = mock(EAExecutionContext.class);
		final AbstractEAConfiguration<Integer> mockEaConfiguration = mock(AbstractEAConfiguration.class);

		final DeleteNodePolicyHandler<Integer> deleteNodePolicyHandler = new DeleteNodePolicyHandler<>(randomGenerator);

		final var mutator = deleteNodePolicyHandler.createMutator(mockEaExecutionContext,
				mockEaConfiguration,
				mockMutationPolicyHandlerResolver,
				DeleteNode.of(0.2));
		assertNotNull(mutator);
	}
}