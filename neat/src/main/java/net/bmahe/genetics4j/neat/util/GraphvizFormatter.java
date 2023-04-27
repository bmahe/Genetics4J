package net.bmahe.genetics4j.neat.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public class GraphvizFormatter {

	public String format(final NeatChromosome neatChromosome, final Map<Integer, String> nodeNames) {
		Validate.notNull(neatChromosome);
		Validate.notNull(nodeNames);

		final List<Connection> connections = neatChromosome.getConnections();
		final StringBuilder graphStringBuilder = new StringBuilder();

		graphStringBuilder.append("""
				digraph g {
					rankdir=LR;
					root[style="invis"]
					end[style="invis"]
				""");

		final Set<Integer> inputNodeIndices = neatChromosome.getInputNodeIndices();
		for (final Integer inputNodeIndex : inputNodeIndices) {
			graphStringBuilder.append("\troot -> ")
					.append(inputNodeIndex)
					.append(" [style=\"invis\"]")
					.append(System.lineSeparator());
		}

		final Set<Integer> outputNodeIndices = neatChromosome.getOutputNodeIndices();
		for (final Integer outputNodeIndex : outputNodeIndices) {
			graphStringBuilder.append("\t")
					.append(outputNodeIndex)
					.append(" -> end")
					.append(" [style=\"invis\"]")
					.append(System.lineSeparator());
		}

		for (final Connection connection : connections) {

			final int fromNodeIndex = connection.fromNodeIndex();
			final int toNodeIndex = connection.toNodeIndex();
			final int innovation = connection.innovation();
			final boolean enabled = connection.isEnabled();
			final float weight = connection.weight();

			final StringBuilder connectionStrBuilder = new StringBuilder();
			connectionStrBuilder.append(fromNodeIndex);
			connectionStrBuilder.append(" -> ");
			connectionStrBuilder.append(toNodeIndex);
			connectionStrBuilder.append(" [ ");
			if (enabled == false) {
				connectionStrBuilder.append("style=\"dashed\", ");
			}
			connectionStrBuilder.append(String.format("label=\"innovation=%d, weight=%.03f\"", innovation, weight));
			connectionStrBuilder.append(" ] ");
			connectionStrBuilder.append(System.lineSeparator());

			graphStringBuilder.append("\t");
			graphStringBuilder.append(connectionStrBuilder.toString());
		}

		for (final Entry<Integer, String> nodeNamesEntry : nodeNames.entrySet()) {

			graphStringBuilder.append("\t")
					.append(Integer.toString(nodeNamesEntry.getKey()))
					.append("[ ")
					.append("label=\"")
					.append(nodeNamesEntry.getValue())
					.append("\"")
					.append(" ]")
					.append(System.lineSeparator());
		}

		graphStringBuilder.append("}");
		return graphStringBuilder.toString();
	}
}