package net.bmahe.genetics4j.core.spec.chromosome;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class BitChromosomeSpec implements ChromosomeSpec{

	@Value.Parameter
	public abstract int numBits();
	
	@Value.Check
	protected void check() {
		Validate.isTrue(numBits() > 0);
	}
}