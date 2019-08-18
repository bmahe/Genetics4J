package net.bmahe.genetics4j.core.chromosomes;

import java.util.BitSet;

import org.apache.commons.lang3.Validate;

public class BitChromosome implements Chromosome {

	private final int numBits;
	private final BitSet bitSet;

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

	public boolean getBit(final int index) {
		Validate.isTrue(index >= 0);
		Validate.isTrue(index < numBits);

		return bitSet.get(index);
	}

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