package net.bmahe.genetics4j.core.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.MultiTournaments;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.Tournament;

public class MultiTournamentsSelectionPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {
	final static public Logger logger = LogManager.getLogger(MultiTournamentsSelectionPolicyHandler.class);

	private final Random random;

	private List<Individual<T>> pickRandomCandidates(final Random random, final List<Genotype> population,
			final List<T> fitnessScore, final int numCandidates) {
		Validate.notNull(random);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(fitnessScore.size() > 0);
		Validate.isTrue(numCandidates > 0);

		return random.ints(0, fitnessScore.size())
				.boxed()
				.limit(numCandidates)
				.map(i -> Individual.of(population.get(i), fitnessScore.get(i)))
				.collect(Collectors.toList());
	}

	private Individual<T> runTournament(final Tournament<T> tournament, final List<Genotype> population,
			final List<T> fitnessScore, final List<Individual<T>> candidates) {
		Validate.notNull(tournament);

		final Comparator<Individual<T>> comparator = tournament.comparator();

		return candidates.stream().max(comparator).get();
	}

	private Individual<T> runTournament(final Random random, final List<Tournament<T>> tournaments,
			final List<Genotype> population, final List<T> fitnessScore, final int tournamentIndex) {
		Validate.notNull(tournaments);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(tournamentIndex < tournaments.size());
		Validate.isTrue(tournamentIndex >= 0);

		final Tournament<T> tournament = tournaments.get(tournamentIndex);
		final int numCandidates = tournament.numCandidates();

		List<Individual<T>> candidates;
		if (tournamentIndex == 0) {
			candidates = pickRandomCandidates(random, population, fitnessScore, numCandidates);
		} else {
			candidates = new ArrayList<>();

			for (int i = 0; i < numCandidates; i++) {
				final Individual<T> candidate = runTournament(random,
						tournaments,
						population,
						fitnessScore,
						tournamentIndex - 1);
				candidates.add(candidate);
			}
		}

		return runTournament(tournament, population, fitnessScore, candidates);

	}

	public MultiTournamentsSelectionPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof MultiTournaments;
	}

	@Override
	public Selector<T> resolve(final EAExecutionContext<T> eaExecutionContext, final EAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(MultiTournaments.class, selectionPolicy);

		return new Selector<T>() {

			@Override
			public Population<T> select(final EAConfiguration<T> eaConfiguration, final int numIndividuals,
					final List<Genotype> population, final List<T> fitnessScore) {
				Validate.notNull(eaConfiguration);
				Validate.notNull(population);
				Validate.notNull(fitnessScore);
				Validate.isTrue(numIndividuals > 0);
				Validate.isTrue(population.size() == fitnessScore.size());

				@SuppressWarnings("unchecked")
				final MultiTournaments<T> multiTournaments = (MultiTournaments<T>) selectionPolicy;
				final List<Tournament<T>> tournaments = multiTournaments.tournaments();

				logger.debug("Selecting {} individuals", numIndividuals);
				final Population<T> selectedIndividuals = new Population<>();
				while (selectedIndividuals.size() < numIndividuals) {
					final Individual<T> selectedIndividual = runTournament(random,
							tournaments,
							population,
							fitnessScore,
							tournaments.size() - 1);
					selectedIndividuals.add(selectedIndividual);
				}

				return selectedIndividuals;
			}
		};
	}
}