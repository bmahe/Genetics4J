package net.bmahe.genetics4j.neat;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.random.RandomGenerator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

public class NeatUtils {

	private NeatUtils() {
	}

	/**
	 * Working backward from the output nodes, we identify the nodes that did not
	 * get visited as dead nodes
	 * 
	 * @param connections
	 * @param forwardConnections
	 * @param backwardConnections
	 * @param outputNodeIndices
	 * @return
	 */
	public static Set<Integer> computeDeadNodes(final List<Connection> connections,
			final Map<Integer, Set<Integer>> forwardConnections, final Map<Integer, Set<Integer>> backwardConnections,
			final Set<Integer> outputNodeIndices) {
		Validate.notNull(connections);

		final Set<Integer> deadNodes = new HashSet<>();
		for (final Connection connection : connections) {
			deadNodes.add(connection.fromNodeIndex());
			deadNodes.add(connection.toNodeIndex());
		}
		deadNodes.removeAll(outputNodeIndices);

		final Set<Integer> visited = new HashSet<>();
		final Deque<Integer> toVisit = new ArrayDeque<>(outputNodeIndices);
		while (toVisit.size() > 0) {
			final Integer currentNode = toVisit.poll();

			deadNodes.remove(currentNode);
			if (visited.contains(currentNode) == false) {

				visited.add(currentNode);

				final var next = backwardConnections.getOrDefault(currentNode, Set.of());
				if (next.size() > 0) {
					toVisit.addAll(next);
				}
			}
		}

		return deadNodes;
	}

	public static Map<Integer, Set<Integer>> computeForwardLinks(final List<Connection> connections) {
		Validate.notNull(connections);

		final Map<Integer, Set<Integer>> forwardConnections = new HashMap<>();
		for (final Connection connection : connections) {
			final var fromNodeIndex = connection.fromNodeIndex();
			final var toNodeIndex = connection.toNodeIndex();

			if (connection.isEnabled()) {
				final var toNodes = forwardConnections.computeIfAbsent(fromNodeIndex, k -> new HashSet<>());

				if (toNodes.add(toNodeIndex) == false) {
					throw new IllegalArgumentException(
							"Found duplicate entries for nodes defined in connection " + connection);
				}
			}
		}

		return forwardConnections;
	}

	public static Map<Integer, Set<Integer>> computeBackwardLinks(final List<Connection> connections) {
		Validate.notNull(connections);

		final Map<Integer, Set<Integer>> backwardConnections = new HashMap<>();
		for (final Connection connection : connections) {
			final var fromNodeIndex = connection.fromNodeIndex();
			final var toNodeIndex = connection.toNodeIndex();

			if (connection.isEnabled()) {
				final var fromNodes = backwardConnections.computeIfAbsent(toNodeIndex, k -> new HashSet<>());

				if (fromNodes.add(fromNodeIndex) == false) {
					throw new IllegalArgumentException(
							"Found duplicate entries for nodes defined in connection " + connection);
				}
			}
		}
		return backwardConnections;
	}

	public static Map<Integer, Set<Connection>> computeBackwardConnections(final List<Connection> connections) {
		Validate.notNull(connections);

		final Map<Integer, Set<Connection>> backwardConnections = new HashMap<>();
		for (final Connection connection : connections) {
			final var toNodeIndex = connection.toNodeIndex();

			if (connection.isEnabled()) {
				final var fromConnections = backwardConnections.computeIfAbsent(toNodeIndex, k -> new HashSet<>());

				if (fromConnections.stream()
						.anyMatch(existingConnection -> existingConnection.fromNodeIndex() == connection.fromNodeIndex())) {
					throw new IllegalArgumentException(
							"Found duplicate entries for nodes defined in connection " + connection);
				}
				fromConnections.add(connection);
			}
		}
		return backwardConnections;
	}

	public static List<List<Integer>> partitionLayersNodes(final Set<Integer> inputNodeIndices,
			final Set<Integer> outputNodeIndices, final List<Connection> connections) {
		Validate.isTrue(CollectionUtils.isNotEmpty(inputNodeIndices));
		Validate.isTrue(CollectionUtils.isNotEmpty(outputNodeIndices));
		Validate.isTrue(CollectionUtils.isNotEmpty(connections));

		final Map<Integer, Set<Integer>> forwardConnections = computeForwardLinks(connections);
		final Map<Integer, Set<Integer>> backwardConnections = computeBackwardLinks(connections);

		// Is it useful? If it's connected to the input node, it's not dead
		final var deadNodes = computeDeadNodes(connections, forwardConnections, backwardConnections, outputNodeIndices);

		final Set<Integer> processedSet = new HashSet<>();
		final List<List<Integer>> layers = new ArrayList<>();
		processedSet.addAll(inputNodeIndices);
		layers.add(new ArrayList<>(inputNodeIndices));

		boolean done = false;
		while (done == false) {
			final List<Integer> layer = new ArrayList<>();

			final Set<Integer> layerCandidates = new HashSet<>();
			for (final Entry<Integer, Set<Integer>> entry : forwardConnections.entrySet()) {
				final var key = entry.getKey();
				final var values = entry.getValue();

				if (processedSet.contains(key) == true) {
					for (final Integer candidate : values) {
						if (deadNodes.contains(candidate) == false && processedSet.contains(candidate) == false
								&& outputNodeIndices.contains(candidate) == false) {
							layerCandidates.add(candidate);
						}
					}
				}
			}

			/**
			 * We need to ensure that all the nodes pointed at the candidate are either a
			 * dead node (and we don't care) or is already in the processedSet
			 */
			for (final Integer candidate : layerCandidates) {
				final var backwardLinks = backwardConnections.getOrDefault(candidate, Set.of());

				final boolean allBackwardInEndSet = backwardLinks.stream()
						.allMatch(next -> processedSet.contains(next) || deadNodes.contains(next));

				if (allBackwardInEndSet) {
					layer.add(candidate);
				}
			}

			if (layer.size() == 0) {
				done = true;
				layer.addAll(outputNodeIndices);
			} else {
				processedSet.addAll(layer);
			}
			layers.add(layer);
		}
		return layers;
	}

	public static float compatibilityDistance(final List<Connection> firstConnections,
			final List<Connection> secondConnections, final float c1, final float c2, final float c3) {
		if (firstConnections == null || secondConnections == null) {
			return Float.MAX_VALUE;
		}

		/**
		 * Both connections are expected to already be sorted
		 */

		final int maxConnectionSize = Math.max(firstConnections.size(), secondConnections.size());
		final float n = maxConnectionSize < 20 ? 1.0f : maxConnectionSize;

		int disjointGenes = 0;

		float sumWeightDifference = 0;
		int numMatchingGenes = 0;

		int indexFirst = 0;
		int indexSecond = 0;

		while (indexFirst < firstConnections.size() && indexSecond < secondConnections.size()) {

			final Connection firstConnection = firstConnections.get(indexFirst);
			final int firstInnovation = firstConnection.innovation();

			final Connection secondConnection = secondConnections.get(indexSecond);
			final int secondInnovation = secondConnection.innovation();

			if (firstInnovation == secondInnovation) {
				sumWeightDifference += Math.abs(secondConnection.weight() - firstConnection.weight());
				numMatchingGenes++;

				indexFirst++;
				indexSecond++;
			} else {

				disjointGenes++;

				if (firstInnovation < secondInnovation) {
					indexFirst++;
				} else {
					indexSecond++;
				}
			}
		}

		int excessGenes = 0;
		/**
		 * We have consumed all elements from secondConnections and thus have their
		 * remaining difference as excess genes
		 */
		if (indexFirst < firstConnections.size()) {
			excessGenes += firstConnections.size() - indexSecond;
		} else if (indexSecond < secondConnections.size()) {
			excessGenes += secondConnections.size() - indexFirst;
		}

		final float averageWeightDifference = sumWeightDifference / Math.max(1, numMatchingGenes);

		return (c1 * excessGenes) / n + (c2 * disjointGenes) / n + c3 * averageWeightDifference;
	}

	public static float compatibilityDistance(final Genotype genotype1, final Genotype genotype2,
			final int chromosomeIndex, final float c1, final float c2, final float c3) {
		Validate.notNull(genotype1);
		Validate.notNull(genotype2);
		Validate.isTrue(chromosomeIndex >= 0);
		Validate.isTrue(chromosomeIndex < genotype1.getSize());
		Validate.isTrue(chromosomeIndex < genotype2.getSize());

		final var neatChromosome1 = genotype1.getChromosome(chromosomeIndex, NeatChromosome.class);
		final var connections1 = neatChromosome1.getConnections();

		final var neatChromosome2 = genotype2.getChromosome(chromosomeIndex, NeatChromosome.class);
		final var connections2 = neatChromosome2.getConnections();

		return compatibilityDistance(connections1, connections2, c1, c2, c3);
	}

	public static <T extends Comparable<T>> List<Species<T>> speciate(final RandomGenerator random,
			final SpeciesIdGenerator speciesIdGenerator, final List<Species<T>> seedSpecies,
			final Population<T> population, final BiPredicate<Individual<T>, Individual<T>> speciesPredicate) {
		Validate.notNull(random);
		Validate.notNull(speciesIdGenerator);
		Validate.notNull(seedSpecies);
		Validate.notNull(population);
		Validate.notNull(speciesPredicate);

		final List<Species<T>> species = new ArrayList<>();

		for (final Species<T> speciesIterator : seedSpecies) {
			final var speciesId = speciesIterator.getId();
			final int numMembers = speciesIterator.getNumMembers();
			if (numMembers > 0) {
				final int randomIndex = random.nextInt(numMembers);
				final var newAncestors = List.of(speciesIterator.getMembers()
						.get(randomIndex));
				final var newSpecies = new Species<>(speciesId, newAncestors);
				species.add(newSpecies);
			}
		}

		for (final Individual<T> individual : population) {

			boolean existingSpeciesFound = false;
			int currentSpeciesIndex = 0;
			while (existingSpeciesFound == false && currentSpeciesIndex < species.size()) {

				final var currentSpecies = species.get(currentSpeciesIndex);

				final boolean anyAncestorMatch = currentSpecies.getAncestors()
						.stream()
						.anyMatch(candidate -> speciesPredicate.test(individual, candidate));

				final boolean anyMemberMatch = currentSpecies.getMembers()
						.stream()
						.anyMatch(candidate -> speciesPredicate.test(individual, candidate));

				if (anyAncestorMatch || anyMemberMatch) {
					currentSpecies.addMember(individual);
					existingSpeciesFound = true;
				} else {
					currentSpeciesIndex++;
				}
			}

			if (existingSpeciesFound == false) {
				final int newSpeciesId = speciesIdGenerator.computeNewId();
				final var newSpecies = new Species<T>(newSpeciesId, List.of());
				newSpecies.addMember(individual);
				species.add(newSpecies);
			}
		}

		return species.stream()
				.filter(sp -> sp.getNumMembers() > 0)
				.toList();
	}
}