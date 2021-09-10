package net.bmahe.genetics4j.core.mutation.chromosome.creepmutation;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.util.DistributionUtils;

public class IntChromosomeCreepMutationHandler implements ChromosomeMutationHandler<IntChromosome> {

	private final Random random;

	public IntChromosomeCreepMutationHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof CreepMutation && chromosome instanceof IntChromosomeSpec;
	}

	@Override
	public IntChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(CreepMutation.class, mutationPolicy);
		Validate.isInstanceOf(IntChromosome.class, chromosome);

		final var intChromosome = (IntChromosome) chromosome;
		final var creepMutation = (CreepMutation) mutationPolicy;

		final var minValue = intChromosome.getMinValue();
		final var maxValue = intChromosome.getMaxValue();
		final Distribution distribution = creepMutation.distribution();

		final Supplier<Double> distributionValueSupplier = DistributionUtils
				.distributionValueSupplier(random, minValue, maxValue, distribution);

		final int[] newValues = Arrays.copyOf(intChromosome.getValues(), intChromosome.getNumAlleles());

		final int alleleFlipIndex = random.nextInt(intChromosome.getNumAlleles());
		newValues[alleleFlipIndex] += distributionValueSupplier.get();

		if (newValues[alleleFlipIndex] > maxValue) {
			newValues[alleleFlipIndex] = maxValue;
		} else if (newValues[alleleFlipIndex] < minValue) {
			newValues[alleleFlipIndex] = minValue;
		}

		return new IntChromosome(intChromosome.getSize(), intChromosome.getMinValue(), intChromosome.getMaxValue(),
				newValues);
	}
}