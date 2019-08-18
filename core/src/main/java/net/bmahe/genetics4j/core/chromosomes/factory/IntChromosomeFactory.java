package net.bmahe.genetics4j.core.chromosomes.factory;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;

public class IntChromosomeFactory implements ChromosomeFactory<IntChromosome> {

	private final Random random;

	public IntChromosomeFactory(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
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
					+ random.nextInt(intChromosomeSpec.maxValue() - intChromosomeSpec.minValue());
		}

		return new IntChromosome(intChromosomeSpec.size(), intChromosomeSpec.minValue(), intChromosomeSpec.maxValue(),
				values);
	}

}