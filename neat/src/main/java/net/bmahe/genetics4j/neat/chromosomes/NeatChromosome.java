package net.bmahe.genetics4j.neat.chromosomes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.neat.Connection;

/**
 * Represents a neural network chromosome in the NEAT (NeuroEvolution of Augmenting Topologies) algorithm.
 * 
 * <p>NeatChromosome is the core genetic representation in NEAT, encoding a neural network as a collection
 * of connections between nodes. Each chromosome defines a complete neural network topology with input nodes,
 * output nodes, optional hidden nodes, and weighted connections. The chromosome maintains essential
 * parameters for network construction and genetic operations.
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Network topology</strong>: Encoded as a list of connections with innovation numbers</li>
 * <li><strong>Node organization</strong>: Fixed input/output nodes with dynamically added hidden nodes</li>
 * <li><strong>Weight constraints</strong>: Configurable minimum and maximum weight bounds</li>
 * <li><strong>Innovation tracking</strong>: Connections sorted by innovation number for genetic alignment</li>
 * </ul>
 * 
 * <p>NEAT algorithm integration:
 * <ul>
 * <li><strong>Structural mutations</strong>: Add/delete nodes and connections while preserving innovation tracking</li>
 * <li><strong>Weight mutations</strong>: Modify connection weights within specified bounds</li>
 * <li><strong>Genetic crossover</strong>: Innovation-number-based gene alignment for topology recombination</li>
 * <li><strong>Compatibility distance</strong>: Genetic similarity measurement for speciation</li>
 * </ul>
 * 
 * <p>Network structure:
 * <ul>
 * <li><strong>Input nodes</strong>: Indices 0 to (numInputs - 1), receive external inputs</li>
 * <li><strong>Output nodes</strong>: Indices numInputs to (numInputs + numOutputs - 1), produce network outputs</li>
 * <li><strong>Hidden nodes</strong>: Indices starting from (numInputs + numOutputs), created by add-node mutations</li>
 * <li><strong>Connections</strong>: Weighted links between nodes with enable/disable states and innovation numbers</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create a basic NEAT chromosome
 * List<Connection> connections = List.of(
 *     Connection.of(0, 2, 0.5f, true, 0),   // input 0 -> output 0
 *     Connection.of(1, 3, -0.3f, true, 1)   // input 1 -> output 1
 * );
 * 
 * NeatChromosome chromosome = new NeatChromosome(
 *     2,      // number of inputs
 *     2,      // number of outputs  
 *     -1.0f,  // minimum weight
 *     1.0f,   // maximum weight
 *     connections
 * );
 * 
 * // Access chromosome properties
 * int numAlleles = chromosome.getNumAlleles();
 * Set<Integer> inputNodes = chromosome.getInputNodeIndices();
 * Set<Integer> outputNodes = chromosome.getOutputNodeIndices();
 * List<Connection> allConnections = chromosome.getConnections();
 * 
 * // Create feed-forward network for evaluation
 * FeedForwardNetwork network = new FeedForwardNetwork(
 *     chromosome.getInputNodeIndices(),
 *     chromosome.getOutputNodeIndices(),
 *     chromosome.getConnections(),
 *     Activations::sigmoid
 * );
 * }</pre>
 * 
 * <p>Genetic operations compatibility:
 * <ul>
 * <li><strong>Mutation operations</strong>: Compatible with weight, add-node, add-connection, and state mutations</li>
 * <li><strong>Crossover operations</strong>: Innovation numbers enable proper gene alignment between parents</li>
 * <li><strong>Selection operations</strong>: Supports species-based selection through compatibility distance</li>
 * <li><strong>Evaluation operations</strong>: Can be converted to executable neural networks</li>
 * </ul>
 * 
 * <p>Innovation number organization:
 * <ul>
 * <li><strong>Sorted connections</strong>: Connections automatically sorted by innovation number</li>
 * <li><strong>Genetic alignment</strong>: Enables efficient crossover and compatibility calculations</li>
 * <li><strong>Historical tracking</strong>: Maintains evolutionary history of structural changes</li>
 * <li><strong>Population consistency</strong>: Same innovation numbers across population for same connection types</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Immutable connections</strong>: Connection list is sorted once and made immutable</li>
 * <li><strong>Efficient lookup</strong>: Node indices computed deterministically for fast access</li>
 * <li><strong>Memory efficiency</strong>: Only stores necessary network topology information</li>
 * <li><strong>Cache-friendly</strong>: Sorted connections improve cache locality for genetic operations</li>
 * </ul>
 * 
 * <p>Integration with NEAT ecosystem:
 * <ul>
 * <li><strong>Chromosome factories</strong>: Created by NeatConnectedChromosomeFactory and similar</li>
 * <li><strong>Genetic operators</strong>: Processed by NEAT-specific mutation and crossover handlers</li>
 * <li><strong>Network evaluation</strong>: Converted to FeedForwardNetwork for fitness computation</li>
 * <li><strong>Speciation</strong>: Used in compatibility distance calculations for species formation</li>
 * </ul>
 * 
 * @see Connection
 * @see FeedForwardNetwork
 * @see InnovationManager
 * @see net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec
 */
public class NeatChromosome implements Chromosome {

	private final int numInputs;
	private final int numOutputs;
	private final float minWeightValue;
	private final float maxWeightValue;
	private final List<Connection> connections;

	/**
	 * Constructs a new NEAT chromosome with the specified network topology and parameters.
	 * 
	 * <p>This constructor creates an immutable neural network chromosome by copying and sorting
	 * the provided connections by their innovation numbers. The sorting ensures efficient
	 * genetic operations and proper gene alignment during crossover operations.
	 * 
	 * <p>Network structure validation:
	 * <ul>
	 * <li>Input and output counts must be positive</li>
	 * <li>Weight bounds must be properly ordered (min &lt; max)</li>
	 * <li>Connections list must not be null (but can be empty)</li>
	 * <li>Connection endpoints should reference valid nodes (not enforced here)</li>
	 * </ul>
	 * 
	 * @param _numInputs number of input nodes in the network (must be positive)
	 * @param _numOutputs number of output nodes in the network (must be positive)
	 * @param _minWeightValue minimum allowed connection weight value
	 * @param _maxWeightValue maximum allowed connection weight value (must be &gt; minWeightValue)
	 * @param _connections list of network connections (will be copied and sorted by innovation number)
	 * @throws IllegalArgumentException if numInputs &lt;= 0, numOutputs &lt;= 0, minWeightValue &gt;= maxWeightValue, or connections is null
	 */
	public NeatChromosome(final int _numInputs, final int _numOutputs, final float _minWeightValue,
			final float _maxWeightValue, final List<Connection> _connections) {
		Validate.isTrue(_numInputs > 0);
		Validate.isTrue(_numOutputs > 0);
		Validate.isTrue(_minWeightValue < _maxWeightValue);
		Validate.notNull(_connections);

		this.numInputs = _numInputs;
		this.numOutputs = _numOutputs;
		this.minWeightValue = _minWeightValue;
		this.maxWeightValue = _maxWeightValue;

		final List<Connection> copyOfConnections = new ArrayList<>(_connections);
		Collections.sort(copyOfConnections, Comparator.comparing(Connection::innovation));
		this.connections = Collections.unmodifiableList(copyOfConnections);
	}

	/**
	 * Returns the total number of alleles (genetic components) in this chromosome.
	 * 
	 * <p>For NEAT chromosomes, the allele count includes:
	 * <ul>
	 * <li>Input nodes: Each input node represents one allele</li>
	 * <li>Output nodes: Each output node represents one allele</li>
	 * <li>Connections: Each connection (with its weight and state) represents one allele</li>
	 * </ul>
	 * 
	 * <p>Hidden nodes are not counted separately as they are implicit in the connection structure.
	 * This count is used by the genetic algorithm framework for population statistics and
	 * compatibility calculations.
	 * 
	 * @return the total number of alleles in this chromosome
	 */
	@Override
	public int getNumAlleles() {
		return numInputs + numOutputs + connections.size();
	}

	/**
	 * Returns the number of input nodes in this neural network.
	 * 
	 * <p>Input nodes are the entry points for external data into the network. They are
	 * assigned node indices from 0 to (numInputs - 1) and do not apply activation functions
	 * to their input values.
	 * 
	 * @return the number of input nodes (always positive)
	 */
	public int getNumInputs() {
		return numInputs;
	}

	/**
	 * Returns the number of output nodes in this neural network.
	 * 
	 * <p>Output nodes produce the final results of network computation. They are assigned
	 * node indices from numInputs to (numInputs + numOutputs - 1) and apply activation
	 * functions to their weighted input sums.
	 * 
	 * @return the number of output nodes (always positive)
	 */
	public int getNumOutputs() {
		return numOutputs;
	}

	/**
	 * Returns the minimum allowed connection weight value for this network.
	 * 
	 * <p>This bound is used by mutation operators to constrain weight perturbations and
	 * ensure that connection weights remain within reasonable ranges. Weight mutations
	 * should respect this bound to maintain network stability.
	 * 
	 * @return the minimum allowed connection weight
	 */
	public float getMinWeightValue() {
		return minWeightValue;
	}

	/**
	 * Returns the maximum allowed connection weight value for this network.
	 * 
	 * <p>This bound is used by mutation operators to constrain weight perturbations and
	 * ensure that connection weights remain within reasonable ranges. Weight mutations
	 * should respect this bound to maintain network stability.
	 * 
	 * @return the maximum allowed connection weight
	 */
	public float getMaxWeightValue() {
		return maxWeightValue;
	}

	/**
	 * Returns an immutable list of all connections in this neural network.
	 * 
	 * <p>The connections are sorted by innovation number to ensure consistent ordering
	 * for genetic operations. Each connection defines a weighted link between two nodes
	 * and includes an enabled/disabled state for topology exploration.
	 * 
	 * <p>Connection properties:
	 * <ul>
	 * <li><strong>Immutable ordering</strong>: Connections are sorted by innovation number</li>
	 * <li><strong>Complete topology</strong>: Includes both enabled and disabled connections</li>
	 * <li><strong>Genetic information</strong>: Each connection carries innovation tracking data</li>
	 * <li><strong>Network structure</strong>: Defines the complete computational graph</li>
	 * </ul>
	 * 
	 * @return immutable list of network connections, sorted by innovation number
	 */
	public List<Connection> getConnections() {
		return connections;
	}

	/**
	 * Returns the set of input node indices for this neural network.
	 * 
	 * <p>Input node indices are deterministically assigned as consecutive integers starting
	 * from 0. Input nodes receive external data and do not apply activation functions to
	 * their values. These indices are used for network construction and evaluation.
	 * 
	 * <p>Index assignment:
	 * <ul>
	 * <li><strong>Range</strong>: 0 to (numInputs - 1)</li>
	 * <li><strong>Fixed assignment</strong>: Input indices never change during evolution</li>
	 * <li><strong>External interface</strong>: These indices map to external input data</li>
	 * <li><strong>No activation</strong>: Input nodes pass through their values unchanged</li>
	 * </ul>
	 * 
	 * @return set of input node indices (0 to numInputs-1)
	 */
	public Set<Integer> getInputNodeIndices() {
		return IntStream.range(0, numInputs)
				.boxed()
				.collect(Collectors.toSet());
	}

	/**
	 * Returns the set of output node indices for this neural network.
	 * 
	 * <p>Output node indices are deterministically assigned as consecutive integers starting
	 * immediately after the input nodes. Output nodes apply activation functions to their
	 * weighted input sums and produce the final network results.
	 * 
	 * <p>Index assignment:
	 * <ul>
	 * <li><strong>Range</strong>: numInputs to (numInputs + numOutputs - 1)</li>
	 * <li><strong>Fixed assignment</strong>: Output indices never change during evolution</li>
	 * <li><strong>Network results</strong>: These nodes produce the final computational output</li>
	 * <li><strong>Activation applied</strong>: Output nodes apply activation functions to their sums</li>
	 * </ul>
	 * 
	 * @return set of output node indices (numInputs to numInputs+numOutputs-1)
	 */
	public Set<Integer> getOutputNodeIndices() {
		return IntStream.range(numInputs, getNumInputs() + getNumOutputs())
				.boxed()
				.collect(Collectors.toSet());
	}

	@Override
	public int hashCode() {
		return Objects.hash(connections, maxWeightValue, minWeightValue, numInputs, numOutputs);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NeatChromosome other = (NeatChromosome) obj;
		return Objects.equals(connections, other.connections)
				&& Float.floatToIntBits(maxWeightValue) == Float.floatToIntBits(other.maxWeightValue)
				&& Float.floatToIntBits(minWeightValue) == Float.floatToIntBits(other.minWeightValue)
				&& numInputs == other.numInputs && numOutputs == other.numOutputs;
	}

	@Override
	public String toString() {
		return "NeatChromosome [numInputs=" + numInputs + ", numOutputs=" + numOutputs + ", minWeightValue="
				+ minWeightValue + ", maxWeightValue=" + maxWeightValue + ", connections=" + connections + "]";
	}
}