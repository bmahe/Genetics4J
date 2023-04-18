package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.InnovationManager;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.AddConnection;

public class NeatChromosomeAddConnection implements ChromosomeMutationHandler<NeatChromosome> {

	public static final Logger logger = LogManager.getLogger(NeatChromosomeAddConnection.class);

	private final RandomGenerator randomGenerator;
	private final InnovationManager innovationManager;

	public NeatChromosomeAddConnection(final RandomGenerator _randomGenerator,
			final InnovationManager _innovationManager) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_innovationManager);

		this.randomGenerator = _randomGenerator;
		this.innovationManager = _innovationManager;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof AddConnection && chromosome instanceof NeatChromosomeSpec;
	}

	@Override
	public NeatChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(AddConnection.class, mutationPolicy);
		Validate.isInstanceOf(NeatChromosome.class, chromosome);

		final var neatChromosome = (NeatChromosome) chromosome;
		final var numInputs = neatChromosome.getNumInputs();
		final var numOutputs = neatChromosome.getNumOutputs();
		final var minValue = neatChromosome.getMinWeightValue();
		final var maxValue = neatChromosome.getMaxWeightValue();

		final var oldConnections = neatChromosome.getConnections();
		final List<Connection> newConnections = new ArrayList<>(oldConnections);

		final int maxNodeConnectionsValue = neatChromosome.getConnections()
				.stream()
				.map(connection -> Math.max(connection.fromNodeIndex(), connection.toNodeIndex()))
				.max(Comparator.naturalOrder())
				.orElse(0);

		final int maxNodeValue = Math.max(maxNodeConnectionsValue,
				neatChromosome.getNumInputs() + neatChromosome.getNumOutputs() - 1);

		final int fromNode = randomGenerator.nextInt(maxNodeValue + 1);
		final int toNode = randomGenerator.nextInt(maxNodeValue + 1);

		final boolean isConnectionExist = oldConnections.stream()
				.anyMatch(connection -> connection.fromNodeIndex() == fromNode && connection.toNodeIndex() == toNode);

		final boolean isFromNodeAnOutput = fromNode < numInputs + numOutputs && fromNode >= numInputs;
		final boolean isToNodeAnInput = toNode < numInputs;

		if (fromNode != toNode && isConnectionExist == false && isToNodeAnInput == false && isFromNodeAnOutput == false) {
			final int innovation = innovationManager.computeNewId(fromNode, toNode);

			final var newConnection = Connection.builder()
					.fromNodeIndex(fromNode)
					.toNodeIndex(toNode)
					.innovation(innovation)
					.weight(randomGenerator.nextFloat(minValue, maxValue))
					.isEnabled(true)
					.build();

			newConnections.add(newConnection);
		}

		return new NeatChromosome(numInputs, numOutputs, minValue, maxValue, newConnections);
	}
}