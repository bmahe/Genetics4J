package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;

public class IntChromosomeFactory implements ChromosomeFactory<IntChromosome> {

	private final RandomGenerator randomGenerator;

	public IntChromosomeFactory(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		return chromosomeSpec instanceof IntChromosomeSpec;
	}

	@Override
	public IntChromosome generate(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);
		Validate.isInstanceOf(IntChromosomeSpec.class, chromosomeSpec);

		final IntChromosomeSpec intChromosomeSpec = (IntChromosomeSpec) chromosomeSpec;

		int[] values = new int[intChromosomeSpec.size()];
		for (int i = 0; i < intChromosomeSpec.size(); i++) {
			values[i] = intChromosomeSpec.minValue()
					+ randomGenerator.nextInt(intChromosomeSpec.maxValue() - intChromosomeSpec.minValue());
		}

		return new IntChromosome(intChromosomeSpec.size(), intChromosomeSpec.minValue(), intChromosomeSpec.maxValue(),
				values);
	}

}