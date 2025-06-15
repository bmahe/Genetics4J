package net.bmahe.genetics4j.core.chromosomes;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;

/**
 * A chromosome implementation that represents genetic information as an array of integer values.
 * 
 * <p>IntChromosome is widely used for discrete optimization problems where solutions can be
 * encoded as sequences of integer values within specified ranges. Each position in the array
 * represents a gene, and the integer value at that position represents the allele.
 * 
 * <p>This chromosome type is particularly suitable for:
 * <ul>
 * <li><strong>Combinatorial optimization</strong>: Problems with discrete decision variables</li>
 * <li><strong>Parameter optimization</strong>: Integer hyperparameters, configuration settings</li>
 * <li><strong>Permutation encoding</strong>: When combined with appropriate operators</li>
 * <li><strong>Resource allocation</strong>: Assignment and scheduling problems</li>
 * <li><strong>Graph problems</strong>: Node labeling, path encoding</li>
 * </ul>
 * 
 * <p>Key features:
 * <ul>
 * <li><strong>Bounded values</strong>: All integers are constrained to [minValue, maxValue]</li>
 * <li><strong>Fixed length</strong>: Chromosome size is determined at creation time</li>
 * <li><strong>Immutable</strong>: Values cannot be changed after construction</li>
 * <li><strong>Type-safe</strong>: Compile-time guarantees for integer operations</li>
 * </ul>
 * 
 * <p>The chromosome maintains bounds information which is used by genetic operators
 * to ensure that crossover and mutation operations produce valid offspring within
 * the specified constraints.
 * 
 * @see Chromosome
 * @see net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec
 * @see net.bmahe.genetics4j.core.chromosomes.factory.IntChromosomeFactory
 */
public class IntChromosome implements Chromosome {

	private final int size;
	private final int minValue;
	private final int maxValue;
	private final int[] values;

	/**
	 * Creates a new integer chromosome with the specified parameters and values.
	 * 
	 * @param _size the number of integer values in this chromosome
	 * @param _minValue the minimum allowed value for any integer in this chromosome
	 * @param _maxValue the maximum allowed value for any integer in this chromosome
	 * @param _values the array of integer values for this chromosome
	 * @throws IllegalArgumentException if size is not positive, if minValue > maxValue,
	 *                                  if values array is null, or if the array length
	 *                                  doesn't match the specified size
	 */
	public IntChromosome(final int _size, final int _minValue, final int _maxValue, final int[] _values) {
		Validate.isTrue(_size > 0);
		Validate.isTrue(_minValue <= _maxValue);
		Validate.notNull(_values);
		Validate.isTrue(_size == _values.length, "Provided size does not match the size of the content");

		this.size = _size;
		this.minValue = _minValue;
		this.maxValue = _maxValue;
		this.values = Arrays.copyOf(_values, _size);
	}

	@Override
	public int getNumAlleles() {
		return size;
	}

	/**
	 * Returns the integer value at the specified index.
	 * 
	 * @param index the index of the allele to retrieve (0-based)
	 * @return the integer value at the specified position
	 * @throws IllegalArgumentException if index is negative or greater than or equal to the chromosome size
	 */
	public int getAllele(final int index) {
		Validate.inclusiveBetween(0, size - 1, index);

		return values[index];
	}

	/**
	 * Returns the number of integer values in this chromosome.
	 * 
	 * @return the chromosome size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the minimum allowed value for integers in this chromosome.
	 * 
	 * @return the minimum value constraint
	 */
	public int getMinValue() {
		return minValue;
	}

	/**
	 * Returns the maximum allowed value for integers in this chromosome.
	 * 
	 * @return the maximum value constraint
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * Returns a copy of the integer values in this chromosome.
	 * 
	 * <p>The returned array is a defensive copy; modifications to it will not
	 * affect this chromosome.
	 * 
	 * @return a copy of the integer values array
	 */
	public int[] getValues() {
		return values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxValue;
		result = prime * result + minValue;
		result = prime * result + size;
		result = prime * result + Arrays.hashCode(values);
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
		IntChromosome other = (IntChromosome) obj;
		if (maxValue != other.maxValue)
			return false;
		if (minValue != other.minValue)
			return false;
		if (size != other.size)
			return false;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IntChromosome [size=" + size + ", minValue=" + minValue + ", maxValue=" + maxValue + ", values="
				+ Arrays.toString(values) + "]";
	}
}