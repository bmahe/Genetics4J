package net.bmahe.genetics4j.core;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;

/**
 * Represents a genotype in an evolutionary algorithm, which is a collection of chromosomes.
 * 
 * <p>A genotype encapsulates the complete genetic representation of an individual solution,
 * consisting of one or more chromosomes that together define the individual's characteristics.
 * Each chromosome may represent different aspects or components of the solution space.
 * 
 * <p>Genotypes are immutable once created and provide type-safe access to their constituent chromosomes.
 * 
 * @see Chromosome
 * @see Individual
 */
public class Genotype {

	private final Chromosome[] chromosomes;

	/**
	 * Creates a new genotype with the specified chromosomes.
	 * 
	 * @param _chromosomes one or more chromosomes to include in this genotype
	 * @throws IllegalArgumentException if chromosomes array is null or empty
	 */
	public Genotype(final Chromosome... _chromosomes) {
		Validate.notNull(_chromosomes);
		Validate.isTrue(_chromosomes.length > 0);

		this.chromosomes = _chromosomes;
	}

	/**
	 * Creates a new genotype with chromosomes from the specified collection.
	 * 
	 * @param _chromosomes a collection of chromosomes to include in this genotype
	 * @throws IllegalArgumentException if chromosomes collection is null or empty
	 */
	public Genotype(final Collection<Chromosome> _chromosomes) {
		Validate.notNull(_chromosomes);
		Validate.isTrue(_chromosomes.size() > 0);

		final Chromosome[] chromosomesArray = _chromosomes.toArray(new Chromosome[_chromosomes.size()]);

		this.chromosomes = chromosomesArray;
	}

	/**
	 * Returns the number of chromosomes in this genotype.
	 * 
	 * @return the count of chromosomes
	 */
	public int getSize() {
		return chromosomes.length;
	}

	/**
	 * Returns all chromosomes in this genotype.
	 * 
	 * @return an array containing all chromosomes
	 */
	public Chromosome[] getChromosomes() {
		return chromosomes;
	}

	/**
	 * Returns the chromosome at the specified index.
	 * 
	 * @param index the index of the chromosome to retrieve (0-based)
	 * @return the chromosome at the specified index
	 * @throws IllegalArgumentException if index is negative or greater than or equal to the number of chromosomes
	 */
	public Chromosome getChromosome(final int index) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < chromosomes.length);

		return chromosomes[index];
	}

	/**
	 * Returns the chromosome at the specified index, cast to the specified type.
	 * 
	 * @param <T> the expected chromosome type
	 * @param index the index of the chromosome to retrieve (0-based)
	 * @param clazz the class to cast the chromosome to
	 * @return the chromosome at the specified index, cast to the specified type
	 * @throws IllegalArgumentException if index is invalid or clazz is null
	 * @throws ClassCastException if the chromosome cannot be cast to the specified type
	 */
	public <T extends Chromosome> T getChromosome(final int index, Class<T> clazz) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < chromosomes.length,
				"Index (%d) larger than the number of chromosomes (%d)",
				index,
				chromosomes.length);
		Validate.notNull(clazz);

		return clazz.cast(chromosomes[index]);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(chromosomes);
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
		Genotype other = (Genotype) obj;
		if (!Arrays.equals(chromosomes, other.chromosomes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Genotype [chromosomes=" + Arrays.toString(chromosomes) + "]";
	}
}