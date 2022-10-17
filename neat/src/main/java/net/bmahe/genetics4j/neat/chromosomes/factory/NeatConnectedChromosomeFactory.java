package net.bmahe.genetics4j.neat.chromosomes.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.InnovationManager;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;

public class NeatConnectedChromosomeFactory implements ChromosomeFactory<NeatChromosome> {

	private final RandomGenerator randomGenerator;
	private final InnovationManager innovationManager;

	public NeatConnectedChromosomeFactory(final RandomGenerator _randomGenerator,
			final InnovationManager _innovationManager) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_innovationManager);

		this.randomGenerator = _randomGenerator;
		this.innovationManager = _innovationManager;
	}

	@Override
	public boolean canHandle(final ChromosomeSpec chromosomeSpec) {
		return chromosomeSpec instanceof NeatChromosomeSpec;
	}

	@Override
	public NeatChromosome generate(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		final NeatChromosomeSpec neatChromosomeSpec = (NeatChromosomeSpec) chromosomeSpec;
		final int numInputs = neatChromosomeSpec.numInputs();
		final int numOutputs = neatChromosomeSpec.numOutputs();
		float minWeightValue = neatChromosomeSpec.minWeightValue();
		float maxWeightValue = neatChromosomeSpec.maxWeightValue();

		final List<Connection> connections = new ArrayList<>();
		for (int inputIndex = 0; inputIndex < numInputs; inputIndex++) {
			for (int outputIndex = numInputs; outputIndex < numInputs + numOutputs; outputIndex++) {

				final int innovation = innovationManager.computeNewId(inputIndex, outputIndex);
				final Connection connection = Connection.builder()
						.fromNodeIndex(inputIndex)
						.toNodeIndex(outputIndex)
						.innovation(innovation)
						.isEnabled(true)
						.weight(randomGenerator.nextFloat(minWeightValue, maxWeightValue))
						.build();
				connections.add(connection);
			}
		}

		return new NeatChromosome(numInputs, numOutputs, minWeightValue, maxWeightValue, connections);
	}
}