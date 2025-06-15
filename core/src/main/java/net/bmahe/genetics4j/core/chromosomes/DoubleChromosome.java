package net.bmahe.genetics4j.core.chromosomes;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * A chromosome implementation that represents genetic information as an array of double-precision floating-point values.
 * 
 * <p>DoubleChromosome is ideal for continuous optimization problems where solutions can be encoded as
 * real-valued vectors. This chromosome type provides high precision for numerical optimization tasks
 * and is commonly used in function optimization, neural network weight evolution, and parameter tuning.
 * 
 * <p>This chromosome type is particularly suitable for:
 * <ul>
 * <li><strong>Continuous optimization</strong>: Function minimization/maximization with real-valued parameters</li>
 * <li><strong>Neural network evolution</strong>: Evolving connection weights and biases</li>
 * <li><strong>Engineering optimization</strong>: Design parameters with continuous constraints</li>
 * <li><strong>Scientific computing</strong>: Model parameter estimation and calibration</li>
 * <li><strong>Financial modeling</strong>: Portfolio optimization and risk parameter tuning</li>
 * <li><strong>Machine learning</strong>: Hyperparameter optimization for continuous parameters</li>
 * </ul>
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>High precision</strong>: 64-bit floating-point representation for accurate computations</li>
 * <li><strong>Bounded values</strong>: All doubles are constrained to [minValue, maxValue]</li>
 * <li><strong>Fixed length</strong>: Chromosome size is determined at creation time</li>
 * <li><strong>Immutable</strong>: Values cannot be changed after construction</li>
 * <li><strong>IEEE 754 compliant</strong>: Standard floating-point arithmetic and comparisons</li>
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
 * <p>Special considerations for floating-point chromosomes:
 * <ul>
 * <li><strong>Precision handling</strong>: Be aware of floating-point precision limits</li>
 * <li><strong>Boundary conditions</strong>: Handle edge cases at min/max values</li>
 * <li><strong>Convergence</strong>: May require epsilon-based convergence criteria</li>
 * <li><strong>Scaling</strong>: Consider normalizing parameters for better performance</li>
 * </ul>
 * 
 * @see Chromosome
 * @see net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec
 * @see net.bmahe.genetics4j.core.chromosomes.factory.DoubleChromosomeFactory
 * @see FloatChromosome
 */
public class DoubleChromosome implements Chromosome {

	private final int size;
	private final double minValue;
	private final double maxValue;
	private final double[] values;

	/**
	 * Creates a new double chromosome with the specified parameters and values.
	 * 
	 * @param _size the number of double values in this chromosome
	 * @param _minValue the minimum allowed value for any double in this chromosome
	 * @param _maxValue the maximum allowed value for any double in this chromosome
	 * @param _values the array of double values for this chromosome
	 * @throws IllegalArgumentException if size is not positive, if minValue > maxValue,
	 *                                  if values array is null, or if the array length
	 *                                  doesn't match the specified size
	 */
	public DoubleChromosome(final int _size, final double _minValue, final double _maxValue, final double[] _values) {
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
	 * Returns the double value at the specified index.
	 * 
	 * @param index the index of the allele to retrieve (0-based)
	 * @return the double value at the specified position
	 * @throws IllegalArgumentException if index is negative or greater than or equal to the chromosome size
	 */
	public double getAllele(final int index) {
		Validate.inclusiveBetween(0, size - 1, index);

		return values[index];
	}

	/**
	 * Returns the number of double values in this chromosome.
	 * 
	 * @return the chromosome size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the minimum allowed value for doubles in this chromosome.
	 * 
	 * @return the minimum value constraint
	 */
	public double getMinValue() {
		return minValue;
	}

	/**
	 * Returns the maximum allowed value for doubles in this chromosome.
	 * 
	 * @return the maximum value constraint
	 */
	public double getMaxValue() {
		return maxValue;
	}

	/**
	 * Returns a copy of the double values in this chromosome.
	 * 
	 * <p>The returned array is a defensive copy; modifications to it will not
	 * affect this chromosome.
	 * 
	 * @return a copy of the double values array
	 */
	public double[] getValues() {
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
		DoubleChromosome other = (DoubleChromosome) obj;
		return Double.doubleToLongBits(maxValue) == Double.doubleToLongBits(other.maxValue)
				&& Double.doubleToLongBits(minValue) == Double.doubleToLongBits(other.minValue) && size == other.size
				&& Arrays.equals(values, other.values);
	}

	@Override
	public String toString() {
		return "DoubleChromosome [size=" + size + ", minValue=" + minValue + ", maxValue=" + maxValue + ", values="
				+ Arrays.toString(values) + "]";
	}
}