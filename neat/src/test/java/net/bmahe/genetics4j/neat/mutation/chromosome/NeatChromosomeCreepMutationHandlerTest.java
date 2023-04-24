package net.bmahe.genetics4j.neat.mutation.chromosome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public class NeatChromosomeCreepMutationHandlerTest {

	@Test
	public void constructor() {
		assertThrows(NullPointerException.class, () -> new NeatChromosomeCreepMutationHandler(null));
	}

	@Test
	public void mutateConnection() {

		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		when(mockRandomGenerator.nextGaussian()).thenReturn(0.0);

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, -8.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 9.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));

		final NeatChromosome originalChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final double negativeMean = -5;

		final NeatChromosomeCreepMutationHandler neatChromosomeCreepMutationHandler = new NeatChromosomeCreepMutationHandler(
				mockRandomGenerator);

		// Go below min value
		final Connection connectionA = originalConnections.get(1);
		final List<Connection> mutatedConnectionA = neatChromosomeCreepMutationHandler
				.mutateConnection(CreepMutation.ofNormal(0, negativeMean, 0), originalChromosome, connectionA, 1);
		assertNotNull(mutatedConnectionA);
		assertEquals(1, mutatedConnectionA.size());
		assertEquals(-10,
				mutatedConnectionA.get(0)
						.weight(),
				0.001);

		// Stay within min value
		final Connection connectionB = originalConnections.get(2);
		final List<Connection> mutatedConnectionB = neatChromosomeCreepMutationHandler
				.mutateConnection(CreepMutation.ofNormal(0, negativeMean, 0), originalChromosome, connectionB, 2);
		assertNotNull(mutatedConnectionB);
		assertEquals(1, mutatedConnectionB.size());
		assertEquals(connectionB.weight() + negativeMean,
				mutatedConnectionB.get(0)
						.weight(),
				0.001);

		final double positiveMean = 5.25;
		// Go above max value
		final Connection connectionC = originalConnections.get(3);
		final List<Connection> mutatedConnectionC = neatChromosomeCreepMutationHandler
				.mutateConnection(CreepMutation.ofNormal(0, positiveMean, 0), originalChromosome, connectionC, 3);
		assertNotNull(mutatedConnectionC);
		assertEquals(1, mutatedConnectionC.size());
		assertEquals(10,
				mutatedConnectionC.get(0)
						.weight(),
				0.001);

		// Stay within max value
		final Connection connectionD = originalConnections.get(4);
		final List<Connection> mutatedConnectionD = neatChromosomeCreepMutationHandler
				.mutateConnection(CreepMutation.ofNormal(0, positiveMean, 0), originalChromosome, connectionD, 4);
		assertNotNull(mutatedConnectionD);
		assertEquals(1, mutatedConnectionD.size());
		assertEquals(connectionD.weight() + positiveMean,
				mutatedConnectionD.get(0)
						.weight(),
				0.001);
	}
}