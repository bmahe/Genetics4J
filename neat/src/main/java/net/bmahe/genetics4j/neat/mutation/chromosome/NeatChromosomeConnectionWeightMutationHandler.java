package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.List;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.util.DistributionUtils;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.NeatConnectionWeight;

public class NeatChromosomeConnectionWeightMutationHandler implements ChromosomeMutationHandler<NeatChromosome> {

	public static final Logger logger = LogManager.getLogger(NeatChromosomeConnectionWeightMutationHandler.class);

	private final RandomGenerator randomGenerator;

	public NeatChromosomeConnectionWeightMutationHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof NeatConnectionWeight && chromosome instanceof NeatChromosomeSpec;
	}

	protected float perturbateWeight(final float weight, final float disturbance, final float minValue,
			final float maxValue) {
		Validate.isTrue(minValue <= maxValue);

		float newWeight = weight + disturbance;
		if (newWeight > maxValue) {
			newWeight = maxValue;
		} else if (newWeight < minValue) {
			newWeight = minValue;
		}

		return newWeight;
	}

	protected Connection mutateConnection(final Connection connection, final double perturbationRatio,
			final Supplier<Float> distributionValueSupplier, final Supplier<Float> distributionNewValueSupplier,
			final float minValue, final float maxValue) {

		final var connectionBuilder = Connection.builder()
				.from(connection);

		float newWeight = connection.weight();
		if (randomGenerator.nextDouble() < perturbationRatio) {
			final float disturbance = distributionValueSupplier.get();
			newWeight = perturbateWeight(newWeight, disturbance, minValue, maxValue);
		} else {
			newWeight = distributionNewValueSupplier.get();
		}
		connectionBuilder.weight(newWeight);
		return connectionBuilder.build();
	}

	@Override
	public NeatChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(NeatConnectionWeight.class, mutationPolicy);
		Validate.isInstanceOf(NeatChromosome.class, chromosome);

		final var neatChromosome = (NeatChromosome) chromosome;
		final var numInputs = neatChromosome.getNumInputs();
		final var numOutputs = neatChromosome.getNumOutputs();
		final var minValue = neatChromosome.getMinWeightValue();
		final var maxValue = neatChromosome.getMaxWeightValue();

		final var neatConnectionWeight = (NeatConnectionWeight) mutationPolicy;
		final Distribution perturbationDistribution = neatConnectionWeight.perturbationDistribution();
		final double perturbationRatio = neatConnectionWeight.perturbationRatio();
		final Distribution newValuesDistribution = neatConnectionWeight.newValuesDistribution();

		final Supplier<Float> distributionValueSupplier = DistributionUtils
				.distributionFloatValueSupplier(randomGenerator, minValue, maxValue, perturbationDistribution);
		final Supplier<Float> distributionNewValueSupplier = DistributionUtils
				.distributionFloatValueSupplier(randomGenerator, minValue, maxValue, newValuesDistribution);

		final var oldConnections = neatChromosome.getConnections();
		final List<Connection> newConnections = oldConnections.stream()
				.map(connection -> mutateConnection(connection,
						perturbationRatio,
						distributionValueSupplier,
						distributionNewValueSupplier,
						minValue,
						maxValue))
				.toList();

		return new NeatChromosome(numInputs, numOutputs, minValue, maxValue, newConnections);
	}
}