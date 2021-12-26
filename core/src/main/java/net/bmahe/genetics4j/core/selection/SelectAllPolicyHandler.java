package net.bmahe.genetics4j.core.selection;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.selection.SelectAll;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;

public class SelectAllPolicyHandler<T extends Comparable<T>> implements SelectionPolicyHandler<T> {

	public SelectAllPolicyHandler() {
	}

	@Override
	public boolean canHandle(final SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		return selectionPolicy instanceof SelectAll;
	}

	@Override
	public Selector<T> resolve(EAExecutionContext<T> eaExecutionContext, AbstractEAConfiguration<T> eaConfiguration,
			SelectionPolicyHandlerResolver<T> selectionPolicyHandlerResolver, SelectionPolicy selectionPolicy) {
		Validate.notNull(selectionPolicy);
		Validate.isInstanceOf(SelectAll.class, selectionPolicy);

		return new Selector<T>() {

			@Override
			public Population<T> select(AbstractEAConfiguration<T> eaConfiguration, int numIndividuals,
					List<Genotype> population, List<T> fitnessScore) {
				Validate.notNull(eaConfiguration);
				Validate.notNull(population);
				Validate.notNull(fitnessScore);
				Validate.isTrue(numIndividuals > 0);
				Validate.isTrue(population.size() > 0);
				Validate.isTrue(fitnessScore.size() > 0);
				Validate.isTrue(fitnessScore.size() == population.size());

				final Population<T> selected = new Population<>();

				int i = 0;
				while (selected.size() < numIndividuals) {
					selected.add(population.get(i), fitnessScore.get(i));

					i = (i + 1) % population.size();
				}

				return selected;
			}
		};
	}
}