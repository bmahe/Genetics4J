package net.bmahe.genetics4j.core.replacement;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.replacement.Elitism;

public class ElitismImpl<T extends Comparable<T>> implements ReplacementStrategyImplementor<T> {
	final static public Logger logger = LogManager.getLogger(ElitismImpl.class);

	private final Elitism elitismSpec;
	private final Selector<T> offspringSelector;
	private final Selector<T> survivorSelector;

	public ElitismImpl(final Elitism _elistismSpec, final Selector<T> _offspringSelector,
			final Selector<T> _survivorSelector) {
		Validate.notNull(_elistismSpec);
		Validate.notNull(_offspringSelector);
		Validate.notNull(_survivorSelector);

		this.elitismSpec = _elistismSpec;
		this.offspringSelector = _offspringSelector;
		this.survivorSelector = _survivorSelector;
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

		final int offspringNeeded = (int) (elitismSpec.offspringRatio() * numIndividuals);
		final int survivorNeeded = numIndividuals - offspringNeeded;

		logger.debug("We have {} individuals requested and an offspring ratio of {}",
				numIndividuals,
				elitismSpec.offspringRatio());

		final Population<T> selected = new Population<>();

		logger.info("Selecting {} offsprings", offspringNeeded);
		final Population<T> selectedOffspring = offspringSelector
				.select(eaConfiguration, offspringNeeded, offsprings, offspringScores);
		selected.addAll(selectedOffspring);

		logger.info("Selecting {} survivors", survivorNeeded);
		final Population<T> selectedSurvivors = survivorSelector
				.select(eaConfiguration, survivorNeeded, population, populationScores);
		selected.addAll(selectedSurvivors);

		return selected;
	}

}