package net.bmahe.genetics4j.core.chromosomes;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

public class DoubleChromosome implements Chromosome {

	private final int size;
	private final double minValue;
	private final double maxValue;
	private final double[] values;

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

	public double getAllele(final int index) {
		Validate.inclusiveBetween(0, size - 1, index);

		return values[index];
	}

	public int getSize() {
		return size;
	}

	public double getMinValue() {
		return minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

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