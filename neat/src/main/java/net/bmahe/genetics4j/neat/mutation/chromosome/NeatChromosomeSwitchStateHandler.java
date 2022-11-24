package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.mutation.SwitchStateMutation;

public class NeatChromosomeSwitchStateHandler
		extends AbstractNeatChromosomeConnectionMutationHandler<SwitchStateMutation>
{
	public NeatChromosomeSwitchStateHandler(final RandomGenerator _randomGenerator) {
		super(SwitchStateMutation.class, _randomGenerator);
	}

	@Override
	protected List<Connection> mutateConnection(final SwitchStateMutation switchStateMutation,
			final NeatChromosome neatChromosome, final Connection oldConnection, final int i) {
		Validate.notNull(switchStateMutation);
		Validate.notNull(neatChromosome);
		Validate.notNull(oldConnection);

		final var connectionBuilder = Connection.builder()
				.from(oldConnection);
		connectionBuilder.isEnabled(!oldConnection.isEnabled());
		return List.of(connectionBuilder.build());
	}
}