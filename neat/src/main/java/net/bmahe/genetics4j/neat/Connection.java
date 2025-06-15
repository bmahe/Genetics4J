package net.bmahe.genetics4j.neat;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

/**
 * Represents a neural network connection in the NEAT (NeuroEvolution of Augmenting Topologies) algorithm.
 * 
 * <p>A Connection is a fundamental building block of NEAT neural networks, representing a weighted link
 * between two nodes. Each connection carries essential information for network topology, genetic operations,
 * and evolutionary tracking through innovation numbers. Connections can be enabled or disabled, allowing
 * for dynamic network topology exploration during evolution.
 * 
 * <p>Key properties:
 * <ul>
 * <li><strong>Source and target nodes</strong>: Define the directed connection in the network graph</li>
 * <li><strong>Connection weight</strong>: Determines the strength and polarity of signal transmission</li>
 * <li><strong>Enabled state</strong>: Controls whether the connection participates in network computation</li>
 * <li><strong>Innovation number</strong>: Unique identifier for genetic alignment and historical tracking</li>
 * </ul>
 * 
 * <p>NEAT algorithm integration:
 * <ul>
 * <li><strong>Genetic crossover</strong>: Innovation numbers enable proper gene alignment between parents</li>
 * <li><strong>Structural mutations</strong>: Connections can be added, removed, or have their state toggled</li>
 * <li><strong>Weight evolution</strong>: Connection weights are subject to mutation and optimization</li>
 * <li><strong>Topology innovation</strong>: New connections track the historical order of structural changes</li>
 * </ul>
 * 
 * <p>Network computation role:
 * <ul>
 * <li><strong>Signal propagation</strong>: Enabled connections transmit weighted signals between nodes</li>
 * <li><strong>Network evaluation</strong>: Only enabled connections participate in forward propagation</li>
 * <li><strong>Topology dynamics</strong>: Enable/disable states allow topology exploration without gene loss</li>
 * <li><strong>Weight optimization</strong>: Connection weights are evolved to optimize network performance</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create a new connection
 * Connection connection = Connection.of(
 *     0,      // from node index
 *     3,      // to node index  
 *     0.75f,  // connection weight
 *     true,   // enabled state
 *     42      // innovation number
 * );
 * 
 * // Builder pattern for complex construction
 * Connection connection = Connection.builder()
 *     .fromNodeIndex(1)
 *     .toNodeIndex(4)
 *     .weight(-0.5f)
 *     .isEnabled(false)
 *     .innovation(15)
 *     .build();
 * 
 * // Copy with modifications
 * Connection modifiedConnection = Connection.builder()
 *     .from(connection)
 *     .weight(connection.weight() + 0.1f)
 *     .build();
 * }</pre>
 * 
 * <p>Innovation number significance:
 * <ul>
 * <li><strong>Historical marking</strong>: Tracks when each connection type first appeared in evolution</li>
 * <li><strong>Genetic alignment</strong>: Enables meaningful crossover between different network topologies</li>
 * <li><strong>Compatibility distance</strong>: Used to measure genetic similarity for speciation</li>
 * <li><strong>Structural tracking</strong>: Maintains evolutionary history of network topology changes</li>
 * </ul>
 * 
 * <p>Connection state management:
 * <ul>
 * <li><strong>Enabled connections</strong>: Actively participate in network computation</li>
 * <li><strong>Disabled connections</strong>: Preserved in genome but don't affect network output</li>
 * <li><strong>State mutations</strong>: Can toggle between enabled/disabled states during evolution</li>
 * <li><strong>Structural preservation</strong>: Disabled connections maintain genetic information</li>
 * </ul>
 * 
 * <p>Validation and constraints:
 * <ul>
 * <li><strong>Node indices</strong>: Must be non-negative and reference valid nodes in the network</li>
 * <li><strong>Innovation numbers</strong>: Must be non-negative and unique within the population context</li>
 * <li><strong>Self-connections</strong>: Typically not allowed (from and to nodes must be different)</li>
 * <li><strong>Weight bounds</strong>: While not enforced, weights typically range within reasonable bounds</li>
 * </ul>
 * 
 * @see NeatChromosome
 * @see InnovationManager
 * @see FeedForwardNetwork
 * @see net.bmahe.genetics4j.neat.mutation.AddConnectionPolicyHandler
 */
@Value.Immutable
public interface Connection {

	/**
	 * Returns the index of the source node for this connection.
	 * 
	 * @return the source node index (non-negative)
	 */
	@Value.Parameter
	int fromNodeIndex();

	/**
	 * Returns the index of the target node for this connection.
	 * 
	 * @return the target node index (non-negative)
	 */
	@Value.Parameter
	int toNodeIndex();

	/**
	 * Returns the weight of this connection.
	 * 
	 * <p>The weight determines the strength and polarity of signal transmission through
	 * this connection. Positive weights amplify signals, negative weights invert them,
	 * and zero weights effectively disable signal transmission.
	 * 
	 * @return the connection weight
	 */
	@Value.Parameter
	float weight();

	/**
	 * Returns whether this connection is enabled.
	 * 
	 * <p>Enabled connections participate in network computation and signal propagation.
	 * Disabled connections are preserved in the genome but do not affect network output,
	 * allowing for topology exploration without gene loss.
	 * 
	 * @return true if the connection is enabled, false otherwise
	 */
	@Value.Parameter
	boolean isEnabled();

	/**
	 * Returns the innovation number for this connection.
	 * 
	 * <p>Innovation numbers are unique identifiers that track the historical order of
	 * structural mutations in the NEAT algorithm. They enable proper gene alignment
	 * during crossover and are used to calculate compatibility distance for speciation.
	 * 
	 * @return the innovation number (non-negative)
	 */
	@Value.Parameter
	int innovation();

	@Value.Check
	default void check() {
		Validate.isTrue(fromNodeIndex() >= 0);
		Validate.isTrue(toNodeIndex() >= 0);
		Validate.isTrue(innovation() >= 0);
	}

	static class Builder extends ImmutableConnection.Builder {
	}

	static Builder builder() {
		return new Builder();
	}

	/**
	 * Creates a new connection with the specified parameters.
	 * 
	 * @param from the source node index
	 * @param to the target node index
	 * @param weight the connection weight
	 * @param isEnabled whether the connection is enabled
	 * @param innovation the innovation number
	 * @return a new connection instance
	 * @throws IllegalArgumentException if node indices or innovation number are negative
	 */
	static Connection of(final int from, final int to, final float weight, final boolean isEnabled,
			final int innovation) {
		return ImmutableConnection.of(from, to, weight, isEnabled, innovation);
	}

	/**
	 * Creates a copy of the specified connection.
	 * 
	 * @param original the connection to copy
	 * @return a new connection instance with the same properties as the original
	 */
	static Connection copyOf(Connection original) {
		return ImmutableConnection.copyOf(original);
	}
}