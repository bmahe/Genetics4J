package net.bmahe.genetics4j.neat.mutation.chromosome;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;
import net.bmahe.genetics4j.neat.spec.mutation.DeleteConnection;
import net.bmahe.genetics4j.neat.spec.mutation.DeleteNode;

public class NeatChromosomeDeleteNodeMutationHandlerTest {

	public static final Logger logger = LogManager.getLogger(NeatChromosomeDeleteNodeMutationHandlerTest.class);

	@Test
	public void constructor() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();

		assertThrows(NullPointerException.class, () -> new NeatChromosomeDeleteNodeMutationHandler(null));
		assertDoesNotThrow(() -> new NeatChromosomeDeleteNodeMutationHandler(randomGenerator));
	}

	@Test
	public void canHandle() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();

		final var neatChromosomeDeleteNode = new NeatChromosomeDeleteNodeMutationHandler(randomGenerator);

		assertThrows(NullPointerException.class, () -> neatChromosomeDeleteNode.canHandle(null, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeDeleteNode.canHandle(AddNode.of(0.2), null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeDeleteNode.canHandle(null, IntChromosomeSpec.of(10, 0, 10)));

		assertFalse(neatChromosomeDeleteNode.canHandle(AddNode.of(0.2), NeatChromosomeSpec.of(3, 2, 0, 10)));
		assertFalse(neatChromosomeDeleteNode.canHandle(DeleteNode.of(0.2), IntChromosomeSpec.of(10, 0, 10)));
		assertTrue(neatChromosomeDeleteNode.canHandle(DeleteNode.of(0.2), NeatChromosomeSpec.of(3, 2, 0, 10)));
	}

	@Test
	public void mutateConnectionOnlyOneNonInputOutputToNode() {

		final List<Connection> originalConnections = List.of(Connection.of(0, 1, 0.5f, true, 0),
				Connection.of(1, 2, 1.5f, true, 1),
				Connection.of(2, 3, 2.5f, true, 2),
				Connection.of(3, 4, 3.5f, true, 3),
				Connection.of(4, 5, 4.5f, true, 4));

		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		when(mockRandomGenerator.nextInt(anyInt())).thenReturn(1);

		final var neatChromosomeDeleteNodeMutationHandler = new NeatChromosomeDeleteNodeMutationHandler(
				mockRandomGenerator);

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final NeatChromosome mutatedChromosome = neatChromosomeDeleteNodeMutationHandler.mutate(DeleteNode.of(0.2),
				originalNeatChromosome);

		assertNotNull(mutatedChromosome);
		assertEquals(originalNeatChromosome.getInputNodeIndices(), mutatedChromosome.getInputNodeIndices());
		assertEquals(originalNeatChromosome.getOutputNodeIndices(), mutatedChromosome.getOutputNodeIndices());
		assertEquals(originalNeatChromosome.getMinWeightValue(), mutatedChromosome.getMinWeightValue(), 0.0001);
		assertEquals(originalNeatChromosome.getMaxWeightValue(), mutatedChromosome.getMaxWeightValue(), 0.0001);

		final List<Connection> mutatedConnections = mutatedChromosome.getConnections();
		assertNotNull(mutatedConnections);
		assertEquals(4, mutatedConnections.size());
		assertEquals(0,
				mutatedConnections.stream()
						.filter(connection -> connection.fromNodeIndex() == 5 || connection.toNodeIndex() == 5)
						.count());
	}

	@Test
	public void mutateConnectionOnlyOneNonInputOutputFromNode() {

		final List<Connection> originalConnections = List.of(Connection.of(0, 1, 0.5f, true, 0),
				Connection.of(1, 2, 1.5f, true, 1),
				Connection.of(2, 3, 2.5f, true, 2),
				Connection.of(3, 4, 3.5f, true, 3),
				Connection.of(5, 4, 4.5f, true, 4));

		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		when(mockRandomGenerator.nextInt(anyInt())).thenReturn(1);

		final var neatChromosomeDeleteNodeMutationHandler = new NeatChromosomeDeleteNodeMutationHandler(
				mockRandomGenerator);

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final NeatChromosome mutatedChromosome = neatChromosomeDeleteNodeMutationHandler.mutate(DeleteNode.of(0.2),
				originalNeatChromosome);

		assertNotNull(mutatedChromosome);
		assertEquals(originalNeatChromosome.getInputNodeIndices(), mutatedChromosome.getInputNodeIndices());
		assertEquals(originalNeatChromosome.getOutputNodeIndices(), mutatedChromosome.getOutputNodeIndices());
		assertEquals(originalNeatChromosome.getMinWeightValue(), mutatedChromosome.getMinWeightValue(), 0.0001);
		assertEquals(originalNeatChromosome.getMaxWeightValue(), mutatedChromosome.getMaxWeightValue(), 0.0001);

		final List<Connection> mutatedConnections = mutatedChromosome.getConnections();
		assertNotNull(mutatedConnections);
		assertEquals(4, mutatedConnections.size());
		assertEquals(0,
				mutatedConnections.stream()
						.filter(connection -> connection.fromNodeIndex() == 5 || connection.toNodeIndex() == 5)
						.count());
	}

	@Test
	public void mutateConnectionEmpty() {

		final List<Connection> originalConnections = List.of();

		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		when(mockRandomGenerator.nextInt(anyInt())).thenReturn(1);

		final var neatChromosomeDeleteNodeMutationHandler = new NeatChromosomeDeleteNodeMutationHandler(
				mockRandomGenerator);

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final NeatChromosome mutatedChromosome = neatChromosomeDeleteNodeMutationHandler.mutate(DeleteNode.of(0.2),
				originalNeatChromosome);

		assertNotNull(mutatedChromosome);
		assertEquals(originalNeatChromosome.getInputNodeIndices(), mutatedChromosome.getInputNodeIndices());
		assertEquals(originalNeatChromosome.getOutputNodeIndices(), mutatedChromosome.getOutputNodeIndices());
		assertEquals(originalNeatChromosome.getMinWeightValue(), mutatedChromosome.getMinWeightValue(), 0.0001);
		assertEquals(originalNeatChromosome.getMaxWeightValue(), mutatedChromosome.getMaxWeightValue(), 0.0001);

		final List<Connection> mutatedConnections = mutatedChromosome.getConnections();
		assertNotNull(mutatedConnections);
		assertEquals(0, mutatedConnections.size());
	}
}