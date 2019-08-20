package net.bmahe.genetics4j.core.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class RandomSelectionPolicyHandler implements SelectionPolicyHandler {

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
	public List<Genotype> select(final GenotypeSpec genotypeSpec, final SelectionPolicy selectionPolicy,
			final int numParent, final Genotype[] population, final double[] fitnessScore) {
		Validate.notNull(genotypeSpec);
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(RandomSelectionPolicy.class, selectionPolicy);
		Validate.notNull(population);
		Validate.notNull(fitnessScore);
		Validate.isTrue(numParent > 0);
		Validate.isTrue(population.length > 0);
		Validate.isTrue(fitnessScore.length > 0);
		Validate.isTrue(fitnessScore.length == population.length);

		final List<Genotype> selected = new ArrayList<>(numParent);

		for (int i = 0; i < numParent; i++) {
			final int selectedIndex = random.nextInt(population.length);
			selected.add(population[selectedIndex]);
		}

		return selected;
	}
}