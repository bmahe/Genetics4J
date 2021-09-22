package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.util.DistributionUtils;

public class DoubleChromosomeFactory implements ChromosomeFactory<DoubleChromosome> {

	private final RandomGenerator randomGenerator;

	public DoubleChromosomeFactory(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
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

		final var minValue = doubleChromosomeSpec.minValue();
		final var maxValue = doubleChromosomeSpec.maxValue();
		final var distribution = doubleChromosomeSpec.distribution();

		final Supplier<Double> generator = DistributionUtils
				.distributionValueSupplier(randomGenerator, minValue, maxValue, distribution);

		double[] values = new double[doubleChromosomeSpec.size()];
		for (int i = 0; i < doubleChromosomeSpec.size(); i++) {
			values[i] = generator.get();
		}

		return new DoubleChromosome(doubleChromosomeSpec.size(), doubleChromosomeSpec.minValue(),
				doubleChromosomeSpec.maxValue(), values);
	}
}