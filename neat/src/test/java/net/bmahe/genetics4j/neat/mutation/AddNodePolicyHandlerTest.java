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
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;

public class AddNodePolicyHandlerTest {

	@Test
	public void constructorNullParam() {
		assertThrows(NullPointerException.class, () -> new AddNodePolicyHandler<>(null));
	}

	@Test
	public void canHandleRequireMutation() {
		final AddNodePolicyHandler<Integer> addNodeMutationPolicyHandler = new AddNodePolicyHandler<>(
				RandomGenerator.getDefault());

		final var mutationPolicyHandlerResolver = mock(MutationPolicyHandlerResolver.class);

		assertThrows(NullPointerException.class, () -> addNodeMutationPolicyHandler.canHandle(null, null));
		assertThrows(NullPointerException.class,
				() -> addNodeMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, null));
		assertThrows(NullPointerException.class,
				() -> addNodeMutationPolicyHandler.canHandle(null, RandomMutation.of(0.2)));
		assertFalse(addNodeMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, RandomMutation.of(0.2)));
		assertTrue(addNodeMutationPolicyHandler.canHandle(mutationPolicyHandlerResolver, AddNode.of(0.2)));
	}

	@Test
	public void createMutator() {
		final RandomGenerator randomGenerator = RandomGenerator.getDefault();

		final MutationPolicyHandlerResolver<Integer> mockMutationPolicyHandlerResolver = mock(
				MutationPolicyHandlerResolver.class);
		final EAExecutionContext<Integer> mockEaExecutionContext = mock(EAExecutionContext.class);
		final AbstractEAConfiguration<Integer> mockEaConfiguration = mock(AbstractEAConfiguration.class);

		final AddNodePolicyHandler<Integer> addNodePolicyHandler = new AddNodePolicyHandler<>(randomGenerator);

		final var mutator = addNodePolicyHandler.createMutator(mockEaExecutionContext,
				mockEaConfiguration,
				mockMutationPolicyHandlerResolver,
				AddNode.of(0.2));
		assertNotNull(mutator);
	}
}