package net.bmahe.genetics4j.neat.mutation.chromosome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.spec.statistics.distributions.NormalDistribution;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;
import net.bmahe.genetics4j.neat.spec.mutation.NeatConnectionWeight;

public class NeatChromosomeConnectionWeightMutationHandlerTest {
	private static final Logger logger = LogManager.getLogger(NeatChromosomeConnectionWeightMutationHandlerTest.class);
	private final static double EPSILON = 0.0001d;

	@Test
	public void constructor() {
		assertThrows(NullPointerException.class, () -> new NeatChromosomeConnectionWeightMutationHandler(null));
	}

	@Test
	public void canHandle() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final var neatChromosomeConnectionWeightMutation = new NeatChromosomeConnectionWeightMutationHandler(
				randomGenerator);

		assertThrows(NullPointerException.class, () -> neatChromosomeConnectionWeightMutation.canHandle(null, null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeConnectionWeightMutation.canHandle(AddNode.of(0.2), null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeConnectionWeightMutation.canHandle(null, IntChromosomeSpec.of(10, 0, 10)));

		assertFalse(
				neatChromosomeConnectionWeightMutation.canHandle(AddNode.of(0.2), NeatChromosomeSpec.of(3, 2, 0, 10)));
		assertFalse(neatChromosomeConnectionWeightMutation.canHandle(NeatConnectionWeight.build(),
				IntChromosomeSpec.of(10, 0, 10)));
		assertTrue(neatChromosomeConnectionWeightMutation.canHandle(NeatConnectionWeight.build(),
				NeatChromosomeSpec.of(3, 2, 0, 10)));
	}

	@Test
	public void perturbateWeight() {

		final RandomGenerator randomGenerator = RandomGenerator.getDefault();
		final var neatChromosomeConnectionWeightMutation = new NeatChromosomeConnectionWeightMutationHandler(
				randomGenerator);

		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeConnectionWeightMutation.perturbateWeight(0, 0, 10, 0));
		assertEquals(0.0f, neatChromosomeConnectionWeightMutation.perturbateWeight(0, 0, 0, 0), EPSILON);
		assertEquals(5.0f, neatChromosomeConnectionWeightMutation.perturbateWeight(0, 5, -10, 10), EPSILON);
		assertEquals(-4.0f, neatChromosomeConnectionWeightMutation.perturbateWeight(1, -5, -10, 10), EPSILON);
		assertEquals(10.0f, neatChromosomeConnectionWeightMutation.perturbateWeight(0, 15, -10, 10), EPSILON);
		assertEquals(-10.0f, neatChromosomeConnectionWeightMutation.perturbateWeight(0, -15, -10, 10), EPSILON);
	}

	@Test
	public void mutateConnection() {
		final RandomGenerator mockRandomGenerator = mock(RandomGenerator.class);
		when(mockRandomGenerator.nextDouble()).thenReturn(0.5);

		final var neatChromosomeConnectionWeightMutation = new NeatChromosomeConnectionWeightMutationHandler(
				mockRandomGenerator);

		final Connection connection = Connection.of(4, 6, 0.25f, true, 12);

		final float minValue = -10f;
		final float maxValue = 10f;

		assertTrue(neatChromosomeConnectionWeightMutation
				.mutateConnection(connection, 0.5, () -> -5f, () -> 8f, minValue, maxValue) != null);
		assertEquals(8.0f,
				neatChromosomeConnectionWeightMutation
						.mutateConnection(connection, 0.5, () -> -5f, () -> 8f, minValue, maxValue)
						.weight(),
				EPSILON);
		assertEquals(8.0f,
				neatChromosomeConnectionWeightMutation
						.mutateConnection(connection, 0.45, () -> -5f, () -> 8f, minValue, maxValue)
						.weight(),
				EPSILON);
		assertEquals(0.25f - 5,
				neatChromosomeConnectionWeightMutation
						.mutateConnection(connection, 0.55, () -> -5f, () -> 8f, minValue, maxValue)
						.weight(),
				EPSILON);

		final Connection mutatedConnection = neatChromosomeConnectionWeightMutation
				.mutateConnection(connection, 0.55, () -> -5f, () -> 8f, minValue, maxValue);
		assertEquals(connection.fromNodeIndex(), mutatedConnection.fromNodeIndex());
		assertEquals(connection.toNodeIndex(), mutatedConnection.toNodeIndex());
		assertEquals(connection.isEnabled(), mutatedConnection.isEnabled());
		assertEquals(connection.innovation(), mutatedConnection.innovation());
	}

	@Test
	public void mutate() {

		final RandomGenerator mockZeroRandomGenerator = mock(RandomGenerator.class);
		when(mockZeroRandomGenerator.nextDouble()).thenReturn(0.0);
		when(mockZeroRandomGenerator.nextFloat()).thenReturn(0.0f);
		when(mockZeroRandomGenerator.nextGaussian()).thenReturn(0.0d);

		final var neatChromosomeConnectionWeightMutation = new NeatChromosomeConnectionWeightMutationHandler(
				mockZeroRandomGenerator);

		assertThrows(NullPointerException.class, () -> neatChromosomeConnectionWeightMutation.mutate(null, null));
		assertThrows(NullPointerException.class,
				() -> neatChromosomeConnectionWeightMutation.mutate(NeatConnectionWeight.build(), null));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeConnectionWeightMutation.mutate(NeatConnectionWeight.build(),
						new IntChromosome(2, 0, 10, new int[] { 2, 3 })));
		assertThrows(IllegalArgumentException.class,
				() -> neatChromosomeConnectionWeightMutation.mutate(AddNode.of(0.2),
						new NeatChromosome(3, 2, -10, 10, List.of())));

		final List<Connection> originalConnections = List.of(Connection.of(0, 5, 0.5f, true, 0),
				Connection.of(5, 6, 1.5f, true, 1),
				Connection.of(6, 7, 2.5f, true, 2),
				Connection.of(7, 8, 3.5f, true, 3),
				Connection.of(8, 3, 4.5f, true, 4));
		final NeatChromosome originalChromosome = new NeatChromosome(3, 2, -10, 10, originalConnections);

		final double distributionMean = 1;
		final NeatChromosome mutatedChromosome = neatChromosomeConnectionWeightMutation
				.mutate(NeatConnectionWeight.builder()
						.perturbationDistribution(NormalDistribution.of(distributionMean, 0))
						.build(), originalChromosome);

		logger.info("Got: {}", mutatedChromosome);
		final List<Connection> mutatedConnections = mutatedChromosome.getConnections();

		assertNotNull(mutatedChromosome);
		assertEquals(originalConnections.size(), mutatedConnections.size());

		for (int i = 0; i < originalConnections.size(); i++) {
			final Connection originalConnection = originalConnections.get(i);
			final Connection mutatedConnection = mutatedConnections.get(i);

			assertEquals(originalConnection.fromNodeIndex(), mutatedConnection.fromNodeIndex());
			assertEquals(originalConnection.toNodeIndex(), mutatedConnection.toNodeIndex());
			assertEquals(originalConnection.innovation(), mutatedConnection.innovation());
			assertEquals(originalConnection.isEnabled(), mutatedConnection.isEnabled());

			assertEquals(originalConnection.weight() + distributionMean, mutatedConnection.weight());
		}
	}
}