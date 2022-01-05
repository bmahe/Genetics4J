package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.FloatChromosomeSpec;
import net.bmahe.genetics4j.core.util.DistributionUtils;

public class FloatChromosomeFactory implements ChromosomeFactory<FloatChromosome> {

	private final RandomGenerator randomGenerator;

	public FloatChromosomeFactory(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);

		return chromosomeSpec instanceof FloatChromosomeSpec;
	}

	@Override
	public FloatChromosome generate(final ChromosomeSpec chromosomeSpec) {
		Validate.notNull(chromosomeSpec);
		Validate.isInstanceOf(FloatChromosomeSpec.class, chromosomeSpec);

		final FloatChromosomeSpec floatChromosomeSpec = (FloatChromosomeSpec) chromosomeSpec;

		final var minValue = floatChromosomeSpec.minValue();
		final var maxValue = floatChromosomeSpec.maxValue();
		final var distribution = floatChromosomeSpec.distribution();

		final Supplier<Float> generator = DistributionUtils
				.distributionFloatValueSupplier(randomGenerator, minValue, maxValue, distribution);

		float[] values = new float[floatChromosomeSpec.size()];
		for (int i = 0; i < floatChromosomeSpec.size(); i++) {
			values[i] = generator.get();
		}

		return new FloatChromosome(floatChromosomeSpec.size(),
				floatChromosomeSpec.minValue(),
				floatChromosomeSpec.maxValue(),
				values);
	}
}