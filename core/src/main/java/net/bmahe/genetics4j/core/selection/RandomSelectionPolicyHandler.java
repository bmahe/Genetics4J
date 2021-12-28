package net.bmahe.genetics4j.core.selection;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class RandomSelectionPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {

	private final RandomGenerator randomGenerator;

	public RandomSelectionPolicyHandler(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof RandomSelection;
	}

	@Override
	public Selector<T> resolve(final AbstractEAExecutionContext<T> eaExecutionContext,
			final AbstractEAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(RandomSelection.class, selectionPolicy);

		return new Selector<T>() {

			@Override
			public Population<T> select(final AbstractEAConfiguration<T> eaConfiguration, final int numIndividuals,
					final List<Genotype> population, final List<T> fitnessScore) {
				Validate.notNull(eaConfiguration);
				Validate.notNull(population);
				Validate.notNull(fitnessScore);
				Validate.isTrue(numIndividuals > 0);
				Validate.isTrue(population.size() > 0);
				Validate.isTrue(fitnessScore.size() > 0);
				Validate.isTrue(fitnessScore.size() == population.size());

				final Population<T> selected = new Population<>();

				for (int i = 0; i < numIndividuals; i++) {
					final int selectedIndex = randomGenerator.nextInt(population.size());
					selected.add(population.get(selectedIndex), fitnessScore.get(selectedIndex));
				}
				return selected;
			}
		};
	}
}