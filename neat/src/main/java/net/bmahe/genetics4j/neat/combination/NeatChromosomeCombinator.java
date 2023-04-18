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
import net.bmahe.genetics4j.neat.spec.combination.NeatCombination;

public class NeatChromosomeCombinator<T extends Comparable<T>> implements ChromosomeCombinator<T> {
	public static final Logger logger = LogManager.getLogger(NeatChromosomeCombinator.class);

	private final RandomGenerator randomGenerator;
	private final NeatCombination neatCombination;

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

	protected boolean shouldReEnable(final Connection chosenParent, final Connection otherParent) {
		Validate.notNull(chosenParent);
		Validate.notNull(otherParent);

		boolean shouldReEnable = false;
		if (chosenParent.isEnabled() == false && otherParent.isEnabled() == true) {
			if (randomGenerator.nextDouble() < neatCombination.reenableGeneInheritanceThresold()) {
				shouldReEnable = true;
			}
		}

		return shouldReEnable;
	}

	public NeatChromosomeCombinator(final RandomGenerator _randomGenerator, final NeatCombination _neatCombination) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_neatCombination);

		this.randomGenerator = _randomGenerator;
		this.neatCombination = _neatCombination;
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

		final NeatChromosome firstNeatChromosome = (NeatChromosome) firstChromosome;
		final NeatChromosome secondNeatChromosome = (NeatChromosome) secondChromosome;
		final Comparator<T> fitnessComparator = eaConfiguration.fitnessComparator();
		final double inheritanceThresold = neatCombination.inheritanceThresold();

		NeatChromosome bestChromosome = firstNeatChromosome;
		NeatChromosome worstChromosome = secondNeatChromosome;

		final int fitnessComparison = fitnessComparator.compare(firstParentFitness, secondParentFitness);
		if (fitnessComparison < 0) {
			bestChromosome = secondNeatChromosome;
			worstChromosome = firstNeatChromosome;
		}

		final List<Connection> combinedConnections = new ArrayList<>();
		final Map<Integer, Set<Integer>> linksCache = new HashMap<>();

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
				var original = bestConnection;
				var other = worstConnection;
				if (randomGenerator.nextDouble() < 1 - inheritanceThresold) {
					original = worstConnection;
					other = bestConnection;
				}
				if (linksCacheContainsConnection(linksCache, original) == false) {

					/**
					 * If the chosen gene is disabled but the other one is enabled, then there is a
					 * chance we will re-enable it
					 */
					final boolean isEnabled = shouldReEnable(original, other) ? true : original.isEnabled();

					final var childConnection = Connection.builder()
							.from(original)
							.isEnabled(isEnabled)
							.build();
					combinedConnections.add(childConnection);
					insertInlinksCache(linksCache, original);
				}
				indexBest++;
				indexWorst++;
			} else if (bestConnection.innovation() > worstConnection.innovation()) {

				/**
				 * If the fitnesses are equal, then we randomly inherit from the parent
				 * Otherwise, we do not inherit from the lesser gene
				 */
				if (fitnessComparison == 0 && randomGenerator.nextDouble() < 1.0 - inheritanceThresold) {
					final var original = worstConnection;
					if (linksCacheContainsConnection(linksCache, original) == false) {
						combinedConnections.add(Connection.copyOf(original));
						insertInlinksCache(linksCache, original);
					}
				}

				indexWorst++;
			} else {

				/**
				 * If the fitnesses are equal, then we randomly inherit from the parent
				 * Otherwise, we always inherit from the better gene
				 */

				if (fitnessComparison != 0 || randomGenerator.nextDouble() < inheritanceThresold) {
					if (linksCacheContainsConnection(linksCache, bestConnection) == false) {
						combinedConnections.add(Connection.copyOf(bestConnection));
						insertInlinksCache(linksCache, bestConnection);
					}
				}
				indexBest++;
			}
		}

		/*
		 * Case where the best connection has more genes. It's called excess genes
		 */
		while (indexBest < bestConnections.size()) {
			/**
			 * If the fitnesses are equal, then we randomly inherit from the parent
			 * Otherwise, we always inherit from the better gene
			 */
			if (fitnessComparison != 0 || randomGenerator.nextDouble() < inheritanceThresold) {
				final var bestConnection = bestConnections.get(indexBest);
				if (linksCacheContainsConnection(linksCache, bestConnection) == false) {
					combinedConnections.add(Connection.copyOf(bestConnection));
					insertInlinksCache(linksCache, bestConnection);
				}

			}
			indexBest++;
		}

		/*
		 * Case where the worst connection has more genes. It's called excess genes.
		 * Since we don't inherit when their fitness aren't equal, it means we can skip
		 * the excess genes from the weaker connections. However we will randomly
		 * inherit if their fitnesses are equal
		 */
		while (fitnessComparison == 0 && indexWorst < worstConnections.size()) {
			if (randomGenerator.nextDouble() < 1.0 - inheritanceThresold) {
				final var worstConnection = worstConnections.get(indexWorst);
				if (linksCacheContainsConnection(linksCache, worstConnection) == false) {
					combinedConnections.add(Connection.copyOf(worstConnection));
					insertInlinksCache(linksCache, worstConnection);
				}

			}
			indexWorst++;
		}

		return List.of(new NeatChromosome(bestChromosome.getNumInputs(),
				bestChromosome.getNumOutputs(),
				bestChromosome.getMinWeightValue(),
				bestChromosome.getMaxWeightValue(),
				combinedConnections));
	}
}