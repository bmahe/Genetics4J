package net.bmahe.genetics4j.neat.mutation.chromosome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.InnovationManager;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.AddConnection;
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;

public class NeatChromosomeAddConnectionTest {

	public static final Logger logger = LogManager.getLogger(NeatChromosomeAddConnectionTest.class);

	@Test
	public void constructor() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final InnovationManager innovationManager = new InnovationManager();

		assertThrows(NullPointerException.class, () -> new NeatChromosomeAddConnection(null, null));
		assertThrows(NullPointerException.class, () -> new NeatChromosomeAddConnection(randomGenerator, null));
		assertThrows(NullPointerException.class, () -> new NeatChromosomeAddConnection(null, innovationManager));
	}

	@Test
	public void canHandle() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final InnovationManager innovationManager = new InnovationManager();

		final var neatChromosomeAddConnection = new NeatChromosomeAddConnection(randomGenerator, innovationManager);

		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.canHandle(null, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.canHandle(AddNode.of(0.2), null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeAddConnection.canHandle(null, IntChromosomeSpec.of(10, 0, 10)));

		assertFalse(neatChromosomeAddConnection.canHandle(AddNode.of(0.2), NeatChromosomeSpec.of(3, 2, 0, 10)));
		assertFalse(neatChromosomeAddConnection.canHandle(AddConnection.of(0.2), IntChromosomeSpec.of(10, 0, 10)));
		assertTrue(neatChromosomeAddConnection.canHandle(AddConnection.of(0.2), NeatChromosomeSpec.of(3, 2, 0, 10)));
	}

	@Test
	public void mutate() {

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, 1.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 3.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));
		final int maxOriginalInnovation = originalConnections.stream()
				.mapToInt(Connection::innovation)
				.max()
				.getAsInt();

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final RandomGenerator randomGenerator = mock(RandomGenerator.class);
		when(randomGenerator.nextInt(anyInt())).thenReturn(5)
				.thenReturn(7);
		when(randomGenerator.nextDouble(anyFloat(), anyFloat())).thenReturn(20.0d);
		final InnovationManager innovationManager = new InnovationManager(maxOriginalInnovation + 1);

		final var neatChromosomeAddConnection = new NeatChromosomeAddConnection(randomGenerator, innovationManager);

		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2), null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddNode.of(0.2), originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2),
						new IntChromosome(3, 0, 10, new int[] { 0, 1, 2 })));

		final NeatChromosome mutatedChromosome = neatChromosomeAddConnection.mutate(AddConnection.of(0.5),
				originalNeatChromosome);
		logger.info("Mutated chromosome: {}", mutatedChromosome);

		assertNotNull(mutatedChromosome);
		assertNotNull(mutatedChromosome.getConnections());
		assertEquals(originalConnections.size() + 1,
				mutatedChromosome.getConnections()
						.size());
		final var lastMutatedConnection = mutatedChromosome.getConnections()
				.get(mutatedChromosome.getConnections()
						.size() - 1);
		assertEquals(5, lastMutatedConnection.fromNodeIndex());
		assertEquals(7, lastMutatedConnection.toNodeIndex());
	}

	@Test
	public void mutateConnectionExist() {

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, 1.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 3.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));
		final int maxOriginalInnovation = originalConnections.stream()
				.mapToInt(Connection::innovation)
				.max()
				.getAsInt();

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final RandomGenerator randomGenerator = mock(RandomGenerator.class);
		when(randomGenerator.nextInt(anyInt())).thenReturn(6)
				.thenReturn(7);
		when(randomGenerator.nextDouble(anyFloat(), anyFloat())).thenReturn(20.0d);
		final InnovationManager innovationManager = new InnovationManager(maxOriginalInnovation + 1);

		final var neatChromosomeAddConnection = new NeatChromosomeAddConnection(randomGenerator, innovationManager);

		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2), null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddNode.of(0.2), originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2),
						new IntChromosome(3, 0, 10, new int[] { 0, 1, 2 })));

		final NeatChromosome mutatedChromosome = neatChromosomeAddConnection.mutate(AddConnection.of(0.5),
				originalNeatChromosome);
		logger.info("Mutated chromosome: {}", mutatedChromosome);

		assertNotNull(mutatedChromosome);
		assertEquals(originalNeatChromosome, mutatedChromosome);
	}

	@Test
	public void mutateConnectionFromOutput() {

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, 1.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 3.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));
		final int maxOriginalInnovation = originalConnections.stream()
				.mapToInt(Connection::innovation)
				.max()
				.getAsInt();

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final RandomGenerator randomGenerator = mock(RandomGenerator.class);
		when(randomGenerator.nextInt(anyInt())).thenReturn(3)
				.thenReturn(7);
		when(randomGenerator.nextDouble(anyFloat(), anyFloat())).thenReturn(20.0d);
		final InnovationManager innovationManager = new InnovationManager(maxOriginalInnovation + 1);

		final var neatChromosomeAddConnection = new NeatChromosomeAddConnection(randomGenerator, innovationManager);

		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2), null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddNode.of(0.2), originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2),
						new IntChromosome(3, 0, 10, new int[] { 0, 1, 2 })));

		final NeatChromosome mutatedChromosome = neatChromosomeAddConnection.mutate(AddConnection.of(0.5),
				originalNeatChromosome);
		logger.info("Mutated chromosome: {}", mutatedChromosome);

		assertNotNull(mutatedChromosome);
		assertEquals(originalNeatChromosome, mutatedChromosome);
	}

	@Test
	public void mutateConnectionToInput() {

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, 1.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 3.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));
		final int maxOriginalInnovation = originalConnections.stream()
				.mapToInt(Connection::innovation)
				.max()
				.getAsInt();

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final RandomGenerator randomGenerator = mock(RandomGenerator.class);
		when(randomGenerator.nextInt(anyInt())).thenReturn(3)
				.thenReturn(1);
		when(randomGenerator.nextDouble(anyFloat(), anyFloat())).thenReturn(20.0d);
		final InnovationManager innovationManager = new InnovationManager(maxOriginalInnovation + 1);

		final var neatChromosomeAddConnection = new NeatChromosomeAddConnection(randomGenerator, innovationManager);

		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2), null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddNode.of(0.2), originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2),
						new IntChromosome(3, 0, 10, new int[] { 0, 1, 2 })));

		final NeatChromosome mutatedChromosome = neatChromosomeAddConnection.mutate(AddConnection.of(0.5),
				originalNeatChromosome);
		logger.info("Mutated chromosome: {}", mutatedChromosome);

		assertNotNull(mutatedChromosome);
		assertEquals(originalNeatChromosome, mutatedChromosome);
	}

	@Test
	public void mutateConnectionFromAndToAreSame() {

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, 1.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 3.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));
		final int maxOriginalInnovation = originalConnections.stream()
				.mapToInt(Connection::innovation)
				.max()
				.getAsInt();

		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final RandomGenerator randomGenerator = mock(RandomGenerator.class);
		when(randomGenerator.nextInt(anyInt())).thenReturn(7)
				.thenReturn(7);
		when(randomGenerator.nextDouble(anyFloat(), anyFloat())).thenReturn(20.0d);
		final InnovationManager innovationManager = new InnovationManager(maxOriginalInnovation + 1);

		final var neatChromosomeAddConnection = new NeatChromosomeAddConnection(randomGenerator, innovationManager);

		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2), null));
		assertThrows(NullPointerException.class, () -> neatChromosomeAddConnection.mutate(null, originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddNode.of(0.2), originalNeatChromosome));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeAddConnection.mutate(AddConnection.of(0.2),
						new IntChromosome(3, 0, 10, new int[] { 0, 1, 2 })));

		final NeatChromosome mutatedChromosome = neatChromosomeAddConnection.mutate(AddConnection.of(0.5),
				originalNeatChromosome);
		logger.info("Mutated chromosome: {}", mutatedChromosome);

		assertNotNull(mutatedChromosome);
		assertEquals(originalNeatChromosome, mutatedChromosome);
	}
}