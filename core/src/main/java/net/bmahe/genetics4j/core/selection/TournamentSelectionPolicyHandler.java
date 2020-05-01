package net.bmahe.genetics4j.core.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class TournamentSelectionPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {
	private final Random random;

	public TournamentSelectionPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof TournamentSelection;
	}

	@Override
	public Selector<T> resolve(EAExecutionContext<T> eaExecutionContext, EAConfiguration<T> eaConfiguration,
			SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(TournamentSelection.class, selectionPolicy);

		return new Selector<T>() {

			@Override
			public List<Genotype> select(EAConfiguration<T> eaConfiguration, int numIndividuals, Genotype[] population,
					List<T> fitnessScore) {
				Validate.notNull(eaConfiguration);
				Validate.notNull(population);
				Validate.notNull(fitnessScore);
				Validate.isTrue(numIndividuals > 0);
				Validate.isTrue(population.length == fitnessScore.size());

				switch (eaConfiguration.optimization()) {
					case MAXIMZE:
					case MINIMIZE:
						break;
					default:
						throw new IllegalArgumentException(
								"Unsupported optimization " + eaConfiguration.optimization());
				}

				final List<Genotype> selected = new ArrayList<>(numIndividuals);

				final Comparator<T> comparator = Optimization.MAXIMZE.equals(eaConfiguration.optimization())
						? Comparator.naturalOrder()
						: Comparator.reverseOrder();

				final TournamentSelection tournamentSelection = (TournamentSelection) selectionPolicy;

				while (selected.size() < numIndividuals) {

					Genotype bestCandidate = null;
					T bestFitness = null;

					for (int i = 0; i < tournamentSelection.numCandidates(); i++) {
						final int candidateIndex = random.nextInt(fitnessScore.size());

						if (bestCandidate == null
								|| comparator.compare(bestFitness, fitnessScore.get(candidateIndex)) < 0) {
							bestFitness = fitnessScore.get(candidateIndex);
							bestCandidate = population[candidateIndex];
						}
					}

					selected.add(bestCandidate);
				}

				return selected;
			}
		};
	}
}