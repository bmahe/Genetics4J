package net.bmahe.genetics4j.core.util;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;

public class MultiIntCounter {

	final int[] indices;
	final int[] maxIndices;

	public MultiIntCounter(final int... maxIndices) {
		Validate.notNull(maxIndices);
		Validate.isTrue(maxIndices.length > 0);

		for (int i = 0; i < maxIndices.length; i++) {
			final int maxIndex = maxIndices[i];
			Validate.isTrue(maxIndex > 0);
		}

		this.indices = new int[maxIndices.length];
		this.maxIndices = Arrays.copyOf(maxIndices, maxIndices.length);
	}

	public int[] getIndices() {
		return indices;
	}

	public int getIndex(final int index) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < indices.length);

		return indices[index];
	}

	public int[] getMaxIndices() {
		return maxIndices;
	}

	public int getTotal() {
		int total = 1;
		for (int i : maxIndices) {
			total *= i;
		}
		return total;
	}

	public boolean hasNext() {

		/**
		 * Precondition check if we tried all the combinations
		 */
		boolean allToTheMax = false;
		for (int i = 0; i < indices.length && !allToTheMax; i++) {
			if (indices[i] >= maxIndices[i]) {
				allToTheMax = true;
			}
		}
		return allToTheMax == false;
	}

	/**
	 * 
	 * @param indices
	 * @param maxIndices
	 * @return true if indices was successfully updates; false if there are no new
	 *         cases
	 */
	public int[] next() {

		Validate.isTrue(hasNext());

		boolean carryOver = true;
		int currentIndex = 0;
		while (carryOver && currentIndex < indices.length) {

			indices[currentIndex] += 1;

			if (indices[currentIndex] >= maxIndices[currentIndex] && currentIndex < indices.length - 1) {
				indices[currentIndex] = 0;

				for (int j = 0; j < currentIndex; j++) {
					indices[j] = 0;
				}

				currentIndex++;
				carryOver = true;
			} else {
				carryOver = false;
			}

		}

		return indices;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(indices);
		result = prime * result + Arrays.hashCode(maxIndices);
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
		MultiIntCounter other = (MultiIntCounter) obj;
		if (!Arrays.equals(indices, other.indices))
			return false;
		if (!Arrays.equals(maxIndices, other.maxIndices))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MultiIntCounter [indices=" + Arrays.toString(indices) + ", maxIndices=" + Arrays.toString(maxIndices)
				+ "]";
	}
}