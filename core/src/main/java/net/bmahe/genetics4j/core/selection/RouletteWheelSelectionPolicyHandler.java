package net.bmahe.genetics4j.core.selection;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class RouletteWheelSelectionPolicyHandler<T extends Number & Comparable<T>>
		implements SelectionPolicyHandler<T> {

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
	public Selector<T> resolve(EAExecutionContext<T> eaExecutionContext, EAConfiguration<T> eaConfiguration,
			SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(RouletteWheelSelection.class, selectionPolicy);

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

				final List<Genotype> selectedParents = new LinkedList<>();

				final double minFitness = fitnessScore.stream()
						.map(Number::doubleValue)
						.min(Comparator.naturalOrder())
						.orElseThrow();
				final double maxFitness = fitnessScore.stream()
						.map(Number::doubleValue)
						.max(Comparator.naturalOrder())
						.orElseThrow();
				final double reversedBase = minFitness + maxFitness; // Used as a base when minimizing

				double sumFitness = 0.0;
				final double[] probabilities = new double[population.length];

				for (int i = 0; i < population.length; i++) {
					if (eaConfiguration.optimization().equals(Optimization.MAXIMZE)) {
						sumFitness += fitnessScore.get(i).doubleValue();
					} else {
						sumFitness += reversedBase - fitnessScore.get(i).doubleValue();
					}
					probabilities[i] = sumFitness;
				}

				for (int i = 0; i < numIndividuals; i++) {
					final double targetScore = random.nextDouble() * sumFitness;

					int index = 0;
					while (probabilities[index] < targetScore) {
						index++;
					}

					selectedParents.add(population[index]);
				}

				return selectedParents;
			}
		};
	}
}