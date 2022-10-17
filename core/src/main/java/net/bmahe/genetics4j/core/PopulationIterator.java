package net.bmahe.genetics4j.core;

import java.util.Iterator;

import org.apache.commons.lang3.Validate;

public class PopulationIterator<T extends Comparable<T>> implements Iterator<Individual<T>> {

	private final Population<T> population;

	private int currentIndex = 0;

	public PopulationIterator(final Population<T> _population) {
		Validate.notNull(_population);

		this.population = _population;
	}

	@Override
	public boolean hasNext() {
		return currentIndex < population.size();
	}

	@Override
	public Individual<T> next() {
		final Genotype genotype = population.getGenotype(currentIndex);
		final T fitness = population.getFitness(currentIndex);
		currentIndex++;
		return Individual.of(genotype, fitness);
	}
}