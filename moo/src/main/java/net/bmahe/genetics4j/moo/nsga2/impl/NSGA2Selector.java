package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;

public class NSGA2Selector<T extends Comparable<T>> implements Selector<T> {
	final static public Logger logger = LogManager.getLogger(NSGA2Selector.class);

	private final NSGA2Selection<T> nsga2Selection;

	public NSGA2Selector(final NSGA2Selection<T> _nsga2Selection) {
		Validate.notNull(_nsga2Selection);

		this.nsga2Selection = _nsga2Selection;
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
		if (nsga2Selection.deduplicate().isPresent()) {
			final Comparator<Genotype> individualDeduplicator = nsga2Selection.deduplicate().get();
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

		final int numberObjectives = nsga2Selection.numberObjectives();

		final Comparator<T> dominance = Optimization.MAXIMZE.equals(eaConfiguration.optimization())
				? nsga2Selection.dominance()
				: nsga2Selection.dominance().reversed();

		final Function<Integer, Comparator<T>> objectiveComparator = Optimization.MAXIMZE
				.equals(eaConfiguration.optimization()) ? nsga2Selection.objectiveComparator()
						: (m) -> nsga2Selection.objectiveComparator().apply(m).reversed();

		final ObjectiveDistance<T> objectiveDistance = nsga2Selection.distance();

		logger.debug("Ranking population");
		final List<Set<Integer>> rankedPopulation = ParetoUtils.rankedPopulation(dominance,
				individuals.getAllFitnesses());

		logger.debug("Computing crowding distance assignment");
		double[] crowdingDistanceAssignment = NSGA2Utils.crowdingDistanceAssignment(numberObjectives,
				individuals.getAllFitnesses(),
				objectiveComparator,
				objectiveDistance);

		logger.debug("Selecting individuals");
		final Population<T> selectedIndividuals = new Population<>();
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
				if (logger.isTraceEnabled()) {
					logger.trace("Adding individual with index {}, fitness {}, rank {}, crowding distance {}",
							individualIndex,
							individuals.getFitness(individualIndex),
							currentFrontIndex,
							crowdingDistanceAssignment[individualIndex]);
				}

				selectedIndividuals.add(individuals.getGenotype(individualIndex),
						individuals.getFitness(individualIndex));
			}

			logger.trace("Selected {} individuals from rank {}", bestIndividuals.size(), currentFrontIndex);
			currentFrontIndex++;
		}

		return selectedIndividuals;
	}
}