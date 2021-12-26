package net.bmahe.genetics4j.core.selection;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.MultiSelections;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class MultiSelectionsPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {

	public MultiSelectionsPolicyHandler() {
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof MultiSelections;
	}

	@Override
	public Selector<T> resolve(final EAExecutionContext<T> eaExecutionContext, final AbstractEAConfiguration<T> eaConfiguration,
			final SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver,
			final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(MultiSelections.class, selectionPolicy);

		final MultiSelections multiSelections = (MultiSelections) selectionPolicy;
		final List<SelectionPolicy> selectionPolicies = multiSelections.selectionPolicies();
		Validate.isTrue(selectionPolicies.isEmpty() == false);

		final List<Selector<T>> selectors = selectionPolicies.stream().map((sp) -> {

			final SelectionPolicyHandler<T> spHandler = selectionPolicyHandlerResolver.resolve(sp);
			return spHandler.resolve(eaExecutionContext, eaConfiguration, selectionPolicyHandlerResolver, sp);
		}).collect(Collectors.toList());

		return new Selector<T>() {

			@Override
			public Population<T> select(AbstractEAConfiguration<T> eaConfiguration, int numIndividuals,
					List<Genotype> population, List<T> fitnessScore) {
				final int incrementSelection = numIndividuals / selectors.size();

				final Population<T> selectedIndividuals = new Population<>();
				for (final Selector<T> selector : selectors) {
					selectedIndividuals
							.addAll(selector.select(eaConfiguration, incrementSelection, population, fitnessScore));
				}

				int i = 0;
				while (selectedIndividuals.size() < numIndividuals) {

					selectedIndividuals.addAll(
							selectors.get(i).select(eaConfiguration, incrementSelection, population, fitnessScore));

					i = (i + 1) % selectors.size();
				}

				return selectedIndividuals;
			}
		};
	}
}