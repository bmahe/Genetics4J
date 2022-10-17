package net.bmahe.genetics4j.neat.chromosomes.factory;

import java.util.List;

import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;

public class NeatEmptyChromosomeFactory implements ChromosomeFactory<NeatChromosome> {

	@Override
	public boolean canHandle(ChromosomeSpec chromosomeSpec) {
		return chromosomeSpec instanceof NeatChromosomeSpec;
	}

	@Override
	public NeatChromosome generate(ChromosomeSpec chromosomeSpec) {

		final NeatChromosomeSpec neatChromosomeSpec = (NeatChromosomeSpec) chromosomeSpec;
		final int numInputs = neatChromosomeSpec.numInputs();
		final int numOutputs = neatChromosomeSpec.numOutputs();
		float minWeightValue = neatChromosomeSpec.minWeightValue();
		float maxWeightValue = neatChromosomeSpec.maxWeightValue();

		return new NeatChromosome(numInputs, numOutputs, minWeightValue, maxWeightValue, List.of());
	}
}