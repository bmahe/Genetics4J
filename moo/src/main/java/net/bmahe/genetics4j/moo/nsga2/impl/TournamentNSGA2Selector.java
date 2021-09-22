package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.moo.ObjectiveDistance;
import net.bmahe.genetics4j.moo.ParetoUtils;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class TournamentNSGA2Selector<T extends Comparable<T>> implements Selector<T> {
	final static public Logger logger = LogManager.getLogger(TournamentNSGA2Selector.class);

	private final TournamentNSGA2Selection<T> tournamentNSGA2Selection;
	private final RandomGenerator randomGenerator;

	public TournamentNSGA2Selector(final RandomGenerator _randomGenerator,
			final TournamentNSGA2Selection<T> _tournamentNSGA2Selection) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_tournamentNSGA2Selection);

		this.randomGenerator = _randomGenerator;
		this.tournamentNSGA2Selection = _tournamentNSGA2Selection;

	}

	@Override
	public Population<T> select(final EAConfiguration<T> eaConfiguration, final int numIndividuals,
			final List<Genotype> population, final List<T> fitnessScore) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numIndividuals > 0);
		Validate.isTrue(population.size() == fitnessScore.size());

		logger.debug("Incoming population size is {}", population.size());

		final Population<T> individuals = new Population<>();
		if (tournamentNSGA2Selection.deduplicate().isPresent()) {
			final Comparator<Genotype> individualDeduplicator = tournamentNSGA2Selection.deduplicate().get();
			final Set<Genotype> seenGenotype = new TreeSet<>(individualDeduplicator);

			for (int i = 0; i < population.size(); i++) {
				final Genotype genotype = population.get(i);
				final T fitness = fitnessScore.get(i);

				if (seenGenotype.add(genotype)) {
					individuals.add(genotype, fitness);
				}
			}

		} else {
			for (int i = 0; i < population.size(); i++) {
				final Genotype genotype = population.get(i);
				final T fitness = fitnessScore.get(i);

				individuals.add(genotype, fitness);
			}
		}

		logger.debug("Selecting {} individuals from a population of {}", numIndividuals, individuals.size());

		switch (eaConfiguration.optimization()) {
		case MAXIMZE:
		case MINIMIZE:
			break;
		default:
			throw new IllegalArgumentException("Unsupported optimization " + eaConfiguration.optimization());
		}

		final int numberObjectives = tournamentNSGA2Selection.numberObjectives();

		final Comparator<T> dominance = Optimization.MAXIMZE.equals(eaConfiguration.optimization())
				? tournamentNSGA2Selection.dominance()
				: tournamentNSGA2Selection.dominance().reversed();

		final Function<Integer, Comparator<T>> objectiveComparator = Optimization.MAXIMZE
				.equals(eaConfiguration.optimization()) ? tournamentNSGA2Selection.objectiveComparator()
						: (m) -> tournamentNSGA2Selection.objectiveComparator().apply(m).reversed();

		final ObjectiveDistance<T> objectiveDistance = tournamentNSGA2Selection.distance();
		final int numCandidates = tournamentNSGA2Selection.numCandidates();

		logger.debug("Ranking population");
		final List<Set<Integer>> rankedPopulation = ParetoUtils.rankedPopulation(dominance,
				individuals.getAllFitnesses());
		// Build a reverse index
		final int[] individual2Rank = new int[individuals.size()];
		for (int j = 0; j < rankedPopulation.size(); j++) {
			final Set<Integer> set = rankedPopulation.get(j);

			for (final Integer idx : set) {
				individual2Rank[idx] = j;
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Ranked population: {}", rankedPopulation);
			for (int i = 0; i < rankedPopulation.size(); i++) {
				final Set<Integer> subPopulationIdx = rankedPopulation.get(i);
				logger.trace("\tRank {}", i);
				for (final Integer index : subPopulationIdx) {
					logger.trace("\t\t{} - Fitness {}", index, individuals.getFitness(index));
				}
			}
		}
		logger.debug("Computing crowding distance assignment");
		final double[] crowdingDistanceAssignment = NSGA2Utils.crowdingDistanceAssignment(numberObjectives,
				individuals.getAllFitnesses(),
				objectiveComparator,
				objectiveDistance);

		logger.debug("Performing tournaments");
		final Population<T> selectedIndividuals = new Population<>();
		while (selectedIndividuals.size() < numIndividuals) {

			logger.trace("Performing tournament");
			Genotype bestCandidate = null;
			int bestCandidateIndex = -1;
			T bestFitness = null;

			for (int i = 0; i < numCandidates; i++) {
				final int candidateIndex = randomGenerator.nextInt(individuals.size());

				logger.trace("\tCandidate - index {} - rank {} - crowding distance {} - fitness {}",
						candidateIndex,
						individual2Rank[candidateIndex],
						crowdingDistanceAssignment[candidateIndex],
						individuals.getFitness(candidateIndex));

				if (bestCandidate == null || individual2Rank[candidateIndex] < individual2Rank[bestCandidateIndex]
						|| (individual2Rank[candidateIndex] == individual2Rank[bestCandidateIndex]
								&& crowdingDistanceAssignment[candidateIndex] > crowdingDistanceAssignment[bestCandidateIndex])) {

					logger.trace("\t candidate win!");
					bestCandidate = individuals.getGenotype(candidateIndex);
					bestFitness = individuals.getFitness(candidateIndex);
					bestCandidateIndex = candidateIndex;
				}
			}

			selectedIndividuals.add(bestCandidate, bestFitness);
		}

		return selectedIndividuals;
	}
}