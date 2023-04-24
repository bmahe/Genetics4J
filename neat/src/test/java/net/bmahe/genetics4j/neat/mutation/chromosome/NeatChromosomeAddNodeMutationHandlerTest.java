package net.bmahe.genetics4j.neat.mutation.chromosome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.InnovationManager;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;

public class NeatChromosomeAddNodeMutationHandlerTest {

	@Test
	public void constructor() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final InnovationManager innovationManager = new InnovationManager();

		assertThrows(NullPointerException.class, () -> new NeatChromosomeAddNodeMutationHandler(null, null));
		assertThrows(NullPointerException.class, () -> new NeatChromosomeAddNodeMutationHandler(randomGenerator, null));
		assertThrows(NullPointerException.class, () -> new NeatChromosomeAddNodeMutationHandler(null, innovationManager));
	}

	@Test
	public void mutateConnection() {

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, 1.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 3.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));
		final int maxOriginalInnovation = originalConnections.stream()
				.mapToInt(Connection::innovation)
				.max()
				.getAsInt();

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final InnovationManager innovationManager = new InnovationManager(maxOriginalInnovation + 1);

		final var neatChromosomeAddNodeMutationHandler = new NeatChromosomeAddNodeMutationHandler(randomGenerator,
				innovationManager);

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);
		final int toMutateIndex = 2;
		final Connection toMutateConnection = originalConnections.get(toMutateIndex);

		final List<Connection> mutatedConnections = neatChromosomeAddNodeMutationHandler
				.mutateConnection(AddNode.of(0.2), originalNeatChromosome, toMutateConnection, toMutateIndex);
		assertNotNull(mutatedConnections);
		assertEquals(3, mutatedConnections.size());
		assertEquals(toMutateConnection.fromNodeIndex(),
				mutatedConnections.get(0)
						.fromNodeIndex());
		assertEquals(toMutateConnection.toNodeIndex(),
				mutatedConnections.get(0)
						.toNodeIndex());
		assertEquals(toMutateConnection.innovation(),
				mutatedConnections.get(0)
						.innovation());
		assertEquals(toMutateConnection.weight(),
				mutatedConnections.get(0)
						.weight(),
				0.0001f);
		assertEquals(false,
				mutatedConnections.get(0)
						.isEnabled());

		final int expectedNewNodeValue = originalConnections.stream()
				.mapToInt(connection -> Math.max(connection.fromNodeIndex(), connection.toNodeIndex()))
				.max()
				.getAsInt() + 1;

		assertTrue(mutatedConnections.stream()
				.filter(connection -> connection.isEnabled())
				.filter(connection -> connection.fromNodeIndex() == toMutateConnection.fromNodeIndex())
				.filter(connection -> expectedNewNodeValue == connection.toNodeIndex())
				.count() == 1);
		assertTrue(mutatedConnections.stream()
				.filter(connection -> connection.isEnabled())
				.filter(connection -> connection.toNodeIndex() == toMutateConnection.toNodeIndex())
				.filter(connection -> expectedNewNodeValue == connection.fromNodeIndex())
				.count() == 1);

		assertEquals(maxOriginalInnovation + 1,
				mutatedConnections.get(1)
						.innovation());

		assertEquals(maxOriginalInnovation + 2,
				mutatedConnections.get(2)
						.innovation());

		assertEquals(toMutateConnection.weight(),
				mutatedConnections.stream()
						.filter(connection -> connection.isEnabled())
						.mapToDouble(Connection::weight)
						.reduce(1.0, (acc, weight) -> acc * weight),
				0.0001);
	}
}