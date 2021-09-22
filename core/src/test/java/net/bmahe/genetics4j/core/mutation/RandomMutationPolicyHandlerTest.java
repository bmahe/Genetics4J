package net.bmahe.genetics4j.core.mutation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.spec.mutation.ImmutableRandomMutation;

public class RandomMutationPolicyHandlerTest {

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new RandomMutationPolicyHandler(null));
	}

	@Test
	public void canHandleRequireMutation() {
		final RandomMutationPolicyHandler randomMutationPolicyHandler = new RandomMutationPolicyHandler(new Random());

		assertThrows(NullPointerException.class, () -> randomMutationPolicyHandler.canHandle(null, null));
	}

	@Test
	public void canHandle() {
		final RandomMutationPolicyHandler randomMutationPolicyHandler = new RandomMutationPolicyHandler(new Random());

		assertTrue(randomMutationPolicyHandler.canHandle(null, ImmutableRandomMutation.of(0.1)));
	}
}