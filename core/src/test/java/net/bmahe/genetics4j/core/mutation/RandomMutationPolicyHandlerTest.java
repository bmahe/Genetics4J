package net.bmahe.genetics4j.core.mutation;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.spec.mutation.ImmutableRandomMutation;

public class RandomMutationPolicyHandlerTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new RandomMutationPolicyHandler(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireMutation() {
		final RandomMutationPolicyHandler randomMutationPolicyHandler = new RandomMutationPolicyHandler(new Random());

		randomMutationPolicyHandler.canHandle(null, null);
	}

	@Test
	public void canHandle() {
		final RandomMutationPolicyHandler randomMutationPolicyHandler = new RandomMutationPolicyHandler(new Random());

		assertTrue(randomMutationPolicyHandler.canHandle(null, ImmutableRandomMutation.of(0.1)));
	}
}