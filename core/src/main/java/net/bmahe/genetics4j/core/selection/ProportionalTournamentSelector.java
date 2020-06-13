package net.bmahe.genetics4j.core.selection;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.ProportionalTournament;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class ProportionalTournamentSelector<T extends Comparable<T>> implements Selector<T> {
	final static public Logger logger = LogManager.getLogger(ProportionalTournamentSelector.class);

	private final SelectionPolicy selectionPolicy;
	private final Random random;

	public ProportionalTournamentSelector(final SelectionPolicy _selectionPolicy, final Random _random) {
		Validate.notNull(_selectionPolicy);
		Validate.isInstanceOf(ProportionalTournament.class, _selectionPolicy);
		Validate.notNull(_random);

		this.selectionPolicy = _selectionPolicy;
		this.random = _random;
	}

	@Override
	public Population<T> select(final EAConfiguration<T> eaConfiguration, final int numIndividuals,
			final List<Genotype> population, final List<T> fitnessScore) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numIndividuals > 0);
		Validate.isTrue(population.size() == fitnessScore.size());

		@SuppressWarnings("unchecked")
		final ProportionalTournament<T> proportionalTournament = (ProportionalTournament<T>) selectionPolicy;
		final Comparator<Individual<T>> firstComparator = proportionalTournament.firstComparator();
		final Comparator<Individual<T>> secondComparator = proportionalTournament.secondComparator();
		final int numCandidates = proportionalTournament.numCandidates();
		final double proportionFirst = proportionalTournament.proportionFirst();

		switch (eaConfiguration.optimization()) {
			case MAXIMZE:
			case MINIMIZE:
				break;
			default:
				throw new IllegalArgumentException("Unsupported optimization " + eaConfiguration.optimization());
		}

		final Comparator<Individual<T>> firstComparatorOptimize = Optimization.MAXIMZE
				.equals(eaConfiguration.optimization()) ? firstComparator : firstComparator.reversed();

		final Comparator<Individual<T>> secondComparatorOptimize = Optimization.MAXIMZE
				.equals(eaConfiguration.optimization()) ? secondComparator : secondComparator.reversed();

		logger.debug("Selecting {} individuals", numIndividuals);

		final Population<T> selectedIndividuals = new Population<>();

		while (selectedIndividuals.size() < numIndividuals) {

			final Comparator<Individual<T>> comparator = random.nextDouble() < proportionFirst ? firstComparatorOptimize
					: secondComparatorOptimize;

			final Individual<T> selected = random.ints(numCandidates, 0, population.size())
					.boxed()
					.map((i) -> Individual.of(population.get(i), fitnessScore.get(i)))
					.max(comparator)
					.get();

			selectedIndividuals.add(selected.genotype(), selected.fitness());
		}

		return selectedIndividuals;
	}
}