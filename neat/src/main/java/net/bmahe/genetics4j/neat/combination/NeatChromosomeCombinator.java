package net.bmahe.genetics4j.neat.combination;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public class NeatChromosomeCombinator<T extends Comparable<T>> implements ChromosomeCombinator<T> {
	public static final Logger logger = LogManager.getLogger(NeatChromosomeCombinator.class);

	private final RandomGenerator randomGenerator;

	private boolean linksCacheContainsConnection(final Map<Integer, Set<Integer>> linksCache,
			final Connection connection) {
		Validate.notNull(linksCache);
		Validate.notNull(connection);

		final int fromNodeIndex = connection.fromNodeIndex();
		final int toNodeIndex = connection.toNodeIndex();

		return linksCache.containsKey(fromNodeIndex) == true && linksCache.get(fromNodeIndex)
				.contains(toNodeIndex) == true;
	}

	private void insertInlinksCache(final Map<Integer, Set<Integer>> linksCache, final Connection connection) {
		Validate.notNull(linksCache);
		Validate.notNull(connection);

		final int fromNodeIndex = connection.fromNodeIndex();
		final int toNodeIndex = connection.toNodeIndex();

		linksCache.computeIfAbsent(fromNodeIndex, k -> new HashSet<>())
				.add(toNodeIndex);
	}

	public NeatChromosomeCombinator(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public List<Chromosome> combine(final AbstractEAConfiguration<T> eaConfiguration, final Chromosome firstChromosome,
			final T firstParentFitness, final Chromosome secondChromosome, final T secondParentFitness) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(firstChromosome);
		Validate.notNull(firstParentFitness);
		Validate.isInstanceOf(NeatChromosome.class, firstChromosome);
		Validate.notNull(secondChromosome);
		Validate.notNull(secondParentFitness);
		Validate.isInstanceOf(NeatChromosome.class, secondChromosome);

		if (firstChromosome instanceof NeatChromosome firstNeatChromosome
				&& secondChromosome instanceof NeatChromosome secondNeatChromosome) {

			final Comparator<T> fitnessComparator = eaConfiguration.fitnessComparator();

			NeatChromosome bestChromosome = firstNeatChromosome;
			T bestFitness = firstParentFitness;
			NeatChromosome worstChromosome = secondNeatChromosome;
			T worstFitness = secondParentFitness;

			final List<Connection> combinedConnections = new ArrayList<>();
			final Map<Integer, Set<Integer>> linksCache = new HashMap<>();

			if (fitnessComparator.compare(bestFitness, worstFitness) == 0) {

				final var bestConnections = bestChromosome.getConnections();
				final var worstConnections = worstChromosome.getConnections();

				int indexBest = 0;
				int indexWorst = 0;

				while (indexBest < bestConnections.size() && indexWorst < worstConnections.size()) {

					final var bestConnection = bestConnections.get(indexBest);
					final var worstConnection = worstConnections.get(indexWorst);

					if (bestConnection.innovation() == worstConnection.innovation()) {
						/**
						 * If innovation is the same, we pick the connection randomly
						 */
						final var original = randomGenerator.nextDouble() < 0.5 ? bestConnection : worstConnection;
						if (linksCacheContainsConnection(linksCache, original) == false) {
							combinedConnections.add(Connection.copyOf(original));
							insertInlinksCache(linksCache, original);
						}
						indexBest++;
						indexWorst++;
					} else if (bestConnection.innovation() > worstConnection.innovation()) {

						if (randomGenerator.nextDouble() < 0.5) {
							final var original = worstConnection;
							if (linksCacheContainsConnection(linksCache, original) == false) {
								combinedConnections.add(Connection.copyOf(original));
								insertInlinksCache(linksCache, original);
							}
						}

						indexWorst++;
					} else {
						if (randomGenerator.nextDouble() < 0.5) {
							if (linksCacheContainsConnection(linksCache, bestConnection) == false) {
								combinedConnections.add(Connection.copyOf(bestConnection));
								insertInlinksCache(linksCache, bestConnection);
							}
						}
						indexBest++;
					}
				}

				while (indexBest < bestConnections.size()) {
					if (randomGenerator.nextDouble() < 0.5) {
						final var bestConnection = bestConnections.get(indexBest);
						if (linksCacheContainsConnection(linksCache, bestConnection) == false) {
							combinedConnections.add(Connection.copyOf(bestConnection));
							insertInlinksCache(linksCache, bestConnection);
						}

					}
					indexBest++;
				}

				while (indexWorst < worstConnections.size()) {
					if (randomGenerator.nextDouble() < 0.5) {
						final var worstConnection = worstConnections.get(indexWorst);
						if (linksCacheContainsConnection(linksCache, worstConnection) == false) {
							combinedConnections.add(Connection.copyOf(worstConnection));
							insertInlinksCache(linksCache, worstConnection);
						}

					}
					indexWorst++;
				}

			} else {

				if (fitnessComparator.compare(firstParentFitness, secondParentFitness) < 0) {
					bestChromosome = secondNeatChromosome;
					bestFitness = secondParentFitness;
					worstChromosome = firstNeatChromosome;
					worstFitness = firstParentFitness;
				}

				final var bestConnections = bestChromosome.getConnections();
				final var worstConnections = worstChromosome.getConnections();

				int indexBest = 0;
				int indexWorst = 0;

				while (indexBest < bestConnections.size() && indexWorst < worstConnections.size()) {

					final var bestConnection = bestConnections.get(indexBest);
					final var worstConnection = worstConnections.get(indexWorst);

					if (bestConnection.innovation() == worstConnection.innovation()) {
						/**
						 * If innovation is the same, we pick the connection randomly
						 */
						final var original = randomGenerator.nextDouble() < 0.5 ? bestConnection : worstConnection;
						if (linksCacheContainsConnection(linksCache, original) == false) {
							combinedConnections.add(Connection.copyOf(original));
							insertInlinksCache(linksCache, original);
						}

						indexBest++;
						indexWorst++;
					} else if (bestConnection.innovation() > worstConnection.innovation()) {

						/**
						 * If worstConnection is missing in bestConnection, we ignore it
						 */

						indexWorst++;
					} else {
						/**
						 * Only bestConnection has that innovation. So we keep it
						 */
						if (linksCacheContainsConnection(linksCache, bestConnection) == false) {
							combinedConnections.add(Connection.copyOf(bestConnection));
							insertInlinksCache(linksCache, bestConnection);
						}

						indexBest++;
					}
				}

				while (indexBest < bestConnections.size()) {
					final var bestConnection = bestConnections.get(indexBest);
					if (linksCacheContainsConnection(linksCache, bestConnection) == false) {
						combinedConnections.add(Connection.copyOf(bestConnection));
						insertInlinksCache(linksCache, bestConnection);
					}

					indexBest++;
				}

			}

			return List.of(new NeatChromosome(bestChromosome.getNumInputs(),
					bestChromosome.getNumOutputs(),
					bestChromosome.getMinWeightValue(),
					bestChromosome.getMaxWeightValue(),
					combinedConnections));
		}

		throw new IllegalStateException("Cannot process chromosomes " + firstChromosome + " or " + secondChromosome);
	}

}