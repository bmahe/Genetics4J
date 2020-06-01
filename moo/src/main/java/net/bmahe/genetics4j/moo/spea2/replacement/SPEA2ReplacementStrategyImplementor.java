package net.bmahe.genetics4j.moo.spea2.replacement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.replacement.ReplacementStrategyImplementor;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.moo.spea2.spec.replacement.SPEA2Replacement;

public class SPEA2ReplacementStrategyImplementor<T extends Comparable<T>> implements ReplacementStrategyImplementor<T> {
	final static public Logger logger = LogManager.getLogger(SPEA2ReplacementStrategyImplementor.class);

	private final SPEA2Replacement<T> spea2Replacement;

	public SPEA2ReplacementStrategyImplementor(final SPEA2Replacement<T> _spea2Replacement) {
		this.spea2Replacement = _spea2Replacement;
	}

	protected double[] computeStrength(final Comparator<T> dominance, final Population<T> population) {
		Validate.notNull(dominance);
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);

		final double[] strengths = new double[population.size()];
		for (int i = 0; i < population.size(); i++) {
			final T fitness = population.getFitness(i);

			strengths[i] = SPEA2Utils.strength(dominance, i, fitness, population);
		}

		return strengths;
	}

	protected double[][] computeObjectiveDistances(final BiFunction<T, T, Double> distance,
			final Population<T> population) {
		Validate.notNull(distance);
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);

		final double[][] distanceObjectives = new double[population.size()][population.size()];

		for (int i = 0; i < population.size(); i++) {
			for (int j = 0; j < i; j++) {
				final Double distanceMeasure = distance.apply(population.getFitness(i), population.getFitness(j));
				distanceObjectives[i][j] = distanceMeasure;
				distanceObjectives[j][i] = distanceMeasure;
			}

			distanceObjectives[i][i] = 0.0;
		}
		return distanceObjectives;
	}

	protected double[] computeRawFitness(final Comparator<T> dominance, final double[] strengths,
			final Population<T> population) {
		Validate.notNull(dominance);
		Validate.notNull(strengths);
		Validate.notNull(population);
		Validate.isTrue(population.size() == strengths.length);
		Validate.isTrue(population.size() > 0);

		final double[] rawFitness = new double[population.size()];
		for (int i = 0; i < population.size(); i++) {
			final T fitness = population.getFitness(i);

			rawFitness[i] = SPEA2Utils.rawFitness(dominance, strengths, i, fitness, population);
		}

		return rawFitness;
	}

	protected List<List<Pair<Integer, Double>>> computeSortedDistances(final double[][] distanceObjectives,
			final Population<T> population) {
		Validate.notNull(distanceObjectives);
		Validate.notNull(population);
		Validate.isTrue(population.size() == distanceObjectives.length); // won't test all the rows
		Validate.isTrue(population.size() > 0);

		final List<List<Pair<Integer, Double>>> distances = new ArrayList<>();
		for (int i = 0; i < population.size(); i++) {
			final T fitness = population.getFitness(i);

			final List<Pair<Integer, Double>> kthDistances = SPEA2Utils
					.kthDistances(distanceObjectives, i, fitness, population);
			distances.add(kthDistances);

		}
		return distances;
	}

	protected double[] computeDensity(final List<List<Pair<Integer, Double>>> distances, final int k,
			final Population<T> population) {
		Validate.notNull(distances);
		Validate.isTrue(population.size() == distances.size());
		Validate.isTrue(k > 0);
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);

		final double[] density = new double[population.size()];
		for (int i = 0; i < population.size(); i++) {
			density[i] = 1.0d / (distances.get(i).get(k).getRight() + 2);
		}

		return density;
	}

	protected double[] computeFinalFitness(final double[] rawFitness, final double[] density,
			final Population<T> population) {
		Validate.notNull(rawFitness);
		Validate.notNull(density);
		Validate.isTrue(rawFitness.length == density.length);
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);
		Validate.isTrue(population.size() == density.length);

		final double[] finalFitness = new double[population.size()];
		for (int i = 0; i < population.size(); i++) {
			finalFitness[i] = rawFitness[i] + density[i];
		}

		return finalFitness;
	}

	protected int skipNull(final List<Pair<Integer, Double>> distances, final int i) {
		Validate.notNull(distances);
		Validate.isTrue(i >= 0);
		Validate.isTrue(i <= distances.size());

		int j = i;

		while (j < distances.size() && distances.get(j) == null) {
			j++;
		}

		return j;
	}

	protected List<Integer> computeAdditionalIndividuals(final Set<Integer> selectedIndex, final double[] rawFitness,
			final Population<T> population, final int numIndividuals) {
		Validate.notNull(selectedIndex);
		Validate.notNull(rawFitness);
		Validate.notNull(population);
		Validate.isTrue(rawFitness.length == population.size());
		Validate.isTrue(numIndividuals >= selectedIndex.size());

		if (numIndividuals == selectedIndex.size()) {
			return Collections.emptyList();
		}

		final List<Integer> additionalIndividuals = IntStream.range(0, population.size())
				.boxed()
				.filter((i) -> selectedIndex.contains(i) == false)
				.sorted((a, b) -> Double.compare(rawFitness[a], rawFitness[b]))
				.limit(numIndividuals - selectedIndex.size())
				.collect(Collectors.toList());

		return additionalIndividuals;
	}

	protected void truncatePopulation(final List<List<Pair<Integer, Double>>> distances, final Population<T> population,
			final int numIndividuals, final Set<Integer> selectedIndex) {

		final Map<Integer, List<Pair<Integer, Double>>> selectedDistances = new HashMap<>();
		final Map<Integer, Map<Integer, Integer>> selectedDistancesIndex = new HashMap<>();

		/**
		 * The goal here is two fold:
		 * - Build selectedDistances, which is a map of individual index -> ordered list
		 * of nearest neighbors, with only the individuals from selectedIndex. This will
		 * prevent the unnecessary processing of ignored individuals
		 * 
		 * - Build an inverted index selectedDistancesIndex so that we know where to
		 * delete entries in selectedDistances whenever an individual has been removed
		 * The index is in the form: individual -> key in selectedDistance -> Which
		 * position in the nearest neighbors
		 */
		for (final int index : selectedIndex) {

			final List<Pair<Integer, Double>> kthDistances = distances.get(index)
					.stream()
					.filter(p -> selectedIndex.contains(p.getLeft()))
					.collect(Collectors.toList());

			Validate.isTrue(kthDistances.size() == selectedIndex.size());
			selectedDistances.put(index, kthDistances);

			for (int i = 0; i < kthDistances.size(); i++) {
				final Pair<Integer, Double> pair = kthDistances.get(i);

				if (selectedDistancesIndex.containsKey(pair.getKey()) == false) {
					selectedDistancesIndex.put(pair.getKey(), new HashMap<>());
				}

				selectedDistancesIndex.get(pair.getKey()).put(index, i);
			}
		}

		while (selectedIndex.size() > numIndividuals) {

			int minIndex = -1;
			List<Pair<Integer, Double>> minDistances = null;
			for (final int candidateIndex : selectedIndex) {

				if (minIndex < 0) {
					minIndex = candidateIndex;
					minDistances = selectedDistances.get(candidateIndex);
				} else {
					final List<Pair<Integer, Double>> distancesCandidate = selectedDistances.get(candidateIndex);
					Validate.isTrue(minDistances.size() == distancesCandidate.size());

					int result = 0;
					int j = skipNull(minDistances, 0);
					int l = skipNull(distancesCandidate, 0);

					while (result == 0 && j < minDistances.size() && l < distancesCandidate.size()) {

						result = Double.compare(minDistances.get(j).getRight(), distancesCandidate.get(l).getRight());

						j++;
						j = skipNull(minDistances, j);

						l++;
						l = skipNull(distancesCandidate, l);
					}

					if (result > 0) {
						minIndex = candidateIndex;
						minDistances = distancesCandidate;
					}
				}
			}

			/**
			 * We cannot just remove it. We have to set the entry to 'null' as to not mess
			 * up the positions recorded in selectedDistancesIndex.
			 */
			final Map<Integer, Integer> reverseIndex = selectedDistancesIndex.get(minIndex);
			for (Entry<Integer, Integer> entry : reverseIndex.entrySet()) {
				final List<Pair<Integer, Double>> distancesToClean = selectedDistances.get(entry.getKey());
				distancesToClean.set((int) entry.getValue(), null);
			}
			for (Map<Integer, Integer> map : selectedDistancesIndex.values()) {
				map.remove(minIndex);
			}

			selectedDistancesIndex.remove(minIndex);
			selectedDistances.remove(minIndex);
			selectedIndex.remove(minIndex);
		}

	}

	protected Set<Integer> environmentalSelection(final List<List<Pair<Integer, Double>>> distances,
			final double[] rawFitness, final double[] finalFitness, final Population<T> population,
			final int numIndividuals) {

		final Set<Integer> selectedIndex = IntStream.range(0, population.size())
				.boxed()
				.filter((i) -> finalFitness[i] < 1)
				.collect(Collectors.toSet());

		logger.trace("Selected index size: {}", selectedIndex.size());

		if (selectedIndex.size() < numIndividuals) {

			final List<Integer> additionalIndividuals = computeAdditionalIndividuals(selectedIndex,
					rawFitness,
					population,
					numIndividuals);

			logger.trace("Adding {} additional individuals", additionalIndividuals.size());
			selectedIndex.addAll(additionalIndividuals);
		}

		if (selectedIndex.size() > numIndividuals) {
			logger.trace("Need to remove {} individuals", selectedIndex.size() - numIndividuals);

			truncatePopulation(distances, population, numIndividuals, selectedIndex);
		}

		return selectedIndex;
	}

	@Override
	public Population<T> select(final EAConfiguration<T> eaConfiguration, final int numIndividuals,
			final List<Genotype> population, final List<T> populationScores, final List<Genotype> offsprings,
			final List<T> offspringScores) {
		Validate.notNull(eaConfiguration);
		Validate.isTrue(numIndividuals > 0);
		Validate.notNull(population);
		Validate.notNull(populationScores);
		Validate.isTrue(population.size() == populationScores.size());
		Validate.notNull(offsprings);
		Validate.notNull(offspringScores);
		Validate.isTrue(offsprings.size() == offspringScores.size());

		final long startTimeNanos = System.nanoTime();
		logger.debug("Starting with requested {} individuals - {} population - {} offsprings",
				numIndividuals,
				population.size(),
				offsprings.size());

		switch (eaConfiguration.optimization()) {
			case MAXIMZE:
			case MINIMIZE:
				break;
			default:
				throw new IllegalArgumentException("Unsupported optimization " + eaConfiguration.optimization());
		}

		final Population<T> archive = new Population<>(population, populationScores);
		final Population<T> offspringPopulation = new Population<>(offsprings, offspringScores);

		final Population<T> combinedPopulation = new Population<>();
		if (spea2Replacement.deduplicate().isPresent()) {
			final Comparator<Genotype> individualDeduplicator = spea2Replacement.deduplicate().get();
			final Set<Genotype> seenGenotype = new TreeSet<>(individualDeduplicator);

			for (int i = 0; i < archive.size(); i++) {
				final Genotype genotype = archive.getGenotype(i);

				if (seenGenotype.add(genotype)) {
					final T fitness = archive.getFitness(i);
					combinedPopulation.add(genotype, fitness);
				}
			}
			final int ingestedFromArchive = combinedPopulation.size();
			logger.debug("Ingested {} individuals from the archive out of the {} available",
					ingestedFromArchive,
					archive.size());

			for (int i = 0; i < offspringPopulation.size(); i++) {
				final Genotype genotype = offspringPopulation.getGenotype(i);

				if (seenGenotype.add(genotype)) {
					final T fitness = offspringPopulation.getFitness(i);
					combinedPopulation.add(genotype, fitness);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Ingested {} individuals from the offsprings out of the {} available",
						combinedPopulation.size() - ingestedFromArchive,
						offspringPopulation.size());
			}

		} else {
			combinedPopulation.addAll(archive);
			combinedPopulation.addAll(offspringPopulation);
		}

		final Comparator<T> dominance = Optimization.MAXIMZE.equals(eaConfiguration.optimization())
				? spea2Replacement.dominance()
				: spea2Replacement.dominance().reversed();
		final int k = spea2Replacement.k().orElseGet(() -> (int) Math.sqrt(combinedPopulation.size()));
		logger.trace("Using k={}", k);
		Validate.isTrue(k > 0);

		///////////////// Fitness computation //////////////////////
		final double[] strengths = computeStrength(dominance, combinedPopulation);

		final double[][] distanceObjectives = computeObjectiveDistances(spea2Replacement.distance(),
				combinedPopulation);

		final double[] rawFitness = computeRawFitness(dominance, strengths, combinedPopulation);

		final List<List<Pair<Integer, Double>>> distances = computeSortedDistances(distanceObjectives,
				combinedPopulation);

		final double[] density = computeDensity(distances, k, combinedPopulation);

		final double[] finalFitness = computeFinalFitness(rawFitness, density, combinedPopulation);

		///////////////// Environmental Selection //////////////////

		final Set<Integer> selectedIndex = environmentalSelection(distances,
				rawFitness,
				finalFitness,
				combinedPopulation,
				numIndividuals);

		final Population<T> newPopulation = new Population<>();
		for (final int i : selectedIndex) {
			newPopulation.add(combinedPopulation.getGenotype(i), combinedPopulation.getFitness(i));
		}

		final long endTimeNanos = System.nanoTime();
		if (logger.isDebugEnabled()) {
			logger.debug("Finished with {} new population - Computation time: {}",
					newPopulation.size(),
					DurationFormatUtils.formatDurationHMS((endTimeNanos - startTimeNanos) / 1_000_000));
		}

		return newPopulation;
	}
}