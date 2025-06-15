package net.bmahe.genetics4j.neat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements a feed-forward neural network for evaluating NEAT (NeuroEvolution of Augmenting Topologies) chromosomes.
 * 
 * <p>FeedForwardNetwork provides a computational engine for executing neural networks evolved by the NEAT algorithm.
 * It takes a network topology defined by connections and nodes, organizes them into computational layers, and
 * provides efficient forward propagation for fitness evaluation. The network supports arbitrary topologies with
 * variable numbers of hidden layers and connections.
 * 
 * <p>Key features:
 * <ul>
 * <li><strong>Dynamic topology</strong>: Supports arbitrary network structures evolved by NEAT</li>
 * <li><strong>Layer-based evaluation</strong>: Automatically computes optimal evaluation order</li>
 * <li><strong>Configurable activation</strong>: Supports any activation function for hidden and output nodes</li>
 * <li><strong>Efficient propagation</strong>: Optimized forward pass through network layers</li>
 * </ul>
 * 
 * <p>Network evaluation process:
 * <ol>
 * <li><strong>Input assignment</strong>: Input values are assigned to input nodes</li>
 * <li><strong>Layer computation</strong>: Each layer is computed in topological order</li>
 * <li><strong>Node activation</strong>: Each node applies weighted sum followed by activation function</li>
 * <li><strong>Output extraction</strong>: Output values are collected from designated output nodes</li>
 * </ol>
 * 
 * <p>Network construction workflow:
 * <ul>
 * <li><strong>Topology analysis</strong>: Network connections are analyzed to determine layer structure</li>
 * <li><strong>Layer partitioning</strong>: Nodes are organized into evaluation layers using topological sorting</li>
 * <li><strong>Connection mapping</strong>: Backward connections are precomputed for efficient evaluation</li>
 * <li><strong>Dead node removal</strong>: Unreachable nodes are excluded from computation</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create network from NEAT chromosome
 * NeatChromosome chromosome = // ... obtain from evolution
 * Set<Integer> inputNodes = Set.of(0, 1, 2);
 * Set<Integer> outputNodes = Set.of(3, 4);
 * Function<Float, Float> activation = Activations::sigmoid;
 * 
 * FeedForwardNetwork network = new FeedForwardNetwork(
 *     inputNodes, outputNodes, chromosome.getConnections(), activation
 * );
 * 
 * // Evaluate network on input data
 * Map<Integer, Float> inputs = Map.of(0, 1.0f, 1, 0.5f, 2, -0.3f);
 * Map<Integer, Float> outputs = network.compute(inputs);
 * 
 * // Extract specific outputs
 * float output1 = outputs.get(3);
 * float output2 = outputs.get(4);
 * }</pre>
 * 
 * <p>Activation function integration:
 * <ul>
 * <li><strong>Sigmoid activation</strong>: Standard logistic function for binary classification</li>
 * <li><strong>Tanh activation</strong>: Hyperbolic tangent for continuous outputs</li>
 * <li><strong>Linear activation</strong>: Identity function for regression problems</li>
 * <li><strong>Custom functions</strong>: Any Function&lt;Float, Float&gt; can be used</li>
 * </ul>
 * 
 * <p>Performance optimizations:
 * <ul>
 * <li><strong>Layer precomputation</strong>: Network layers are computed once during construction</li>
 * <li><strong>Connection mapping</strong>: Backward connections are precomputed for fast lookup</li>
 * <li><strong>Dead node elimination</strong>: Unreachable nodes are excluded from evaluation</li>
 * <li><strong>Efficient propagation</strong>: Only enabled connections participate in computation</li>
 * </ul>
 * 
 * <p>Error handling and validation:
 * <ul>
 * <li><strong>Input validation</strong>: Ensures all input nodes receive values</li>
 * <li><strong>Output validation</strong>: Verifies all output nodes produce values</li>
 * <li><strong>Topology validation</strong>: Validates network structure during construction</li>
 * <li><strong>Connection consistency</strong>: Ensures connection endpoints reference valid nodes</li>
 * </ul>
 * 
 * <p>Integration with NEAT evolution:
 * <ul>
 * <li><strong>Chromosome evaluation</strong>: Converts NEAT chromosomes to executable networks</li>
 * <li><strong>Fitness computation</strong>: Provides network output for fitness evaluation</li>
 * <li><strong>Topology evolution</strong>: Supports networks with varying structure complexity</li>
 * <li><strong>Innovation tracking</strong>: Works with networks containing historical innovations</li>
 * </ul>
 * 
 * @see NeatChromosome
 * @see Connection
 * @see Activations
 * @see NeatUtils#partitionLayersNodes
 */
public class FeedForwardNetwork {
	public static final Logger logger = LogManager.getLogger(FeedForwardNetwork.class);

	private final Set<Integer> inputNodeIndices;
	private final Set<Integer> outputNodeIndices;
	private final List<Connection> connections;

	private final List<List<Integer>> layers;
	private final Map<Integer, Set<Connection>> backwardConnections;

	private final Function<Float, Float> activationFunction;

	/**
	 * Constructs a new feed-forward network with the specified topology and activation function.
	 * 
	 * <p>The constructor analyzes the network topology, computes evaluation layers using topological
	 * sorting, and precomputes connection mappings for efficient forward propagation. The network
	 * is immediately ready for evaluation after construction.
	 * 
	 * @param _inputNodeIndices set of input node indices
	 * @param _outputNodeIndices set of output node indices
	 * @param _connections list of network connections defining the topology
	 * @param _activationFunction activation function to apply to hidden and output nodes
	 * @throws IllegalArgumentException if any parameter is null or empty
	 */
	public FeedForwardNetwork(final Set<Integer> _inputNodeIndices, final Set<Integer> _outputNodeIndices,
			final List<Connection> _connections, final Function<Float, Float> _activationFunction) {
		Validate.isTrue(CollectionUtils.isNotEmpty(_inputNodeIndices));
		Validate.isTrue(CollectionUtils.isNotEmpty(_outputNodeIndices));
		Validate.isTrue(CollectionUtils.isNotEmpty(_connections));
		Validate.notNull(_activationFunction);

		this.inputNodeIndices = _inputNodeIndices;
		this.outputNodeIndices = _outputNodeIndices;
		this.connections = _connections;
		this.activationFunction = _activationFunction;

		this.layers = NeatUtils.partitionLayersNodes(this.inputNodeIndices, this.outputNodeIndices, this.connections);
		this.backwardConnections = NeatUtils.computeBackwardConnections(this.connections);
	}

	/**
	 * Computes the network output for the given input values.
	 * 
	 * <p>This method performs forward propagation through the network, computing node activations
	 * layer by layer in topological order. Input values are assigned to input nodes, then each
	 * subsequent layer is computed by applying weighted sums and activation functions.
	 * 
	 * <p>The computation process:
	 * <ol>
	 * <li>Input values are assigned to input nodes</li>
	 * <li>For each layer (starting from first hidden layer):</li>
	 * <li>  For each node in the layer:</li>
	 * <li>    Compute weighted sum of inputs from previous layers</li>
	 * <li>    Apply activation function to the sum</li>
	 * <li>    Store the result for use in subsequent layers</li>
	 * <li>Extract and return output values from output nodes</li>
	 * </ol>
	 * 
	 * @param inputValues mapping from input node indices to their values
	 * @return mapping from output node indices to their computed values
	 * @throws IllegalArgumentException if inputValues is null, has wrong size, or missing required inputs
	 */
	public Map<Integer, Float> compute(final Map<Integer, Float> inputValues) {
		Validate.notNull(inputValues);
		Validate.isTrue(inputValues.size() == inputNodeIndices.size());

		final Map<Integer, Float> nodeValues = new HashMap<>();

		for (final Integer inputNodeIndex : inputNodeIndices) {
			Float nodeValue = inputValues.get(inputNodeIndex);
			if (nodeValue == null) {
				throw new IllegalArgumentException("Input vector missing values for input node " + inputNodeIndex);
			}
			nodeValues.put(inputNodeIndex, nodeValue);
		}

		int layerIndex = 1;
		while (layerIndex < layers.size()) {

			final List<Integer> layer = layers.get(layerIndex);

			if (CollectionUtils.isNotEmpty(layer)) {

				for (Integer nodeIndex : layer) {
					float sum = 0.0f;
					final var incomingNodes = backwardConnections.getOrDefault(nodeIndex, Set.of());
					for (final Connection incomingConnection : incomingNodes) {
						if (incomingConnection.toNodeIndex() != nodeIndex) {
							throw new IllegalStateException();
						}

						// Incoming connection may have been disabled and dangling
						if (nodeValues.containsKey(incomingConnection.fromNodeIndex())) {
							final float weight = incomingConnection.weight();
							final float incomingNodeValue = nodeValues.get(incomingConnection.fromNodeIndex());

							sum += weight * incomingNodeValue;
						}
					}
					final Float outputValue = activationFunction.apply(sum);
					nodeValues.put(nodeIndex, outputValue);
				}
			}

			layerIndex++;
		}

		final Map<Integer, Float> outputValues = new HashMap<>();
		for (final Integer outputNodeIndex : outputNodeIndices) {
			final Float value = nodeValues.get(outputNodeIndex);
			if (value == null) {
				throw new IllegalArgumentException("Missing output value for node " + outputNodeIndex);
			}
			outputValues.put(outputNodeIndex, value);
		}
		return outputValues;
	}
}