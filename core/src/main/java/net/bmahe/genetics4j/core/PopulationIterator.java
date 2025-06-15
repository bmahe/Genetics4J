package net.bmahe.genetics4j.core;

import java.util.Iterator;

import org.apache.commons.lang3.Validate;

/**
 * Iterator implementation for traversing individuals in a population during evolutionary algorithms.
 * 
 * <p>PopulationIterator provides a standard Java Iterator interface for accessing individuals
 * in a {@link Population}, combining genotypes with their corresponding fitness values to create
 * complete {@link Individual} instances during iteration.
 * 
 * <p>This iterator enables convenient traversal patterns such as:
 * <ul>
 * <li><strong>Enhanced for loops</strong>: Iterate over individuals using for-each syntax</li>
 * <li><strong>Stream operations</strong>: Convert populations to streams for functional processing</li>
 * <li><strong>Sequential access</strong>: Process individuals one at a time without loading all into memory</li>
 * <li><strong>Collection integration</strong>: Use with Java Collection framework methods</li>
 * </ul>
 * 
 * <p>The iterator maintains internal state to track the current position and constructs
 * {@link Individual} objects on-demand by combining genotypes and fitness values from the
 * underlying population at the same index.
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Type safety</strong>: Parameterized with fitness type for compile-time type checking</li>
 * <li><strong>Lazy evaluation</strong>: Creates Individual objects only when requested</li>
 * <li><strong>Memory efficient</strong>: Doesn't duplicate population data, references original</li>
 * <li><strong>Standard interface</strong>: Implements Java Iterator contract completely</li>
 * </ul>
 * 
 * <p>Usage patterns:
 * <pre>{@code
 * // Enhanced for loop iteration
 * Population<Double> population = getPopulation();
 * for (Individual<Double> individual : population) {
 *     System.out.println("Fitness: " + individual.fitness());
 * }
 * 
 * // Stream-based processing
 * population.stream()
 *     .filter(individual -> individual.fitness() > threshold)
 *     .mapToDouble(Individual::fitness)
 *     .average();
 * 
 * // Manual iteration
 * Iterator<Individual<Double>> iterator = population.iterator();
 * while (iterator.hasNext()) {
 *     Individual<Double> individual = iterator.next();
 *     processIndividual(individual);
 * }
 * }</pre>
 * 
 * <p>Thread safety considerations:
 * <ul>
 * <li><strong>Single-threaded use</strong>: Iterator instances are not thread-safe</li>
 * <li><strong>Population stability</strong>: Underlying population should not be modified during iteration</li>
 * <li><strong>Concurrent iterations</strong>: Multiple iterators can be created for the same population</li>
 * </ul>
 * 
 * @param <T> the type of fitness values in the population, must be comparable for selection operations
 * @see Population
 * @see Individual
 * @see java.util.Iterator
 */
public class PopulationIterator<T extends Comparable<T>> implements Iterator<Individual<T>> {

	private final Population<T> population;

	private int currentIndex = 0;

	/**
	 * Constructs a new iterator for the specified population.
	 * 
	 * <p>Creates an iterator that will traverse all individuals in the given population,
	 * starting from index 0 and proceeding sequentially through all individuals.
	 * 
	 * @param _population the population to iterate over
	 * @throws IllegalArgumentException if the population is null
	 */
	public PopulationIterator(final Population<T> _population) {
		Validate.notNull(_population);

		this.population = _population;
	}

	/**
	 * Returns {@code true} if there are more individuals to iterate over.
	 * 
	 * <p>Checks whether the current position is within the bounds of the population.
	 * This method can be called multiple times without advancing the iterator position.
	 * 
	 * @return {@code true} if there are more individuals, {@code false} if all have been visited
	 */
	@Override
	public boolean hasNext() {
		return currentIndex < population.size();
	}

	/**
	 * Returns the next individual in the population and advances the iterator position.
	 * 
	 * <p>Constructs an {@link Individual} by combining the genotype and fitness value
	 * at the current position, then advances to the next position for subsequent calls.
	 * 
	 * @return the next individual in the iteration sequence
	 * @throws java.util.NoSuchElementException if there are no more individuals (when {@link #hasNext()} returns false)
	 */
	@Override
	public Individual<T> next() {
		final Genotype genotype = population.getGenotype(currentIndex);
		final T fitness = population.getFitness(currentIndex);
		currentIndex++;
		return Individual.of(genotype, fitness);
	}
}