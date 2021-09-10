package net.bmahe.genetics4j.core.mutation;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.spec.mutation.ImmutableCreepMutation;

public class CreepMutationPolicyHandlerTest {

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new CreepMutationPolicyHandler(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireMutation() {
		final CreepMutationPolicyHandler randomMutationPolicyHandler = new CreepMutationPolicyHandler(new Random());

		randomMutationPolicyHandler.canHandle(null, null);
	}

	@Test
	public void canHandle() {
		final CreepMutationPolicyHandler randomMutationPolicyHandler = new CreepMutationPolicyHandler(new Random());

		assertTrue(randomMutationPolicyHandler.canHandle(null, ImmutableCreepMutation.ofNormal(0.1, 0.0, 2.0)));
	}
}