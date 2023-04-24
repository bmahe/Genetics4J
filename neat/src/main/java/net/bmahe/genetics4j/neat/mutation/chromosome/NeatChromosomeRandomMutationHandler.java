package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public class NeatChromosomeRandomMutationHandler
		extends AbstractNeatChromosomeConnectionMutationHandler<RandomMutation>
{

	public NeatChromosomeRandomMutationHandler(final RandomGenerator _randomGenerator) {
		super(RandomMutation.class, _randomGenerator);
	}

	@Override
	protected List<Connection> mutateConnection(final RandomMutation randomMutation, final NeatChromosome neatChromosome,
			final Connection oldConnection, final int i) {
		Validate.notNull(randomMutation);
		Validate.notNull(neatChromosome);
		Validate.notNull(oldConnection);

		final var connectionBuilder = Connection.builder()
				.from(oldConnection);

		final var minValue = neatChromosome.getMinWeightValue();
		final var maxValue = neatChromosome.getMaxWeightValue();

		final var randomGenerator = getRandomGenerator();

		// TODO use distribution
		float newWeight = randomGenerator.nextFloat(maxValue - minValue) + minValue;
		connectionBuilder.weight(newWeight);
		return List.of(connectionBuilder.build());
	}
}