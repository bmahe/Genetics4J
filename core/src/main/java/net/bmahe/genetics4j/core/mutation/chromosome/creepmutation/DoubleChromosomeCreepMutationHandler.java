package net.bmahe.genetics4j.core.mutation.chromosome.creepmutation;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.util.DistributionUtils;

public class DoubleChromosomeCreepMutationHandler implements ChromosomeMutationHandler<DoubleChromosome> {

	private final RandomGenerator randomGenerator;

	public DoubleChromosomeCreepMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof CreepMutation && chromosome instanceof DoubleChromosomeSpec;
	}

	@Override
	public DoubleChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(CreepMutation.class, mutationPolicy);
		Validate.isInstanceOf(DoubleChromosome.class, chromosome);

		final var doubleChromosome = (DoubleChromosome) chromosome;
		final var creepMutation = (CreepMutation) mutationPolicy;

		final var minValue = doubleChromosome.getMinValue();
		final var maxValue = doubleChromosome.getMaxValue();
		final Distribution distribution = creepMutation.distribution();

		final Supplier<Double> distributionValueSupplier = DistributionUtils
				.distributionValueSupplier(randomGenerator, minValue, maxValue, distribution);

		final double[] newValues = Arrays.copyOf(doubleChromosome.getValues(), doubleChromosome.getNumAlleles());

		final int alleleFlipIndex = randomGenerator.nextInt(doubleChromosome.getNumAlleles());
		newValues[alleleFlipIndex] += distributionValueSupplier.get();

		if (newValues[alleleFlipIndex] > maxValue) {
			newValues[alleleFlipIndex] = maxValue;
		} else if (newValues[alleleFlipIndex] < minValue) {
			newValues[alleleFlipIndex] = minValue;
		}

		return new DoubleChromosome(doubleChromosome.getSize(), doubleChromosome.getMinValue(),
				doubleChromosome.getMaxValue(), newValues);
	}
}