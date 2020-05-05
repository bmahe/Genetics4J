package net.bmahe.genetics4j.core.evolutionstrategy;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.evolutionstrategy.GenerationalReplacement;

public class GenerationalReplacementImpl<T extends Comparable<T>> implements EvolutionStrategyImplementor<T> {

	private final GenerationalReplacement generationalReplacementSpec;
	private final Selector<T> offspringSelector;

	public GenerationalReplacementImpl(final GenerationalReplacement _elistismSpec,
			final Selector<T> _offspringSelector) {
		Validate.notNull(_elistismSpec);

		this.generationalReplacementSpec = _elistismSpec;
		this.offspringSelector = _offspringSelector;
	}

	@Override
	public Population<T> select(final EAConfiguration<T> eaConfiguration, final int numIndividuals,
			final List<Genotype> population, final List<T> populationScores, final List<Genotype> offsprings,
			final List<T> offspringScores) {
		Validate.notNull(eaConfiguration);
		Validate.isTrue(numIndividuals > 0);
		Validate.notNull(population);
		Validate.notNull(populationScores);
		Validate.isTrue(population.size() == populationScores.size());
		Validate.notNull(offsprings);
		Validate.notNull(offspringScores);
		Validate.isTrue(offsprings.size() == offspringScores.size());

		final Population<T> selected = new Population<>();

		final Population<T> selectedOffspring = offspringSelector
				.select(eaConfiguration, numIndividuals, offsprings, offspringScores);
		selected.addAll(selectedOffspring);

		return selected;
	}
}