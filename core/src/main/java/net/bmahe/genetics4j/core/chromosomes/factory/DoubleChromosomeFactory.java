package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;

public class DoubleChromosomeFactory implements ChromosomeFactory<DoubleChromosome> {

	private final Random random;

	public DoubleChromosomeFactory(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		return chromosomeSpec instanceof DoubleChromosomeSpec;
	}

	@Override
	public DoubleChromosome generate(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);
		Validate.isInstanceOf(DoubleChromosomeSpec.class, chromosomeSpec);

		final DoubleChromosomeSpec doubleChromosomeSpec = (DoubleChromosomeSpec) chromosomeSpec;

		final double valueRange = doubleChromosomeSpec.maxValue() - doubleChromosomeSpec.minValue();

		double[] values = new double[doubleChromosomeSpec.size()];
		for (int i = 0; i < doubleChromosomeSpec.size(); i++) {
			values[i] = doubleChromosomeSpec.minValue() + random.nextDouble() * valueRange;
		}

		return new DoubleChromosome(doubleChromosomeSpec.size(), doubleChromosomeSpec.minValue(),
				doubleChromosomeSpec.maxValue(), values);
	}

}