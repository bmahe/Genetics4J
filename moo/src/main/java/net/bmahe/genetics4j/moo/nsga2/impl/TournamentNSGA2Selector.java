package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.moo.nsga2.spec.ObjectiveDistance;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class TournamentNSGA2Selector<T extends Comparable<T>> implements Selector<T> {

	private final TournamentNSGA2Selection<T> tournamentNSGA2Selection;
	private final Random random;

	public TournamentNSGA2Selector(final Random _random, final TournamentNSGA2Selection<T> _tournamentNSGA2Selection) {
		Validate.notNull(_random);
		Validate.notNull(_tournamentNSGA2Selection);

		this.random = _random;
		this.tournamentNSGA2Selection = _tournamentNSGA2Selection;

	}

	@Override
	public List<Genotype> select(EAConfiguration<T> eaConfiguration, int numIndividuals, Genotype[] population,
			List<T> fitnessScore) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numIndividuals > 0);
		Validate.isTrue(population.length == fitnessScore.size());

		final Comparator<T> dominance = tournamentNSGA2Selection.dominance();
		final int numberObjectives = tournamentNSGA2Selection.numberObjectives();
		final Function<Integer, Comparator<T>> objectiveComparator = tournamentNSGA2Selection.objectiveComparator();
		final ObjectiveDistance<T> objectiveDistance = tournamentNSGA2Selection.distance();
		final int numCandidates = tournamentNSGA2Selection.numCandidates();

		final List<Set<Integer>> rankedPopulation = NSGA2Utils.rankedPopulation(dominance, fitnessScore);
		// Build a reverse index
		final int[] individual2Rank = new int[population.length];
		for (int j = 0; j < rankedPopulation.size(); j++) {
			final Set<Integer> set = rankedPopulation.get(j);

			for (final Integer idx : set) {
				individual2Rank[idx] = j;
			}
		}

		final double[] crowdingDistanceAssignment = NSGA2Utils
				.crowdingDistanceAssignment(numberObjectives, fitnessScore, objectiveComparator, objectiveDistance);

		final List<Genotype> selectedIndividuals = new ArrayList<Genotype>();

		while (selectedIndividuals.size() < numIndividuals) {

			Genotype bestCandidate = null;
			int bestCandidateIndex = -1;

			for (int i = 0; i < numCandidates; i++) {
				final int candidateIndex = random.nextInt(fitnessScore.size());

				if (bestCandidate == null || individual2Rank[candidateIndex] < individual2Rank[bestCandidateIndex]
						|| (individual2Rank[candidateIndex] == individual2Rank[bestCandidateIndex]
								&& crowdingDistanceAssignment[candidateIndex] > crowdingDistanceAssignment[bestCandidateIndex])) {
					bestCandidate = population[candidateIndex];
					bestCandidateIndex = candidateIndex;
				}
			}

			selectedIndividuals.add(bestCandidate);
		}

		return selectedIndividuals;

	}
}