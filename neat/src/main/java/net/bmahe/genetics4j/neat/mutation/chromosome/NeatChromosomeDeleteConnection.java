package net.bmahe.genetics4j.neat.mutation.chromosome;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.mutation.chromosome.ChromosomeMutationHandler;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.mutation.MutationPolicy;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.NeatChromosomeSpec;
import net.bmahe.genetics4j.neat.spec.mutation.DeleteConnection;

public class NeatChromosomeDeleteConnection implements ChromosomeMutationHandler<NeatChromosome> {

	public static final Logger logger = LogManager.getLogger(NeatChromosomeDeleteConnection.class);

	private final RandomGenerator randomGenerator;

	public NeatChromosomeDeleteConnection(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final MutationPolicy mutationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);

		return mutationPolicy instanceof DeleteConnection && chromosome instanceof NeatChromosomeSpec;
	}

	@Override
	public NeatChromosome mutate(final MutationPolicy mutationPolicy, final Chromosome chromosome) {
		Validate.notNull(mutationPolicy);
		Validate.notNull(chromosome);
		Validate.isInstanceOf(DeleteConnection.class, mutationPolicy);
		Validate.isInstanceOf(NeatChromosome.class, chromosome);

		final var neatChromosome = (NeatChromosome) chromosome;
		final var numInputs = neatChromosome.getNumInputs();
		final var numOutputs = neatChromosome.getNumOutputs();
		final var minValue = neatChromosome.getMinWeightValue();
		final var maxValue = neatChromosome.getMaxWeightValue();

		final var oldConnections = neatChromosome.getConnections();
		final List<Connection> newConnections = new ArrayList<>(oldConnections);

		if (oldConnections.size() > 0) {
			final int connectionToDeleteIndex = randomGenerator.nextInt(oldConnections.size());
			newConnections.remove(connectionToDeleteIndex);
		}

		return new NeatChromosome(numInputs, numOutputs, minValue, maxValue, newConnections);
	}
}