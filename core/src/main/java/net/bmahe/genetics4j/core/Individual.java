package net.bmahe.genetics4j.core;

import org.immutables.value.Value;

/**
 * Represents an individual in an evolutionary algorithm, consisting of a genotype and its associated fitness value.
 * 
 * <p>This interface encapsulates the fundamental concept of an individual solution in evolutionary computation,
 * combining genetic representation (genotype) with its evaluated performance (fitness).
 * 
 * @param <T> the type of the fitness value, must be comparable for selection and ranking purposes
 */
@Value.Immutable
public interface Individual<T extends Comparable<T>> {

	/**
	 * Returns the genotype of this individual.
	 * 
	 * @return the genetic representation containing chromosomes that define this individual's traits
	 */
	@Value.Parameter
	Genotype genotype();

	/**
	 * Returns the fitness value of this individual.
	 * 
	 * @return the evaluated performance or quality measure of this individual's genotype
	 */
	@Value.Parameter
	T fitness();

	/**
	 * Creates a new individual with the specified genotype and fitness.
	 * 
	 * @param <U> the type of the fitness value
	 * @param genotype the genetic representation of the individual
	 * @param fitness the evaluated fitness value of the individual
	 * @return a new Individual instance with the given genotype and fitness
	 */
	static <U extends Comparable<U>> Individual<U> of(final Genotype genotype, final U fitness) {
		return ImmutableIndividual.of(genotype, fitness);
	}
}