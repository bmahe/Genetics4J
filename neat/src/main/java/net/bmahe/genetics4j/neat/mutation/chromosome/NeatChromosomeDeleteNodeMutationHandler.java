package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.DeleteNode;

public class NeatChromosomeDeleteNodeMutationHandler implements ChromosomeMutationHandler<NeatChromosome> {

	public static final Logger logger = LogManager.getLogger(NeatChromosomeDeleteNodeMutationHandler.class);

	private final RandomGenerator randomGenerator;

	public NeatChromosomeDeleteNodeMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof DeleteNode && chromosome instanceof NeatChromosomeSpec;
	}

	@Override
	public NeatChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(DeleteNode.class, mutationPolicy);
		Validate.isInstanceOf(NeatChromosome.class, chromosome);

		final var neatChromosome = (NeatChromosome) chromosome;
		final var numInputs = neatChromosome.getNumInputs();
		final var numOutputs = neatChromosome.getNumOutputs();
		final var minValue = neatChromosome.getMinWeightValue();
		final var maxValue = neatChromosome.getMaxWeightValue();

		final var oldConnections = neatChromosome.getConnections();

		final Set<Integer> inoutNodes = new HashSet<>();
		inoutNodes.addAll(neatChromosome.getInputNodeIndices());
		inoutNodes.addAll(neatChromosome.getOutputNodeIndices());

		final List<Integer> allNodeValues = neatChromosome.getConnections()
				.stream()
				.flatMap(connection -> Stream.of(connection.fromNodeIndex(), connection.toNodeIndex()))
				.filter(nodeIndex -> inoutNodes.contains(nodeIndex) == false)
				.toList();

		final Set<Integer> nodeValues = Set.copyOf(allNodeValues);

		final List<Connection> newConnections = switch (nodeValues.size()) {
			case 0 -> new ArrayList<>(oldConnections);
			default -> {
				final int nodeIndexToRemove = nodeValues.size() > 1 ? randomGenerator.nextInt(nodeValues.size() - 1) : 0;

				final int nodeValueToRemove = nodeValues.stream()
						.skip(nodeIndexToRemove)
						.findFirst()
						.get();

				yield oldConnections.stream()
						.filter(connection -> connection.fromNodeIndex() != nodeValueToRemove
								&& connection.toNodeIndex() != nodeValueToRemove)
						.toList();
			}
		};

		return new NeatChromosome(numInputs, numOutputs, minValue, maxValue, newConnections);
	}
}