package net.bmahe.genetics4j.core.mutation.chromosome.creepmutation;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.FloatChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.util.DistributionUtils;

public class FloatChromosomeCreepMutationHandler implements ChromosomeMutationHandler<FloatChromosome> {

	private final RandomGenerator randomGenerator;

	public FloatChromosomeCreepMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof CreepMutation && chromosome instanceof FloatChromosomeSpec;
	}

	@Override
	public FloatChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(CreepMutation.class, mutationPolicy);
		Validate.isInstanceOf(FloatChromosome.class, chromosome);

		final var floatChromosome = (FloatChromosome) chromosome;
		final var creepMutation = (CreepMutation) mutationPolicy;

		final var minValue = floatChromosome.getMinValue();
		final var maxValue = floatChromosome.getMaxValue();
		final Distribution distribution = creepMutation.distribution();

		final Supplier<Float> distributionValueSupplier = DistributionUtils
				.distributionFloatValueSupplier(randomGenerator, minValue, maxValue, distribution);

		final float[] newValues = Arrays.copyOf(floatChromosome.getValues(), floatChromosome.getNumAlleles());

		final int alleleFlipIndex = randomGenerator.nextInt(floatChromosome.getNumAlleles());
		newValues[alleleFlipIndex] += distributionValueSupplier.get();

		if (newValues[alleleFlipIndex] > maxValue) {
			newValues[alleleFlipIndex] = maxValue;
		} else if (newValues[alleleFlipIndex] < minValue) {
			newValues[alleleFlipIndex] = minValue;
		}

		return new FloatChromosome(floatChromosome.getSize(),
				floatChromosome.getMinValue(),
				floatChromosome.getMaxValue(),
				newValues);
	}
}