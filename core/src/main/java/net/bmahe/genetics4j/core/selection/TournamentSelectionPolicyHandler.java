package net.bmahe.genetics4j.core.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class TournamentSelectionPolicyHandler implements SelectionPolicyHandler {
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
	public Selector resolve(GeneticSystemDescriptor geneticSystemDescriptor, GenotypeSpec genotypeSpec,
			SelectionPolicyHandlerResolver selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(TournamentSelection.class, selectionPolicy);

		return new Selector() {

			@Override
			public List<Genotype> select(GenotypeSpec genotypeSpec, int numIndividuals, Genotype[] population,
					double[] fitnessScore) {
				Validate.notNull(genotypeSpec);
				Validate.notNull(population);
				Validate.notNull(fitnessScore);
				Validate.isTrue(numIndividuals > 0);
				Validate.isTrue(population.length == fitnessScore.length);

				switch (genotypeSpec.optimization()) {
					case MAXIMZE:
					case MINIMIZE:
						break;
					default:
						throw new IllegalArgumentException("Unsupported optimization " + genotypeSpec.optimization());
				}

				final List<Genotype> selected = new ArrayList<>(numIndividuals);

				// TODO can't wait for switch expressions
				final BiFunction<Double, Double, Boolean> isScoreBetter = Optimization.MAXIMZE
						.equals(genotypeSpec.optimization()) ? (best, score) -> best < score : (best, score) -> best > score;

				final TournamentSelection tournamentSelection = (TournamentSelection) selectionPolicy;

				while (selected.size() < numIndividuals) {

					Genotype bestCandidate = null;
					double bestScore = 0;

					for (int i = 0; i < tournamentSelection.numCandidates(); i++) {
						final int candidateIndex = random.nextInt(fitnessScore.length);

						if (bestCandidate == null || isScoreBetter.apply(bestScore, fitnessScore[candidateIndex])) {
							bestScore = fitnessScore[candidateIndex];
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