package net.bmahe.genetics4j.core.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
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
	public List<Genotype> select(final SelectionPolicy selectionPolicy, final int numParent, final Genotype[] population,
			final double[] fitnessScore) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(TournamentSelection.class, selectionPolicy);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numParent > 0);
		Validate.isTrue(population.length == fitnessScore.length);

		final List<Genotype> selected = new ArrayList<>(numParent);

		final TournamentSelection tournamentSelection = (TournamentSelection) selectionPolicy;

		while (selected.size() < numParent) {

			Genotype maxCandidate = null;
			double maxScore = Double.MIN_VALUE;
			for (int i = 0; i < tournamentSelection.numCandidates(); i++) {
				final int candidateIndex = random.nextInt(fitnessScore.length);

				if (fitnessScore[candidateIndex] > maxScore) {
					maxScore = fitnessScore[candidateIndex];
					maxCandidate = population[candidateIndex];
				}
			}
			selected.add(maxCandidate);
		}

		return selected;
	}
}