package net.bmahe.genetics4j.core.chromosomes;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;

public class IntChromosome implements Chromosome {

	private final int size;
	private final int minValue;
	private final int maxValue;
	private final int[] values;

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

	public int getAllele(final int index) {
		Validate.inclusiveBetween(0, size - 1, index);

		return values[index];
	}

	public int getSize() {
		return size;
	}

	public int getMinValue() {
		return minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

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