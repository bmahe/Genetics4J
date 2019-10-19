package net.bmahe.genetics4j.core.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.selection.MultiSelections;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class MultiSelectionsPolicyHandler implements SelectionPolicyHandler {

	private final Random random;

	public MultiSelectionsPolicyHandler(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof MultiSelections;
	}

	@Override
	public Selector resolve(GeneticSystemDescriptor geneticSystemDescriptor, GenotypeSpec genotypeSpec,
			SelectionPolicyHandlerResolver selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(MultiSelections.class, selectionPolicy);

		final MultiSelections multiSelections = (MultiSelections) selectionPolicy;
		final List<SelectionPolicy> selectionPolicies = multiSelections.selectionPolicies();
		Validate.isTrue(selectionPolicies.isEmpty() == false);

		final List<Selector> selectors = selectionPolicies.stream()
				.map((sp) -> {

					final SelectionPolicyHandler spHandler = selectionPolicyHandlerResolver.resolve(sp);
					return spHandler.resolve(geneticSystemDescriptor, genotypeSpec, selectionPolicyHandlerResolver, sp);
				})
				.collect(Collectors.toList());

		return new Selector() {

			@Override
			public List<Genotype> select(GenotypeSpec genotypeSpec, int numIndividuals, Genotype[] population,
					double[] fitnessScore) {
				final int incrementSelection = numIndividuals / selectors.size();

				final List<Genotype> selectedIndividuals = new ArrayList<Genotype>();
				for (final Selector selector : selectors) {
					selectedIndividuals.addAll(selector.select(genotypeSpec, incrementSelection, population, fitnessScore));
				}

				int i = 0;
				while (selectedIndividuals.size() < numIndividuals) {

					selectedIndividuals.addAll(selectors.get(i)
							.select(genotypeSpec, incrementSelection, population, fitnessScore));

					i = (i + 1) % selectors.size();
				}

				return selectedIndividuals;
			}
		};
	}
}