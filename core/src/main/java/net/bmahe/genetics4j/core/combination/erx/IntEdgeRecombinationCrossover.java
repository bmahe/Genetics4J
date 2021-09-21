package net.bmahe.genetics4j.core.combination.erx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;

public class IntEdgeRecombinationCrossover implements ChromosomeCombinator {

	private final RandomGenerator randomGenerator;

	public IntEdgeRecombinationCrossover(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	protected void addEdges(final Map<Integer, Set<Integer>> edgeMap, final int[] chromosomeValues) {
		Validate.notNull(edgeMap);
		Validate.notNull(chromosomeValues);

		for (int i = 0; i < chromosomeValues.length; i++) {
			final int j = chromosomeValues[i];

			// Add city before
			if (i > 0) {
				final int cityBefore = chromosomeValues[i - 1];
				final Set<Integer> cities = edgeMap.computeIfAbsent(cityBefore, k -> new HashSet<Integer>());
				cities.add(j);
			}

			// Add city after
			if (i < chromosomeValues.length - 1) {
				final int cityAfter = chromosomeValues[i + 1];
				final Set<Integer> cities = edgeMap.computeIfAbsent(cityAfter, k -> new HashSet<Integer>());
				cities.add(j);
			}
		}

		final Set<Integer> lastCities = edgeMap.computeIfAbsent(chromosomeValues[chromosomeValues.length - 1],
				k -> new HashSet<Integer>());
		lastCities.add(chromosomeValues[0]);

		final Set<Integer> firstCities = edgeMap.computeIfAbsent(chromosomeValues[0], k -> new HashSet<Integer>());
		firstCities.add(chromosomeValues[chromosomeValues.length - 1]);
	}

	protected Optional<Integer> cityWithSmallestEdgeList(final Map<Integer, Set<Integer>> edgeMap) {
		Validate.notNull(edgeMap);

		int citySmallestEdgeList = -1;
		int smallestEdgeListSize = Integer.MAX_VALUE;

		for (final Entry<Integer, Set<Integer>> entry : edgeMap.entrySet()) {
			final Integer city = entry.getKey();
			final Set<Integer> edgeList = entry.getValue();

			if (edgeList.size() < smallestEdgeListSize) {
				citySmallestEdgeList = city;
				smallestEdgeListSize = edgeList.size();
			}
		}

		return citySmallestEdgeList > -1 ? Optional.of(citySmallestEdgeList) : Optional.empty();
	}

	protected Optional<Integer> cityWithSmallestEdgeList(final Map<Integer, Set<Integer>> edgeMap,
			final Set<Integer> candidates) {
		Validate.notNull(edgeMap);

		int citySmallestEdgeList = -1;
		int smallestEdgeListSize = Integer.MAX_VALUE;

		for (final Integer candidate : candidates) {
			if (edgeMap.containsKey(candidate)) {
				final Set<Integer> edgeList = edgeMap.get(candidate);

				if (edgeList.size() < smallestEdgeListSize) {
					citySmallestEdgeList = candidate;
					smallestEdgeListSize = edgeList.size();
				}
			}
		}

		return citySmallestEdgeList > -1 ? Optional.of(citySmallestEdgeList) : Optional.empty();
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(IntChromosome.class, chromosome1);
		Validate.isInstanceOf(IntChromosome.class, chromosome2);

		final IntChromosome intChromosome1 = (IntChromosome) chromosome1;
		Validate.isTrue(intChromosome1.getNumAlleles() > 2);
		final int[] chromosome1Values = intChromosome1.getValues();

		final IntChromosome intChromosome2 = (IntChromosome) chromosome2;
		final int[] chromosome2Values = intChromosome2.getValues();

		Validate.isTrue(intChromosome2.getNumAlleles() > 2);
		Validate.isTrue(intChromosome1.getNumAlleles() == intChromosome2.getNumAlleles());

		final Map<Integer, Set<Integer>> edgeMap = new HashMap<>();
		addEdges(edgeMap, chromosome1Values);
		addEdges(edgeMap, chromosome2Values);

		int[] chromosome = new int[chromosome1.getNumAlleles()];
		int currentCity = randomGenerator.nextInt(chromosome1Values.length);
		int currentIndex = 0;
		final Set<Integer> citiesVisited = new HashSet<>();
		while (edgeMap.size() > 0) {

			chromosome[currentIndex] = currentCity;
			final Set<Integer> nextCities = edgeMap.get(currentCity);
			edgeMap.remove(currentCity);
			citiesVisited.add(currentCity);
			currentIndex++;

			final Optional<Integer> cityWithSmallestEdgeList = cityWithSmallestEdgeList(edgeMap, nextCities);

			if (cityWithSmallestEdgeList.isPresent()) {
				currentCity = cityWithSmallestEdgeList.get();
			} else {
				final Set<Integer> citiesSet = edgeMap.keySet();
				if (citiesSet.size() == 1) {
					currentCity = citiesSet.iterator().next();
				} else if (citiesSet.size() > 0) {
					currentCity = citiesSet.stream().skip(randomGenerator.nextInt(citiesSet.size() - 1)).findFirst().get();
				}
			}
		}

		if (currentIndex < chromosome1.getNumAlleles()) {
			final Set<Integer> remainingValues = new HashSet<>();
			for (int i : chromosome1Values) {
				remainingValues.add(i);
			}
			for (int i : chromosome2Values) {
				remainingValues.add(i);
			}
			remainingValues.removeAll(citiesVisited);
			for (Integer remainingCity : remainingValues) {
				chromosome[currentIndex++] = remainingCity;

			}
		}

		return List.of(new IntChromosome(chromosome1.getNumAlleles(), intChromosome1.getMinValue(),
				intChromosome1.getMaxValue(), chromosome));
	}
}