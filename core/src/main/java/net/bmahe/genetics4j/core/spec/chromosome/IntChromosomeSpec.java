package net.bmahe.genetics4j.core.spec.chromosome;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class IntChromosomeSpec implements ChromosomeSpec {

	@Value.Parameter
	public abstract int size();

	@Value.Parameter
	public abstract int minValue();

	@Value.Parameter
	public abstract int maxValue();

	@Value.Check
	protected void check() {
		Validate.isTrue(size() > 0);
		Validate.isTrue(minValue() <= maxValue());
	}
}