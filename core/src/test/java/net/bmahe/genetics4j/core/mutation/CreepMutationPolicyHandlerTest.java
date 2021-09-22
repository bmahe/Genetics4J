package net.bmahe.genetics4j.core.mutation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.spec.mutation.ImmutableCreepMutation;

public class CreepMutationPolicyHandlerTest {

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new CreepMutationPolicyHandler(null));
	}

	@Test
	public void canHandleRequireMutation() {
		final CreepMutationPolicyHandler randomMutationPolicyHandler = new CreepMutationPolicyHandler(new Random());

		assertThrows(NullPointerException.class, () -> randomMutationPolicyHandler.canHandle(null, null));
	}

	@Test
	public void canHandle() {
		final CreepMutationPolicyHandler randomMutationPolicyHandler = new CreepMutationPolicyHandler(new Random());

		assertTrue(randomMutationPolicyHandler.canHandle(null, ImmutableCreepMutation.ofNormal(0.1, 0.0, 2.0)));
	}
}