package net.bmahe.genetics4j.neat.mutation.chromosome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public class NeatChromosomeRandomMutationHandlerTest {

	@Test
	public void constructor() {
		assertThrows(NullPointerException.class, () -> new NeatChromosomeRandomMutationHandler(null));
	}

	@Test
	public void mutateConnection() {

		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		when(mockRandomGenerator.nextFloat(anyFloat())).thenReturn(0.0f)
				.thenReturn(20.0f);

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, -8.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 9.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));

		final NeatChromosome originalChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final NeatChromosomeRandomMutationHandler neatChromosomeRandomMutationHandler = new NeatChromosomeRandomMutationHandler(
				mockRandomGenerator);

		// min value
		final Connection connectionA = originalConnections.get(1);
		final List<Connection> mutatedConnectionA = neatChromosomeRandomMutationHandler
				.mutateConnection(RandomMutation.of(0.2), originalChromosome, connectionA, 1);
		assertNotNull(mutatedConnectionA);
		assertEquals(1, mutatedConnectionA.size());
		assertEquals(originalChromosome.getMinWeightValue(),
				mutatedConnectionA.get(0)
						.weight(),
				0.001);

		// max value
		final Connection connectionB = originalConnections.get(2);
		final List<Connection> mutatedConnectionB = neatChromosomeRandomMutationHandler
				.mutateConnection(RandomMutation.of(0.2), originalChromosome, connectionB, 2);
		assertNotNull(mutatedConnectionB);
		assertEquals(1, mutatedConnectionB.size());
		assertEquals(originalChromosome.getMaxWeightValue(),
				mutatedConnectionB.get(0)
						.weight(),
				0.001);
	}
}