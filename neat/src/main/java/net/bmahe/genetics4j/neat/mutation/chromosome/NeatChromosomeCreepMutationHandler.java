package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.List;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.statistics.distributions.Distribution;
import net.bmahe.genetics4j.core.util.DistributionUtils;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public class NeatChromosomeCreepMutationHandler extends AbstractNeatChromosomeConnectionMutationHandler<CreepMutation> {

	public NeatChromosomeCreepMutationHandler(final RandomGenerator _randomGenerator) {
		super(CreepMutation.class, _randomGenerator);
	}

	@Override
	protected List<Connection> mutateConnection(final CreepMutation creepMutation, final NeatChromosome neatChromosome,
			final Connection oldConnection, final int i) {
		Validate.notNull(creepMutation);
		Validate.notNull(neatChromosome);
		Validate.notNull(oldConnection);

		final var connectionBuilder = Connection.builder()
				.from(oldConnection);

		final var minValue = neatChromosome.getMinWeightValue();
		final var maxValue = neatChromosome.getMaxWeightValue();

		final var randomGenerator = getRandomGenerator();
		final Distribution distribution = creepMutation.distribution();

		final Supplier<Float> distributionValueSupplier = DistributionUtils
				.distributionFloatValueSupplier(randomGenerator, minValue, maxValue, distribution);

		float newWeight = neatChromosome.getConnections()
				.get(i)
				.weight();
		newWeight += distributionValueSupplier.get();
		if (newWeight > maxValue) {
			newWeight = maxValue;
		} else if (newWeight < minValue) {
			newWeight = minValue;
		}
		connectionBuilder.weight(newWeight);

		return List.of(connectionBuilder.build());
	}
}