package net.bmahe.genetics4j.neat.mutation.chromosome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;
import net.bmahe.genetics4j.neat.spec.mutation.DeleteConnection;

public class NeatChromosomeDeleteConnectionTest {

	public static final Logger logger = LogManager.getLogger(NeatChromosomeDeleteConnectionTest.class);

	@Test
	public void constructor() {

		assertThrows(NullPointerException.class, () -> new NeatChromosomeDeleteConnection(null));
	}

	@Test
	public void canHandle() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();

		final var neatChromosomeDeleteConnection = new NeatChromosomeDeleteConnection(randomGenerator);

		assertThrows(NullPointerException.class, () -> neatChromosomeDeleteConnection.canHandle(null, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeDeleteConnection.canHandle(AddNode.of(0.2), null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeDeleteConnection.canHandle(null, IntChromosomeSpec.of(10, 0, 10)));

		assertFalse(neatChromosomeDeleteConnection.canHandle(AddNode.of(0.2), NeatChromosomeSpec.of(3, 2, 0, 10)));
		assertFalse(neatChromosomeDeleteConnection.canHandle(DeleteConnection.of(0.2), IntChromosomeSpec.of(10, 0, 10)));
		assertTrue(
				neatChromosomeDeleteConnection.canHandle(DeleteConnection.of(0.2), NeatChromosomeSpec.of(3, 2, 0, 10)));
	}

	@Test
	public void mutateConnectionExist() {

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, 1.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 3.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final RandomGenerator randomGenerator = mock(RandomGenerator.class);
		when(randomGenerator.nextInt(anyInt())).thenReturn(3);

		final var neatChromosomeDeleteConnection = new NeatChromosomeDeleteConnection(randomGenerator);

		assertThrows(NullPointerException.class, () -> neatChromosomeDeleteConnection.mutate(null, null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeDeleteConnection.mutate(DeleteConnection.of(0.2), null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeDeleteConnection.mutate(null, originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeDeleteConnection.mutate(AddNode.of(0.2), originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeDeleteConnection.mutate(DeleteConnection.of(0.2),
						new IntChromosome(3, 0, 10, new int[] { 0, 1, 2 })));

		final NeatChromosome mutatedChromosome = neatChromosomeDeleteConnection.mutate(DeleteConnection.of(0.5),
				originalNeatChromosome);
		logger.info("Mutated chromosome: {}", mutatedChromosome);

		assertNotNull(mutatedChromosome);

		final List<Connection> mutatedConnections = mutatedChromosome.getConnections();
		assertNotNull(mutatedConnections);
		assertEquals(originalConnections.size() - 1, mutatedConnections.size());
		assertEquals(originalConnections.get(0), mutatedConnections.get(0));
		assertEquals(originalConnections.get(1), mutatedConnections.get(1));
		assertEquals(originalConnections.get(2), mutatedConnections.get(2));
		assertEquals(originalConnections.get(4), mutatedConnections.get(3));
	}

	@Test
	public void mutateEmptyConnection() {

		final List<Connection> originalConnections = List.of();

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);

		final var neatChromosomeDeleteConnection = new NeatChromosomeDeleteConnection(mockRandomGenerator);

		assertThrows(NullPointerException.class, () -> neatChromosomeDeleteConnection.mutate(null, null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeDeleteConnection.mutate(DeleteConnection.of(0.2), null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeDeleteConnection.mutate(null, originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeDeleteConnection.mutate(AddNode.of(0.2), originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeDeleteConnection.mutate(DeleteConnection.of(0.2),
						new IntChromosome(3, 0, 10, new int[] { 0, 1, 2 })));

		final NeatChromosome mutatedChromosome = neatChromosomeDeleteConnection.mutate(DeleteConnection.of(0.5),
				originalNeatChromosome);
		logger.info("Mutated chromosome: {}", mutatedChromosome);

		assertNotNull(mutatedChromosome);

		final List<Connection> mutatedConnections = mutatedChromosome.getConnections();
		assertNotNull(mutatedConnections);
		assertEquals(0, mutatedConnections.size());
		verifyNoInteractions(mockRandomGenerator);
	}
}