package net.bmahe.genetics4j.core.chromosomes;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

public class FloatChromosome implements Chromosome {

	private final int size;
	private final float minValue;
	private final float maxValue;
	private final float[] values;

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

	public float getAllele(final int index) {
		Validate.inclusiveBetween(0, size - 1, index);

		return values[index];
	}

	public int getSize() {
		return size;
	}

	public float getMinValue() {
		return minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

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