package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.InnovationManager;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.mutation.AddNode;

public class NeatChromosomeAddNodeMutationHandler extends AbstractNeatChromosomeConnectionMutationHandler<AddNode> {

	private final RandomGenerator randomGenerator;
	private final InnovationManager innovationManager;

	public NeatChromosomeAddNodeMutationHandler(final RandomGenerator _randomGenerator,
			final InnovationManager _innovationManager) {
		super(AddNode.class, _randomGenerator);
		Validate.notNull(_randomGenerator);
		Validate.notNull(_innovationManager);

		this.randomGenerator = _randomGenerator;
		this.innovationManager = _innovationManager;
	}

	@Override
	protected List<Connection> mutateConnection(final AddNode mutationPolicy, final NeatChromosome neatChromosome,
			final Connection oldConnection, final int i) {

		final List<Connection> connections = new ArrayList<>();

		final var disabledConnection = Connection.builder()
				.from(oldConnection)
				.isEnabled(false)
				.build();
		connections.add(disabledConnection);

		final int maxNodeConnectionsValue = neatChromosome.getConnections()
				.stream()
				.map(connection -> Math.max(connection.fromNodeIndex(), connection.toNodeIndex()))
				.max(Comparator.naturalOrder())
				.orElse(0);

		final int maxNodeValue = Math.max(maxNodeConnectionsValue,
				neatChromosome.getNumInputs() + neatChromosome.getNumOutputs() - 1);

		final int newNodeValue = maxNodeValue + 1;

		final int firstInnovation = innovationManager.computeNewId(oldConnection.fromNodeIndex(), newNodeValue);
		final var firstConnection = Connection.builder()
				.from(oldConnection)
				.weight(1.0f)
				.toNodeIndex(newNodeValue)
				.innovation(firstInnovation)
				.build();
		connections.add(firstConnection);

		final int secondInnovation = innovationManager.computeNewId(newNodeValue, oldConnection.toNodeIndex());
		final var secondConnection = Connection.builder()
				.from(oldConnection)
				.fromNodeIndex(newNodeValue)
				.innovation(secondInnovation)
				.build();
		connections.add(secondConnection);

		return connections;
	}

}