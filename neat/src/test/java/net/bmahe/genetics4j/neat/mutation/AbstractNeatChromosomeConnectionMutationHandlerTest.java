package net.bmahe.genetics4j.neat.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.mutation.chromosome.AbstractNeatChromosomeConnectionMutationHandler;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.AddConnection;
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;

public class AbstractNeatChromosomeConnectionMutationHandlerTest {
	public static final Logger logger = LogManager.getLogger(AbstractNeatChromosomeConnectionMutationHandlerTest.class);

	@Test
	public void constructor() {
		final RandomGenerator randomGenerator = mock(RandomGenerator.class);

		assertThrows(NullPointerException.class, () -> {
			new AbstractNeatChromosomeConnectionMutationHandler<AddNode>(AddNode.class, null) {

				@Override
				protected List<Connection> mutateConnection(final AddNode mutationPolicy,
						final NeatChromosome neatChromosome, final Connection oldConnection, final int i) {
					return null;
				}
			};
		});

		assertThrows(NullPointerException.class, () -> {
			new AbstractNeatChromosomeConnectionMutationHandler<AddNode>(null, randomGenerator) {

				@Override
				protected List<Connection> mutateConnection(final AddNode mutationPolicy,
						final NeatChromosome neatChromosome, final Connection oldConnection, final int i) {
					return null;
				}
			};
		});

	}

	@Test
	public void canHandle() {

		final RandomGenerator randomGenerator = mock(RandomGenerator.class);

		final AbstractNeatChromosomeConnectionMutationHandler<AddNode> test = new AbstractNeatChromosomeConnectionMutationHandler<AddNode>(
				AddNode.class,
				randomGenerator) {

			@Override
			protected List<Connection> mutateConnection(final AddNode mutationPolicy, final NeatChromosome neatChromosome,
					final Connection oldConnection, final int i) {
				return null;
			}
		};

		assertThrows(NullPointerException.class, () -> test.canHandle(null, null));
		assertThrows(NullPointerException.class, () -> test.canHandle(AddNode.of(0.2), null));
		assertThrows(NullPointerException.class, () -> test.canHandle(null, NeatChromosomeSpec.of(3, 2, 0.0f, 1.f)));

		assertFalse(test.canHandle(AddConnection.of(0.2), NeatChromosomeSpec.of(3, 2, 0.0f, 1.f)));
		assertFalse(test.canHandle(AddNode.of(0.2), IntChromosomeSpec.of(2, 0, 10)));
		assertTrue(test.canHandle(AddNode.of(0.2), NeatChromosomeSpec.of(3, 2, 0.0f, 1.f)));
	}

	@Test
	public void mutate() {

		final List<Connection> connections = List.of(Connection.of(0, 1, 0.2f, true, 0),
				Connection.of(0, 2, 0.2f, true, 1),
				Connection.of(0, 3, 0.2f, true, 2),
				Connection.of(0, 4, 0.2f, true, 3),
				Connection.of(0, 5, 0.2f, true, 4));
		final NeatChromosome originalNeatChromosome = new NeatChromosome(3, 2, 0f, 10.0f, connections);

		final int chosenIndex = 2;
		final RandomGenerator randomGenerator = mock(RandomGenerator.class);
		when(randomGenerator.nextInt(eq(connections.size()))).thenReturn(chosenIndex);

		final List<Connection> mutatedConnections = List.of(Connection.of(10, 11, 10.2f, true, 10),
				Connection.of(10, 12, 10.2f, true, 11),
				Connection.of(10, 13, 10.2f, true, 12));

		final var mutateConnection = new AtomicInteger();
		final var testNeatChromosomeMutationHandler = new AbstractNeatChromosomeConnectionMutationHandler<AddNode>(
				AddNode.class,
				randomGenerator) {

			@Override
			protected List<Connection> mutateConnection(final AddNode mutationPolicy, final NeatChromosome neatChromosome,
					final Connection oldConnection, final int i) {

				assertEquals(originalNeatChromosome, neatChromosome);
				assertEquals(connections.get(chosenIndex), oldConnection);
				assertEquals(chosenIndex, i);

				mutateConnection.incrementAndGet();
				return mutatedConnections;
			}
		};

		final NeatChromosome emptyNeatChromosome = new NeatChromosome(3, 2, 0f, 10.0f, List.of());
		final var resultEmptyMutations = testNeatChromosomeMutationHandler.mutate(AddNode.of(0.2), emptyNeatChromosome);
		assertNotNull(resultEmptyMutations);
		assertNotNull(resultEmptyMutations.getConnections());
		assertEquals(0,
				resultEmptyMutations.getConnections()
						.size());

		final var resultMutations = testNeatChromosomeMutationHandler.mutate(AddNode.of(0.2), originalNeatChromosome);
		logger.info("Result mutations: {}", resultMutations);
		assertNotNull(resultMutations);
		assertEquals(1, mutateConnection.get());
		assertEquals(connections.size() + mutatedConnections.size() - 1,
				resultMutations.getConnections()
						.size());
	}
}