package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
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
	public Population<T> select(EAConfiguration<T> eaConfiguration, int numIndividuals, List<Genotype> population,
			List<T> fitnessScore) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numIndividuals > 0);
		Validate.isTrue(population.size() == fitnessScore.size());

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

		final List<Set<Integer>> rankedPopulation = NSGA2Utils.rankedPopulation(dominance, fitnessScore);
		// Build a reverse index
		final int[] individual2Rank = new int[population.size()];
		for (int j = 0; j < rankedPopulation.size(); j++) {
			final Set<Integer> set = rankedPopulation.get(j);

			for (final Integer idx : set) {
				individual2Rank[idx] = j;
			}
		}

		final double[] crowdingDistanceAssignment = NSGA2Utils
				.crowdingDistanceAssignment(numberObjectives, fitnessScore, objectiveComparator, objectiveDistance);

		final Population<T> selectedIndividuals = new Population<>();

		while (selectedIndividuals.size() < numIndividuals) {

			Genotype bestCandidate = null;
			int bestCandidateIndex = -1;
			T bestFitness = null;

			for (int i = 0; i < numCandidates; i++) {
				final int candidateIndex = random.nextInt(fitnessScore.size());

				if (bestCandidate == null || individual2Rank[candidateIndex] < individual2Rank[bestCandidateIndex]
						|| (individual2Rank[candidateIndex] == individual2Rank[bestCandidateIndex]
								&& crowdingDistanceAssignment[candidateIndex] > crowdingDistanceAssignment[bestCandidateIndex])) {
					bestCandidate = population.get(candidateIndex);
					bestFitness = fitnessScore.get(candidateIndex);
					bestCandidateIndex = candidateIndex;
				}
			}

			selectedIndividuals.add(bestCandidate, bestFitness);
		}

		return selectedIndividuals;

	}
}