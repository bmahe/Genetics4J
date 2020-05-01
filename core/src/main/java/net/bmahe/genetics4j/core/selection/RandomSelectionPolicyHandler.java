package net.bmahe.genetics4j.core.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class RandomSelectionPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {

	private final Random random;

	public RandomSelectionPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof RandomSelectionPolicy;
	}

	@Override
	public Selector<T> resolve(EAExecutionContext<T> eaExecutionContext, EAConfiguration<T> eaConfiguration,
			SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(RandomSelectionPolicy.class, selectionPolicy);

		return new Selector<T>() {

			@Override
			public List<Genotype> select(EAConfiguration<T> eaConfiguration, int numIndividuals, Genotype[] population,
					List<T> fitnessScore) {
				Validate.notNull(eaConfiguration);
				Validate.notNull(population);
				Validate.notNull(fitnessScore);
				Validate.isTrue(numIndividuals > 0);
				Validate.isTrue(population.length > 0);
				Validate.isTrue(fitnessScore.size() > 0);
				Validate.isTrue(fitnessScore.size() == population.length);

				final List<Genotype> selected = new ArrayList<>(numIndividuals);

				for (int i = 0; i < numIndividuals; i++) {
					final int selectedIndex = random.nextInt(population.length);
					selected.add(population[selectedIndex]);
				}

				return selected;
			}
		};
	}
}