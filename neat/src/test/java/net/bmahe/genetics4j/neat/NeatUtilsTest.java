package net.bmahe.genetics4j.neat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public class NeatUtilsTest {
	public static final Logger logger = LogManager.getLogger(NeatUtilsTest.class);

	/*******************************************************************************************************/
	/* ComputeForwardLinks */
	/*******************************************************************************************************/

	@Test
	public void computeForwardLinksNotNullInput() {
		assertThrows(NullPointerException.class, () -> NeatUtils.computeForwardLinks(null));
	}

	@Test
	public void computeForwardLinksEmptyConnections() {
		final var forwardLinks = NeatUtils.computeForwardLinks(List.of());

		assertNotNull(forwardLinks);
		assertEquals(0, forwardLinks.size());
	}

	@Test
	public void computeForwardLinksDuplicateConnection() {
		List<Connection> connections = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 0),
				Connection.of(1, 3, 1.5f, true, 0),
				Connection.of(4, 2, -4.1f, true, 0),
				Connection.of(1, 3, -.3f, true, 0),
				Connection.of(0, 2, -1f, false, 0));

		assertThrows(IllegalArgumentException.class, () -> NeatUtils.computeForwardLinks(connections));
	}

	@Test
	public void computeForwardLinks() {

		//
		// 0 ----|
		// ______3 -----> 4 ----> 2
		// 1 ----|
		//
		//

		List<Connection> connections = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 0),
				Connection.of(4, 5, -0.2f, true, 0),
				Connection.of(1, 3, 1.5f, true, 0),
				Connection.of(4, 2, -4.1f, true, 0),
				Connection.of(1, 3, -.3f, false, 0),
				Connection.of(0, 2, -1f, false, 0));

		/**
		 * Deduct 1 because of the 4-5 and 4-2 start from the same node
		 */
		final long enabledConnections = connections.stream()
				.filter(Connection::isEnabled)
				.count() - 1;

		final var forwardLinks = NeatUtils.computeForwardLinks(connections);
		assertNotNull(forwardLinks);

		logger.info("Connections: {}", connections);
		logger.info("\tForward links: {}", forwardLinks);

		assertEquals(enabledConnections, forwardLinks.size());

		assertNull(forwardLinks.get(5));

		assertNotNull(forwardLinks.get(0));
		assertEquals(1,
				forwardLinks.get(0)
						.size());

		assertNotNull(forwardLinks.get(1));
		assertEquals(1,
				forwardLinks.get(1)
						.size());

		assertNotNull(forwardLinks.get(4));
		assertEquals(2,
				forwardLinks.get(4)
						.size());

	}

	/*******************************************************************************************************/
	/* ComputeBackwardLinks */
	/*******************************************************************************************************/

	@Test
	public void computeBackwardLinksNotNullInput() {
		assertThrows(NullPointerException.class, () -> NeatUtils.computeBackwardLinks(null));
	}

	@Test
	public void computeBackwardLinksEmptyConnections() {
		final var backwardLinks = NeatUtils.computeBackwardLinks(List.of());

		assertNotNull(backwardLinks);
		assertEquals(0, backwardLinks.size());
	}

	@Test
	public void computeBackwardLinksDuplicateConnection() {
		List<Connection> connections = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 0),
				Connection.of(1, 3, 1.5f, true, 0),
				Connection.of(4, 2, -4.1f, true, 0),
				Connection.of(1, 3, -.3f, true, 0),
				Connection.of(0, 2, -1f, false, 0));

		assertThrows(IllegalArgumentException.class, () -> NeatUtils.computeBackwardLinks(connections));
	}

	@Test
	public void computeBackwardLinks() {

		//
		// 0 ----|
		// ______3 -----> 4 ----> 2
		// 1 ----|
		//
		//

		List<Connection> connections = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 0),
				Connection.of(4, 5, -0.2f, true, 0),
				Connection.of(1, 3, 1.5f, true, 0),
				Connection.of(4, 2, -4.1f, true, 0),
				Connection.of(1, 3, -.3f, false, 0),
				Connection.of(0, 2, -1f, false, 0));

		/**
		 * Deduct 1 because of the 4-5 and 4-2 start from the same node
		 */
		final long enabledConnections = connections.stream()
				.filter(Connection::isEnabled)
				.count() - 1;

		final var backwardLinks = NeatUtils.computeBackwardLinks(connections);
		assertNotNull(backwardLinks);

		logger.info("Connections: {}", connections);
		logger.info("\tBackward links: {}", backwardLinks);

		assertEquals(enabledConnections, backwardLinks.size());

		assertNull(backwardLinks.get(0));

		assertNotNull(backwardLinks.get(2));
		assertEquals(1,
				backwardLinks.get(2)
						.size());

		assertNotNull(backwardLinks.get(3));
		assertEquals(2,
				backwardLinks.get(3)
						.size());

		assertNotNull(backwardLinks.get(5));
		assertEquals(1,
				backwardLinks.get(5)
						.size());

	}

	/*******************************************************************************************************/
	/* ComputeBackwardConnections */
	/*******************************************************************************************************/

	@Test
	public void computeBackwardConnectionsNotNullInput() {
		assertThrows(NullPointerException.class, () -> NeatUtils.computeBackwardConnections(null));
	}

	@Test
	public void computeBackwardConnectionsEmptyConnections() {
		final var backwardConnections = NeatUtils.computeBackwardConnections(List.of());

		assertNotNull(backwardConnections);
		assertEquals(0, backwardConnections.size());
	}

	@Test
	public void computeBackwardConnectionsDuplicateConnection() {
		List<Connection> connections = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 0),
				Connection.of(1, 3, 1.5f, true, 0),
				Connection.of(4, 2, -4.1f, true, 0),
				Connection.of(1, 3, -.3f, true, 0),
				Connection.of(0, 2, -1f, false, 0));

		assertThrows(IllegalArgumentException.class, () -> NeatUtils.computeBackwardConnections(connections));
	}

	@Test
	public void computeBackwardConnections() {

		//
		// 0 ----|
		// ______3 -----> 4 ----> 2
		// 1 ----|
		//
		//

		List<Connection> connections = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 0),
				Connection.of(4, 5, -0.2f, true, 0),
				Connection.of(1, 3, 1.5f, true, 0),
				Connection.of(4, 2, -4.1f, true, 0),
				Connection.of(1, 3, -.3f, false, 0),
				Connection.of(0, 2, -1f, false, 0));

		/**
		 * Deduct 1 because of the 4-5 and 4-2 start from the same node
		 */
		final long enabledConnections = connections.stream()
				.filter(Connection::isEnabled)
				.count() - 1;

		final var backwardConnections = NeatUtils.computeBackwardConnections(connections);
		assertNotNull(backwardConnections);

		logger.info("Connections: {}", connections);
		logger.info("\tBackward links: {}", backwardConnections);

		assertEquals(enabledConnections, backwardConnections.size());

		assertNull(backwardConnections.get(0));

		assertNotNull(backwardConnections.get(2));
		assertEquals(1,
				backwardConnections.get(2)
						.size());

		assertNotNull(backwardConnections.get(3));
		assertEquals(2,
				backwardConnections.get(3)
						.size());

		assertNotNull(backwardConnections.get(5));
		assertEquals(1,
				backwardConnections.get(5)
						.size());

	}

	/*******************************************************************************************************/
	/* computeDeadNodes */
	/*******************************************************************************************************/

	@Test
	public void computeDeadNodesNotNullInput() {
		assertThrows(NullPointerException.class, () -> NeatUtils.computeDeadNodes(null, Map.of(), Map.of(), Set.of()));
	}

	@Test
	public void computeDeadNodes() {

		//
		// _______-----> 9 ----> 10
		// _______|
		// 0 ----|
		// ______|
		// ______3 -----> 4 ----> 2
		// 1 ----|
		//
		// 5 ----> 6
		// <----
		//

		List<Connection> connections = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 0),
				Connection.of(4, 2, -0.2f, true, 0),
				Connection.of(1, 3, 1.5f, true, 0),
				Connection.of(5, 6, -4.1f, true, 0),
				Connection.of(6, 5, -4.1f, true, 0),
				Connection.of(0, 9, -4.1f, true, 0),
				Connection.of(9, 10, -4.1f, true, 0),
				Connection.of(1, 3, -.3f, false, 0),
				Connection.of(0, 2, -1f, false, 0));

		final var forwardLinks = NeatUtils.computeForwardLinks(connections);
		assertNotNull(forwardLinks);

		logger.info("Connections: {}", connections);
		logger.info("\tForward links: {}", forwardLinks);

		final var backwardLinks = NeatUtils.computeBackwardLinks(connections);
		assertNotNull(backwardLinks);

		logger.info("Connections: {}", connections);
		logger.info("\tBackward links: {}", backwardLinks);

		final Set<Integer> deadNodes = NeatUtils.computeDeadNodes(connections, forwardLinks, backwardLinks, Set.of(2));
		logger.info("Dead nodes: {}", deadNodes);

		assertNotNull(deadNodes);

		/**
		 * Nodes 5, 6, 9, 10 are dead
		 */
		assertEquals(4, deadNodes.size());
	}

	/*******************************************************************************************************/
	/* partitionLayersNodes */
	/*******************************************************************************************************/

	@Test
	public void partitionLayersNodes() {

		// @formatter:off
		//               <-----
		//       -----> 9 ----> 10
		//       |
		// 0 ----|
		//       |
		//       3 -----> 4 ----> 2
		// 1 ----|                ^
		//                        |
		//           11 -----------
		//
		// 5 ----> 6
		//   <----
		// @formatter:on

		List<Connection> connections = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 0),
				Connection.of(4, 2, -0.2f, true, 0),
				Connection.of(1, 3, 1.5f, true, 0),
				Connection.of(5, 6, -4.1f, true, 0),
				Connection.of(6, 5, -4.1f, true, 0),
				Connection.of(0, 9, -4.1f, true, 0),
				Connection.of(9, 10, -4.1f, true, 0),
				Connection.of(10, 9, -4.1f, true, 0),
				Connection.of(11, 2, -4.1f, true, 0),
				Connection.of(1, 3, -.3f, false, 0),
				Connection.of(0, 2, -1f, false, 0));

		final var inputNodes = Set.of(0, 1);
		final var outputNodes = Set.of(2);

		final List<List<Integer>> partitionLayers = NeatUtils.partitionLayersNodes(inputNodes, outputNodes, connections);

		assertNotNull(partitionLayers);
		assertEquals(4, partitionLayers.size());
		assertEquals(Set.of(0, 1), Set.copyOf(partitionLayers.get(0)));
		assertEquals(Set.of(3), Set.copyOf(partitionLayers.get(1)));
		assertEquals(Set.of(4), Set.copyOf(partitionLayers.get(2)));
		assertEquals(Set.of(2), Set.copyOf(partitionLayers.get(3)));
	}

	/*******************************************************************************************************/
	/* compatibilityDistance */
	/*******************************************************************************************************/

	@Test
	@DisplayName("Better Chromosome with both excess and disjoint genes")
	public void compatibilityDistanceSame() {

		final List<Connection> connectionsA = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 1),
				Connection.of(4, 2, -0.2f, true, 2),
				Connection.of(1, 3, 1.5f, true, 3),
				Connection.of(5, 6, -4.1f, true, 4),
				Connection.of(6, 5, -4.1f, true, 5),
				Connection.of(0, 9, -4.1f, true, 6),
				Connection.of(9, 10, -4.1f, true, 7),
				Connection.of(10, 9, -4.1f, true, 8),
				Connection.of(11, 2, -4.1f, true, 9),
				Connection.of(1, 3, -.3f, false, 10),
				Connection.of(0, 2, -1f, false, 11));

		final List<Connection> connectionsB = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 1),
				Connection.of(4, 2, -0.2f, true, 2),
				Connection.of(1, 3, 1.5f, true, 3),
				Connection.of(5, 6, -4.1f, true, 4),
				Connection.of(6, 5, -4.1f, true, 5),
				Connection.of(0, 9, -4.1f, true, 6),
				Connection.of(9, 10, -4.1f, true, 7),
				Connection.of(10, 9, -4.1f, true, 8),
				Connection.of(11, 2, -4.1f, true, 9),
				Connection.of(1, 3, -.3f, false, 10),
				Connection.of(0, 2, -1f, false, 11));

		final NeatChromosome chromosomeA = new NeatChromosome(2, 1, -10, 10, connectionsA);
		final Genotype genotypeA = new Genotype(chromosomeA);

		final NeatChromosome chromosomeB = new NeatChromosome(2, 1, -10, 10, connectionsB);
		final Genotype genotypeB = new Genotype(chromosomeB);

		final float compatibilityDistance = NeatUtils.compatibilityDistance(genotypeA, genotypeB, 0, 10, 10, 10);
		assertEquals(0.0f, compatibilityDistance, 0.001f);
	}

	@Test
	public void compatibilityDistanceExcessGenes() {

		final List<Connection> connectionsA = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 1),
				Connection.of(4, 2, -0.2f, true, 2),
				Connection.of(1, 3, 1.5f, true, 3),
				Connection.of(5, 6, -4.1f, true, 4),
				Connection.of(6, 5, -4.1f, true, 5),
				Connection.of(0, 9, -4.1f, true, 6),
				Connection.of(9, 10, -4.1f, true, 7),
				Connection.of(10, 9, -4.1f, true, 8),
				Connection.of(11, 2, -4.1f, true, 9),
				Connection.of(1, 3, -.3f, false, 10),
				Connection.of(0, 2, -1f, false, 11));

		final List<Connection> connectionsB = new ArrayList<>(connectionsA);
		connectionsB.addAll(List.of(Connection.of(0, 2, -1f, false, 12), Connection.of(0, 2, -1f, false, 13)));

		final NeatChromosome chromosomeA = new NeatChromosome(2, 1, -10, 10, connectionsA);
		final Genotype genotypeA = new Genotype(chromosomeA);

		final NeatChromosome chromosomeB = new NeatChromosome(2, 1, -10, 10, connectionsB);
		final Genotype genotypeB = new Genotype(chromosomeB);

		/**
		 * 2 excess genes x c1, where c1 = 10
		 */
		assertEquals(20.0f, NeatUtils.compatibilityDistance(genotypeA, genotypeB, 0, 10, 1, 1), 0.001f);
		assertEquals(20.0f, NeatUtils.compatibilityDistance(genotypeB, genotypeA, 0, 10, 1, 1), 0.001f);
	}

	@Test
	public void compatibilityDistanceDisjointsGenes() {

		final List<Connection> connectionsA = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 1),
				Connection.of(4, 2, -0.2f, true, 2),
				Connection.of(1, 3, 1.5f, true, 3),
				Connection.of(5, 6, -4.1f, true, 4),
				Connection.of(9, 10, -4.1f, true, 7),
				Connection.of(10, 9, -4.1f, true, 8),
				Connection.of(11, 2, -4.1f, true, 9),
				Connection.of(1, 3, -.3f, false, 10),
				Connection.of(0, 2, -1f, false, 11),
				Connection.of(0, 2, -1f, false, 12),
				Connection.of(0, 2, -1f, false, 13),
				Connection.of(0, 2, -1f, false, 14),
				Connection.of(0, 2, -1f, false, 15),
				Connection.of(0, 2, -1f, false, 16),
				Connection.of(0, 2, -1f, false, 17),
				Connection.of(0, 2, -1f, false, 18),
				Connection.of(0, 2, -1f, false, 19),
				Connection.of(0, 2, -1f, false, 20),
				Connection.of(0, 2, -1f, false, 21),
				Connection.of(0, 2, -1f, false, 22),
				Connection.of(0, 2, -1f, false, 23),
				Connection.of(0, 2, -1f, false, 24),
				Connection.of(0, 2, -1f, false, 25));

		final List<Connection> connectionsB = new ArrayList<>(connectionsA.subList(1, connectionsA.size()));
		connectionsB.addAll(List.of(Connection.of(6, 5, -4.1f, true, 5), Connection.of(0, 9, -4.1f, true, 6)));

		final NeatChromosome chromosomeA = new NeatChromosome(2, 1, -10, 10, connectionsA);
		final Genotype genotypeA = new Genotype(chromosomeA);

		final NeatChromosome chromosomeB = new NeatChromosome(2, 1, -10, 10, connectionsB);
		final Genotype genotypeB = new Genotype(chromosomeB);

		/**
		 * 3 disjoint genes x c1 / n, where c2 = 10
		 */
		final float c2 = 10.0f;
		final int maxSize = Math.max(connectionsA.size(), connectionsB.size());
		final float expectedCompatibilityDistance = 3 * c2 / maxSize;
		assertTrue(maxSize > 20); // want to trigger the longer version of N
		assertEquals(expectedCompatibilityDistance,
				NeatUtils.compatibilityDistance(genotypeA, genotypeB, 0, 1, c2, 1),
				0.001f);
		assertEquals(expectedCompatibilityDistance,
				NeatUtils.compatibilityDistance(genotypeB, genotypeA, 0, 1, c2, 1),
				0.001f);
	}

	@Test
	public void compatibilityDistanceWeight() {

		final List<Connection> connectionsA = List.of(Connection.of(0, 3, 0.2f, true, 0),
				Connection.of(3, 4, -0.2f, true, 1),
				Connection.of(4, 2, -0.2f, true, 2),
				Connection.of(1, 3, 1.5f, true, 3),
				Connection.of(5, 6, -4.1f, true, 4),
				Connection.of(9, 10, -4.1f, true, 7),
				Connection.of(10, 9, -4.1f, true, 8),
				Connection.of(11, 2, -4.1f, true, 9),
				Connection.of(1, 3, -.3f, false, 10),
				Connection.of(0, 2, -1f, false, 11));

		final List<Connection> connectionsB = connectionsA.stream()
				.map(connection -> Connection.of(connection.fromNodeIndex(),
						connection.toNodeIndex(),
						connection.weight() + (connection.innovation() % 2 == 0 ? -1.0f : 1.0f),
						connection.isEnabled(),
						connection.innovation()))
				.toList();

		final NeatChromosome chromosomeA = new NeatChromosome(2, 1, -10, 10, connectionsA);
		final Genotype genotypeA = new Genotype(chromosomeA);

		final NeatChromosome chromosomeB = new NeatChromosome(2, 1, -10, 10, connectionsB);
		final Genotype genotypeB = new Genotype(chromosomeB);

		assertEquals(10.0f, NeatUtils.compatibilityDistance(genotypeA, genotypeB, 0, 1, 1, 10), 0.001f);
		assertEquals(10.0f, NeatUtils.compatibilityDistance(genotypeB, genotypeA, 0, 1, 1, 10), 0.001f);
	}
}