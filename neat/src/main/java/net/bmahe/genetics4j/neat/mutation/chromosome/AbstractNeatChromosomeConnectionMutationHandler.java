package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.ImmutableConnection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;

public abstract class AbstractNeatChromosomeConnectionMutationHandler<T>
		implements ChromosomeMutationHandler<NeatChromosome>
{
	public final Logger logger = LogManager.getLogger(this.getClass());

	private final Class<T> mutationClazz;
	private final RandomGenerator randomGenerator;

	protected abstract List<Connection> mutateConnection(final T mutationPolicy, final NeatChromosome neatChromosome,
			final Connection oldConnection, final int i);

	protected Class<T> getMutationClazz() {
		return mutationClazz;
	}

	protected RandomGenerator getRandomGenerator() {
		return randomGenerator;
	}

	public AbstractNeatChromosomeConnectionMutationHandler(final Class<T> _mutationClazz,
			final RandomGenerator _randomGenerator) {
		Validate.notNull(_mutationClazz);
		Validate.notNull(_randomGenerator);

		this.mutationClazz = _mutationClazz;
		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationClazz.isInstance(mutationPolicy) && chromosome instanceof NeatChromosomeSpec;
	}

	@Override
	public NeatChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(mutationClazz, mutationPolicy);
		Validate.isInstanceOf(NeatChromosome.class, chromosome);

		final var neatChromosome = (NeatChromosome) chromosome;

		final var minValue = neatChromosome.getMinWeightValue();
		final var maxValue = neatChromosome.getMaxWeightValue();

		final var oldConnections = neatChromosome.getConnections();
		final List<Connection> newConnections = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(oldConnections)) {

			final int alleleFlipIndex = randomGenerator.nextInt(oldConnections.size());

			int i = 0;
			/**
			 * Copy every connection prior to the alleleFlipIndex
			 */
			while (i < alleleFlipIndex) {
				final var connection = oldConnections.get(i);
				final var newConnection = ImmutableConnection.copyOf(connection);
				newConnections.add(newConnection);

				i++;
			}

			/**
			 * Pick a random weight
			 */
			final var oldConnection = oldConnections.get(i);
			final var mutatedConnection = mutateConnection(mutationClazz.cast(mutationPolicy),
					neatChromosome,
					oldConnection,
					i);
			newConnections.addAll(mutatedConnection);
			i++;

			/**
			 * Copy every connection after to the alleleFlipIndex
			 */
			while (i < oldConnections.size()) {
				final var connection = oldConnections.get(i);
				final var newConnection = ImmutableConnection.copyOf(connection);
				newConnections.add(newConnection);

				i++;
			}

		}

		final var numInputs = neatChromosome.getNumInputs();
		final var numOutputs = neatChromosome.getNumOutputs();
		return new NeatChromosome(numInputs, numOutputs, minValue, maxValue, newConnections);
	}
}