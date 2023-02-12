package net.bmahe.genetics4j.neat.chromosomes.factory;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;

public class NeatEmptyChromosomeFactory implements ChromosomeFactory<NeatChromosome> {

	@Override
	public boolean canHandle(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		return chromosomeSpec instanceof NeatChromosomeSpec;
	}

	@Override
	public NeatChromosome generate(ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);
		Validate.isInstanceOf(NeatChromosomeSpec.class, chromosomeSpec);

		final NeatChromosomeSpec neatChromosomeSpec = (NeatChromosomeSpec) chromosomeSpec;
		final int numInputs = neatChromosomeSpec.numInputs();
		final int numOutputs = neatChromosomeSpec.numOutputs();
		float minWeightValue = neatChromosomeSpec.minWeightValue();
		float maxWeightValue = neatChromosomeSpec.maxWeightValue();

		return new NeatChromosome(numInputs, numOutputs, minWeightValue, maxWeightValue, List.of());
	}
}