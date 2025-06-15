package net.bmahe.genetics4j.core.chromosomes;

import java.util.BitSet;

import org.apache.commons.lang3.Validate;

/**
 * A chromosome implementation that represents genetic information as a sequence of bits.
 * 
 * <p>BitChromosome is commonly used for binary optimization problems, feature selection,
 * and any application where the solution can be encoded as a bit string. Each bit (allele)
 * can be either 0 or 1, representing boolean choices or binary features.
 * 
 * <p>This implementation is immutable and uses a {@link BitSet} for efficient storage
 * and manipulation of the bit sequence.
 * 
 * <p>Common use cases include:
 * <ul>
 * <li>Binary optimization problems (knapsack, subset selection)</li>
 * <li>Feature selection in machine learning</li>
 * <li>Boolean satisfiability problems</li>
 * <li>Circuit design and logic optimization</li>
 * </ul>
 * 
 * @see Chromosome
 * @see java.util.BitSet
 */
public class BitChromosome implements Chromosome {

	private final int numBits;
	private final BitSet bitSet;

	/**
	 * Creates a new bit chromosome with the specified number of bits and initial values.
	 * 
	 * @param _numBits the number of bits in this chromosome, must be positive
	 * @param _bitSet the initial bit values for this chromosome
	 * @throws IllegalArgumentException if numBits is zero or negative, if bitSet is null,
	 *                                  or if numBits exceeds the bitSet size
	 */
	public BitChromosome(final int _numBits, final BitSet _bitSet) {
		Validate.isTrue(_numBits > 0, "numBits can't be zero or negative");
		Validate.notNull(_bitSet);
		Validate.isTrue(_numBits <= _bitSet.size());

		this.numBits = _numBits;
		this.bitSet = new BitSet(numBits);
		this.bitSet.or(_bitSet);
	}

	@Override
	public int getNumAlleles() {
		return numBits;
	}

	/**
	 * Returns the bit value at the specified index.
	 * 
	 * @param index the index of the bit to retrieve (0-based)
	 * @return {@code true} if the bit is set (1), {@code false} if clear (0)
	 * @throws IllegalArgumentException if index is negative or greater than or equal to numBits
	 */
	public boolean getBit(final int index) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < numBits);

		return bitSet.get(index);
	}

	/**
	 * Returns the underlying BitSet containing all bit values.
	 * 
	 * <p>The returned BitSet is a copy and modifications to it will not affect this chromosome.
	 * 
	 * @return a BitSet containing the bit values of this chromosome
	 */
	public BitSet getBitSet() {
		return bitSet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bitSet == null) ? 0 : bitSet.hashCode());
		result = prime * result + numBits;
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
		BitChromosome other = (BitChromosome) obj;
		if (bitSet == null) {
			if (other.bitSet != null)
				return false;
		} else if (!bitSet.equals(other.bitSet))
			return false;
		if (numBits != other.numBits)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BitChromosome [numBits=" + numBits + ", bitSet=" + bitSet + "]";
	}
}