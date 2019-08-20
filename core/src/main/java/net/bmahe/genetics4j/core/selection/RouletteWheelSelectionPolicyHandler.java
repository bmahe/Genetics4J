package net.bmahe.genetics4j.core.selection;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class RouletteWheelSelectionPolicyHandler implements SelectionPolicyHandler {

	private final Random random;

	public RouletteWheelSelectionPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof RouletteWheelSelection;
	}

	@Override
	public List<Genotype> select(final GenotypeSpec genotypeSpec, final SelectionPolicy selectionPolicy,
			final int numParent, final Genotype[] population, final double[] fitnessScore) {
		Validate.notNull(genotypeSpec);
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(RouletteWheelSelection.class, selectionPolicy);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numParent > 0);
		Validate.isTrue(population.length == fitnessScore.length);

		switch (genotypeSpec.optimization()) {
			case MAXIMZE:
			case MINIMIZE:
				break;
			default:
				throw new IllegalArgumentException("Unsupported optimization " + genotypeSpec.optimization());
		}

		final List<Genotype> selectedParents = new LinkedList<>();

		final double minFitness = Arrays.stream(fitnessScore).min().orElseThrow();
		final double maxFitness = Arrays.stream(fitnessScore).max().orElseThrow();
		final double reversedBase = minFitness + maxFitness; // Used as a base when minimizing

		double sumFitness = 0.0;
		final double[] probabilities = new double[population.length];

		for (int i = 0; i < population.length; i++) {
			if (genotypeSpec.optimization().equals(Optimization.MAXIMZE)) {
				sumFitness += fitnessScore[i];
			} else {
				sumFitness += reversedBase - fitnessScore[i];
			}
			probabilities[i] = sumFitness;
		}

		for (int i = 0; i < numParent; i++) {
			final double targetScore = random.nextDouble() * sumFitness;

			int index = 0;
			while (probabilities[index] < targetScore) {
				index++;
			}

			selectedParents.add(population[index]);
		}

		return selectedParents;
	}
}