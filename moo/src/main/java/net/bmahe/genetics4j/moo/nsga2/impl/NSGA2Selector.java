package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.ObjectiveDistance;

public class NSGA2Selector<T extends Comparable<T>> implements Selector<T> {

	private final NSGA2Selection<T> nsga2Selection;

	public NSGA2Selector(final NSGA2Selection<T> _nsga2Selection) {
		Validate.notNull(_nsga2Selection);

		this.nsga2Selection = _nsga2Selection;
	}

	@Override
	public List<Genotype> select(final EAConfiguration<T> eaConfiguration, final int numIndividuals,
			final Genotype[] population, final List<T> fitnessScore) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numIndividuals > 0);
		Validate.isTrue(population.length == fitnessScore.size());

		final Comparator<T> dominance = nsga2Selection.dominance();
		final int numberObjectives = nsga2Selection.numberObjectives();
		final Function<Integer, Comparator<T>> objectiveComparator = nsga2Selection.objectiveComparator();
		final ObjectiveDistance<T> objectiveDistance = nsga2Selection.distance();

		final List<Set<Integer>> rankedPopulation = NSGA2Utils.rankedPopulation(dominance, fitnessScore);
		double[] crowdingDistanceAssignment = NSGA2Utils
				.crowdingDistanceAssignment(numberObjectives, fitnessScore, objectiveComparator, objectiveDistance);

		final List<Genotype> selectedIndividuals = new ArrayList<Genotype>();

		int currentFrontIndex = 0;
		while (selectedIndividuals.size() < numIndividuals && currentFrontIndex < rankedPopulation.size()
				&& rankedPopulation.get(currentFrontIndex).size() > 0) {

			final Set<Integer> currentFront = rankedPopulation.get(currentFrontIndex);

			Collection<Integer> bestIndividuals = currentFront;
			if (currentFront.size() > numIndividuals - selectedIndividuals.size()) {
				bestIndividuals = currentFront.stream()
						.sorted((a, b) -> Double.compare(crowdingDistanceAssignment[b], crowdingDistanceAssignment[a]))
						.limit(numIndividuals - selectedIndividuals.size())
						.collect(Collectors.toList());
			}

			for (final Integer individualIndex : bestIndividuals) {
				selectedIndividuals.add(population[individualIndex]);
			}

			currentFrontIndex++;
		}

		return selectedIndividuals;
	}
}