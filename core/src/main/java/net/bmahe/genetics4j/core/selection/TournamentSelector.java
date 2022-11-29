package net.bmahe.genetics4j.core.selection;

import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.Tournament;

public class TournamentSelector<T extends Comparable<T>> implements Selector<T> {
	public static final Logger logger = LogManager.getLogger(TournamentSelector.class);

	private final SelectionPolicy selectionPolicy;
	private final RandomGenerator randomGenerator;

	public TournamentSelector(final SelectionPolicy _selectionPolicy, final RandomGenerator _randomGenerator) {
		Validate.notNull(_selectionPolicy);
		Validate.isInstanceOf(Tournament.class, _selectionPolicy);
		Validate.notNull(_randomGenerator);

		this.selectionPolicy = _selectionPolicy;
		this.randomGenerator = _randomGenerator;
	}

	@Override
	public Population<T> select(final AbstractEAConfiguration<T> eaConfiguration, final int numIndividuals,
			final List<Genotype> population, final List<T> fitnessScore) {
		Validate.notNull(eaConfiguration);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numIndividuals > 0);
		Validate.isTrue(population.size() == fitnessScore.size());

		@SuppressWarnings("unchecked")
		final Tournament<T> tournamentSelection = (Tournament<T>) selectionPolicy;

		switch (eaConfiguration.optimization()) {
			case MAXIMZE:
			case MINIMIZE:
				break;
			default:
				throw new IllegalArgumentException("Unsupported optimization " + eaConfiguration.optimization());
		}

		final Comparator<Individual<T>> baseComparator = tournamentSelection.comparator();
		final Comparator<Individual<T>> comparator = Optimization.MAXIMZE.equals(eaConfiguration.optimization())
				? baseComparator
				: baseComparator.reversed();

		logger.debug("Selecting {} individuals", numIndividuals);

		final Population<T> selectedIndividuals = new Population<>();
		while (selectedIndividuals.size() < numIndividuals) {

			Individual<T> bestIndividual = null;

			for (int i = 0; i < tournamentSelection.numCandidates(); i++) {
				final int candidateIndex = randomGenerator.nextInt(fitnessScore.size());
				final Individual<T> candidate = Individual.of(population.get(candidateIndex),
						fitnessScore.get(candidateIndex));

				if (bestIndividual == null || comparator.compare(bestIndividual, candidate) < 0) {
					bestIndividual = candidate;
				}
			}

			selectedIndividuals.add(bestIndividual);
		}

		return selectedIndividuals;
	}
}