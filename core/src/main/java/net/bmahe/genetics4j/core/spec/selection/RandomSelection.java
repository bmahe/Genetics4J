package net.bmahe.genetics4j.core.spec.selection;

import org.immutables.value.Value;

@Value.Immutable
public abstract class RandomSelection implements SelectionPolicy {

	public static RandomSelection build() {
		return new RandomSelection() {
		};
	}
}