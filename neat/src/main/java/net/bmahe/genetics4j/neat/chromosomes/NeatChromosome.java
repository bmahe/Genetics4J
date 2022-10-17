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

public class NeatChromosome implements Chromosome {

	private final int numInputs;
	private final int numOutputs;
	private final float minWeightValue;
	private final float maxWeightValue;
	private final List<Connection> connections;

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

	@Override
	public int getNumAlleles() {
		return numInputs + numOutputs + connections.size();
	}

	public int getNumInputs() {
		return numInputs;
	}

	public int getNumOutputs() {
		return numOutputs;
	}

	public float getMinWeightValue() {
		return minWeightValue;
	}

	public float getMaxWeightValue() {
		return maxWeightValue;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public Set<Integer> getInputNodeIndices() {
		return IntStream.range(0, numInputs)
				.boxed()
				.collect(Collectors.toSet());
	}

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