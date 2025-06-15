package net.bmahe.genetics4j.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * Represents a population of individuals in an evolutionary algorithm.
 * 
 * <p>A population is a collection of {@link Individual}s, each consisting of a genotype and its associated fitness.
 * This class provides methods to manage, access, and iterate over the individuals in the population.
 * 
 * <p>Populations are mutable and support adding individuals, either individually or in bulk from other populations.
 * The class maintains parallel lists of genotypes and fitnesses for efficient access.
 * 
 * @param <T> the type of the fitness values, must be comparable for selection operations
 * @see Individual
 * @see Genotype
 */
public class Population<T extends Comparable<T>> implements Iterable<Individual<T>> {

	private List<Genotype> genotypes;
	private List<T> fitnesses;

	/**
	 * Creates an empty population.
	 */
	public Population() {
		this.genotypes = new ArrayList<>();
		this.fitnesses = new ArrayList<>();
	}

	/**
	 * Creates a population with the specified genotypes and fitnesses.
	 * 
	 * @param _genotype the list of genotypes for the population
	 * @param _fitnesses the list of fitness values corresponding to the genotypes
	 * @throws IllegalArgumentException if genotypes or fitnesses are null, or if their sizes don't match
	 */
	public Population(final List<Genotype> _genotype, final List<T> _fitnesses) {
		Validate.notNull(_genotype);
		Validate.notNull(_fitnesses);
		Validate.isTrue(_genotype.size() == _fitnesses.size(),
				"Size of genotype (%d) does not match size of fitnesses (%d)",
				_genotype.size(),
				_fitnesses.size());

		this.genotypes = new ArrayList<Genotype>(_genotype);
		this.fitnesses = new ArrayList<>(_fitnesses);
	}

	/**
	 * Adds an individual to the population by specifying its genotype and fitness separately.
	 * 
	 * @param genotype the genotype of the individual to add
	 * @param fitness the fitness value of the individual to add
	 * @throws IllegalArgumentException if genotype or fitness is null
	 */
	public void add(final Genotype genotype, final T fitness) {
		Validate.notNull(genotype);
		Validate.notNull(fitness);

		genotypes.add(genotype);
		fitnesses.add(fitness);
	}

	/**
	 * Adds an individual to the population.
	 * 
	 * @param individual the individual to add to the population
	 * @throws IllegalArgumentException if individual is null
	 */
	public void add(final Individual<T> individual) {
		Validate.notNull(individual);

		genotypes.add(individual.genotype());
		fitnesses.add(individual.fitness());
	}

	/**
	 * Adds all individuals from another population to this population.
	 * 
	 * @param population the population whose individuals should be added to this population
	 * @throws IllegalArgumentException if population is null
	 */
	public void addAll(final Population<T> population) {
		Validate.notNull(population);

		this.genotypes.addAll(population.getAllGenotypes());
		this.fitnesses.addAll(population.getAllFitnesses());
	}

	@Override
	public Iterator<Individual<T>> iterator() {
		return new PopulationIterator<>(this);
	}

	/**
	 * Returns the genotype at the specified index.
	 * 
	 * @param index the index of the genotype to retrieve (0-based)
	 * @return the genotype at the specified index
	 * @throws IllegalArgumentException if index is out of bounds
	 */
	public Genotype getGenotype(final int index) {
		Validate.inclusiveBetween(0, genotypes.size() - 1, index);

		return genotypes.get(index);
	}

	/**
	 * Returns the fitness value at the specified index.
	 * 
	 * @param index the index of the fitness value to retrieve (0-based)
	 * @return the fitness value at the specified index
	 * @throws IllegalArgumentException if index is out of bounds
	 */
	public T getFitness(final int index) {
		Validate.inclusiveBetween(0, fitnesses.size() - 1, index);

		return fitnesses.get(index);
	}

	/**
	 * Returns the individual at the specified index.
	 * 
	 * @param index the index of the individual to retrieve (0-based)
	 * @return the individual at the specified index, combining its genotype and fitness
	 * @throws IllegalArgumentException if index is out of bounds
	 */
	public Individual<T> getIndividual(final int index) {
		return Individual.of(getGenotype(index), getFitness(index));
	}

	/**
	 * Returns all genotypes in this population.
	 * 
	 * @return a list containing all genotypes in this population
	 */
	public List<Genotype> getAllGenotypes() {
		return genotypes;
	}

	/**
	 * Returns all fitness values in this population.
	 * 
	 * @return a list containing all fitness values in this population
	 */
	public List<T> getAllFitnesses() {
		return fitnesses;
	}

	/**
	 * Returns the number of individuals in this population.
	 * 
	 * @return the size of the population
	 */
	public int size() {
		return genotypes.size();
	}

	/**
	 * Checks if this population is empty.
	 * 
	 * @return {@code true} if the population contains no individuals, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fitnesses == null) ? 0 : fitnesses.hashCode());
		result = prime * result + ((genotypes == null) ? 0 : genotypes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		@SuppressWarnings("rawtypes")
		Population other = (Population) obj;
		if (fitnesses == null) {
			if (other.fitnesses != null)
				return false;
		} else if (!fitnesses.equals(other.fitnesses))
			return false;
		if (genotypes == null) {
			if (other.genotypes != null)
				return false;
		} else if (!genotypes.equals(other.genotypes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Population [genotypes=" + genotypes + ", fitnesses=" + fitnesses + "]";
	}

	/**
	 * Creates a new population with the specified genotypes and fitnesses.
	 * 
	 * @param <U> the type of the fitness values
	 * @param _genotype the list of genotypes for the population
	 * @param _fitnesses the list of fitness values corresponding to the genotypes
	 * @return a new population containing the specified genotypes and fitnesses
	 * @throws IllegalArgumentException if genotypes or fitnesses are null, or if their sizes don't match
	 */
	public static <U extends Comparable<U>> Population<U> of(final List<Genotype> _genotype, final List<U> _fitnesses) {
		return new Population<U>(_genotype, _fitnesses);
	}

	/**
	 * Creates a new population from a list of individuals.
	 * 
	 * @param <U> the type of the fitness values
	 * @param individuals the list of individuals to include in the population
	 * @return a new population containing the specified individuals
	 * @throws IllegalArgumentException if individuals list is null
	 */
	public static <U extends Comparable<U>> Population<U> of(final List<Individual<U>> individuals) {
		Validate.notNull(individuals);

		final List<Genotype> genotypes = individuals.stream()
				.map(Individual::genotype)
				.toList();

		final List<U> fitnesses = individuals.stream()
				.map(Individual::fitness)
				.toList();

		return new Population<U>(genotypes, fitnesses);
	}

	/**
	 * Creates an empty population.
	 * 
	 * @param <U> the type of the fitness values
	 * @return a new empty population
	 */
	public static <U extends Comparable<U>> Population<U> empty() {
		return new Population<U>(List.of(), List.of());
	}

}