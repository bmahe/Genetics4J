package net.bmahe.genetics4j.core.selection;

import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheel;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class RouletteWheelSelectionPolicyHandler<T extends Number & Comparable<T>>
		implements SelectionPolicyHandler<T> {

	private final RandomGenerator randomGenerator;

	public RouletteWheelSelectionPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof RouletteWheel;
	}

	@Override
	public Selector<T> resolve(EAExecutionContext<T> eaExecutionContext, AbstractEAConfiguration<T> eaConfiguration,
			SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(RouletteWheel.class, selectionPolicy);

		return new Selector<T>() {

			@Override
			public Population<T> select(final AbstractEAConfiguration<T> eaConfiguration, final int numIndividuals,
					final List<Genotype> population, final List<T> fitnessScore) {
				Validate.notNull(eaConfiguration);
				Validate.notNull(population);
				Validate.notNull(fitnessScore);
				Validate.isTrue(numIndividuals > 0);
				Validate.isTrue(population.size() == fitnessScore.size());

				switch (eaConfiguration.optimization()) {
				case MAXIMZE:
				case MINIMIZE:
					break;
				default:
					throw new IllegalArgumentException("Unsupported optimization " + eaConfiguration.optimization());
				}

				final Population<T> selectedIndividuals = new Population<>();

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
				final double[] probabilities = new double[population.size()];

				for (int i = 0; i < population.size(); i++) {
					if (eaConfiguration.optimization().equals(Optimization.MAXIMZE)) {
						sumFitness += fitnessScore.get(i).doubleValue();
					} else {
						sumFitness += reversedBase - fitnessScore.get(i).doubleValue();
					}
					probabilities[i] = sumFitness;
				}

				for (int i = 0; i < numIndividuals; i++) {
					final double targetScore = randomGenerator.nextDouble() * sumFitness;

					int index = 0;
					while (probabilities[index] < targetScore) {
						index++;
					}

					selectedIndividuals.add(population.get(index), fitnessScore.get(index));
				}

				return selectedIndividuals;
			}
		};
	}
}