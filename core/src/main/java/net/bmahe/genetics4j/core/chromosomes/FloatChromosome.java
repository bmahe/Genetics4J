package net.bmahe.genetics4j.core.chromosomes;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * A chromosome implementation that represents genetic information as an array of single-precision floating-point values.
 * 
 * <p>FloatChromosome provides a memory-efficient alternative to DoubleChromosome for continuous optimization
 * problems where single precision (32-bit) is sufficient. This chromosome type offers good performance for
 * real-valued optimization while using approximately half the memory of double-precision alternatives.
 * 
 * <p>This chromosome type is particularly suitable for:
 * <ul>
 * <li><strong>Large-scale optimization</strong>: Problems with thousands of parameters where memory efficiency matters</li>
 * <li><strong>Neural network evolution</strong>: Evolving weights when single precision is adequate</li>
 * <li><strong>Graphics and gaming</strong>: Position, rotation, and scaling parameters</li>
 * <li><strong>Signal processing</strong>: Audio and image processing parameter optimization</li>
 * <li><strong>Embedded systems</strong>: Resource-constrained environments with limited memory</li>
 * <li><strong>Real-time applications</strong>: Where performance is more critical than precision</li>
 * </ul>
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Memory efficient</strong>: 32-bit floating-point representation reduces memory usage</li>
 * <li><strong>Bounded values</strong>: All floats are constrained to [minValue, maxValue]</li>
 * <li><strong>Fixed length</strong>: Chromosome size is determined at creation time</li>
 * <li><strong>Immutable</strong>: Values cannot be changed after construction</li>
 * <li><strong>IEEE 754 compliant</strong>: Standard floating-point arithmetic and comparisons</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Memory usage</strong>: Approximately 50% less memory than DoubleChromosome</li>
 * <li><strong>Cache efficiency</strong>: Better cache utilization due to smaller data size</li>
 * <li><strong>Precision trade-off</strong>: ~7 decimal digits vs ~15 for double precision</li>
 * <li><strong>Range limitations</strong>: Smaller representable range than double precision</li>
 * </ul>
 * 
 * <p>The chromosome maintains bounds information which is used by genetic operators such as:
 * <ul>
 * <li><strong>Arithmetic crossover</strong>: Weighted averaging of parent values</li>
 * <li><strong>Gaussian mutation</strong>: Adding normally distributed noise</li>
 * <li><strong>Uniform mutation</strong>: Random replacement within bounds</li>
 * <li><strong>Creep mutation</strong>: Small incremental changes</li>
 * </ul>
 * 
 * <p>When to choose FloatChromosome vs DoubleChromosome:
 * <ul>
 * <li><strong>Use FloatChromosome</strong>: Large populations, memory constraints, adequate precision</li>
 * <li><strong>Use DoubleChromosome</strong>: High precision requirements, scientific computing</li>
 * </ul>
 * 
 * @see Chromosome
 * @see net.bmahe.genetics4j.core.spec.chromosome.FloatChromosomeSpec
 * @see net.bmahe.genetics4j.core.chromosomes.factory.FloatChromosomeFactory
 * @see DoubleChromosome
 */
public class FloatChromosome implements Chromosome {

	private final int size;
	private final float minValue;
	private final float maxValue;
	private final float[] values;

	/**
	 * Creates a new float chromosome with the specified parameters and values.
	 * 
	 * @param _size the number of float values in this chromosome
	 * @param _minValue the minimum allowed value for any float in this chromosome
	 * @param _maxValue the maximum allowed value for any float in this chromosome
	 * @param _values the array of float values for this chromosome
	 * @throws IllegalArgumentException if size is not positive, if minValue > maxValue,
	 *                                  if values array is null, or if the array length
	 *                                  doesn't match the specified size
	 */
	public FloatChromosome(final int _size, final float _minValue, final float _maxValue, final float[] _values) {
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
	 * Returns the float value at the specified index.
	 * 
	 * @param index the index of the allele to retrieve (0-based)
	 * @return the float value at the specified position
	 * @throws IllegalArgumentException if index is negative or greater than or equal to the chromosome size
	 */
	public float getAllele(final int index) {
		Validate.inclusiveBetween(0, size - 1, index);

		return values[index];
	}

	/**
	 * Returns the number of float values in this chromosome.
	 * 
	 * @return the chromosome size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the minimum allowed value for floats in this chromosome.
	 * 
	 * @return the minimum value constraint
	 */
	public float getMinValue() {
		return minValue;
	}

	/**
	 * Returns the maximum allowed value for floats in this chromosome.
	 * 
	 * @return the maximum value constraint
	 */
	public float getMaxValue() {
		return maxValue;
	}

	/**
	 * Returns a copy of the float values in this chromosome.
	 * 
	 * <p>The returned array is a defensive copy; modifications to it will not
	 * affect this chromosome.
	 * 
	 * @return a copy of the float values array
	 */
	public float[] getValues() {
		return values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		result = prime * result + Objects.hash(maxValue, minValue, size);
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
		FloatChromosome other = (FloatChromosome) obj;
		return Float.floatToIntBits(maxValue) == Float.floatToIntBits(other.maxValue)
				&& Float.floatToIntBits(minValue) == Float.floatToIntBits(other.minValue) && size == other.size
				&& Arrays.equals(values, other.values);
	}

	@Override
	public String toString() {
		return "FloatChromosome [size=" + size + ", minValue=" + minValue + ", maxValue=" + maxValue + ", values="
				+ Arrays.toString(values) + "]";
	}
}