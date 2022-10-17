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

public class FeedForwardNetwork {
	public static final Logger logger = LogManager.getLogger(FeedForwardNetwork.class);

	private final Set<Integer> inputNodeIndices;
	private final Set<Integer> outputNodeIndices;
	private final List<Connection> connections;

	private final List<List<Integer>> layers;
	private final Map<Integer, Set<Connection>> backwardConnections;

	private final Function<Float, Float> activationFunction;

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