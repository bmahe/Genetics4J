package net.bmahe.genetics4j.core.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.DoubleTournament;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.Tournament;

public class DoubleTournamentSelector<T extends Comparable<T>> implements Selector<T> {
	final static public Logger logger = LogManager.getLogger(DoubleTournamentSelector.class);

	private final SelectionPolicy selectionPolicy;
	private final RandomGenerator randomGenerator;

	public DoubleTournamentSelector(final SelectionPolicy _selectionPolicy, final RandomGenerator _randomGenerator) {
		Validate.notNull(_selectionPolicy);
		Validate.isInstanceOf(DoubleTournament.class, _selectionPolicy);
		Validate.notNull(_randomGenerator);

		this.selectionPolicy = _selectionPolicy;
		this.randomGenerator = _randomGenerator;
	}

	protected Individual<T> randomIndividual(final List<Genotype> population, final List<T> fitnessScore) {
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(fitnessScore.size() > 0);
		Validate.isTrue(population.size() == fitnessScore.size());

		final int candidateIndex = randomGenerator.nextInt(fitnessScore.size());
		return Individual.of(population.get(candidateIndex), fitnessScore.get(candidateIndex));

	}

	protected Individual<T> selectForFitness(final EAConfiguration<T> eaConfiguration,
			final Comparator<Individual<T>> fitnessComparator, final int numCandidates, final List<Genotype> population,
			final List<T> fitnessScore) {
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(fitnessScore.isEmpty() == false);

		return IntStream.range(0, numCandidates)
				.boxed()
				.map(i -> randomIndividual(population, fitnessScore))
				.max((a, b) -> fitnessComparator.compare(a, b))
				.get();

	}

	protected Individual<T> parsimonyPick(final Comparator<Individual<T>> parsimonyComparator,
			final double parsimonyTournamentSize, final Individual<T> first, final Individual<T> second) {
		Validate.notNull(parsimonyComparator);
		Validate.inclusiveBetween(0.0, 2.0, parsimonyTournamentSize);
		Validate.notNull(first);
		Validate.notNull(second);

		final int parsimonyCompared = parsimonyComparator.compare(first, second);

		Individual<T> selected = first;
		if (randomGenerator.nextDouble() < parsimonyTournamentSize / 2.0) {
			if (parsimonyCompared > 0) {
				selected = second;
			}
		} else {
			if (parsimonyCompared < 0) {
				selected = second;
			}
		}

		return selected;
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
		final DoubleTournament<T> doubleTournament = (DoubleTournament<T>) selectionPolicy;
		final boolean doFitnessFirst = doubleTournament.doFitnessFirst();
		final Tournament<T> fitnessTournament = doubleTournament.fitnessTournament();
		final Comparator<Individual<T>> parsimonyComparator = doubleTournament.parsimonyComparator();
		final double parsimonyTournamentSize = doubleTournament.parsimonyTournamentSize();

		Validate.isTrue((doFitnessFirst && parsimonyTournamentSize <= 2.0 && parsimonyTournamentSize >= 0.0)
				|| doFitnessFirst == false);

		switch (eaConfiguration.optimization()) {
		case MAXIMZE:
		case MINIMIZE:
			break;
		default:
			throw new IllegalArgumentException("Unsupported optimization " + eaConfiguration.optimization());
		}

		final Comparator<Individual<T>> fitnessComparator = Optimization.MAXIMZE.equals(eaConfiguration.optimization())
				? fitnessTournament.comparator()
				: fitnessTournament.comparator().reversed();

		logger.debug("Selecting {} individuals", numIndividuals);

		final Population<T> selectedIndividuals = new Population<>();

		while (selectedIndividuals.size() < numIndividuals) {

			if (doFitnessFirst) {
				final Individual<T> first = selectForFitness(eaConfiguration,
						fitnessComparator,
						fitnessTournament.numCandidates(),
						population,
						fitnessScore);
				final Individual<T> second = selectForFitness(eaConfiguration,
						fitnessComparator,
						fitnessTournament.numCandidates(),
						population,
						fitnessScore);

				final Individual<T> selected = parsimonyPick(parsimonyComparator, parsimonyTournamentSize, first, second);

				selectedIndividuals.add(selected.genotype(), selected.fitness());
			} else {

				final int numberCandidatesFitness = fitnessTournament.numCandidates();
				final List<Individual<T>> candidatesFitness = new ArrayList<>(numberCandidatesFitness);

				for (int i = 0; i < numberCandidatesFitness; i++) {
					final Individual<T> first = randomIndividual(population, fitnessScore);
					final Individual<T> second = randomIndividual(population, fitnessScore);

					final Individual<T> selected = parsimonyPick(parsimonyComparator,
							parsimonyTournamentSize,
							first,
							second);

					candidatesFitness.add(selected);
				}

				final Individual<T> selected = candidatesFitness.stream()
						.max((a, b) -> fitnessComparator.compare(a, b))
						.get();

				selectedIndividuals.add(selected.genotype(), selected.fitness());
			}

		}

		return selectedIndividuals;
	}
}