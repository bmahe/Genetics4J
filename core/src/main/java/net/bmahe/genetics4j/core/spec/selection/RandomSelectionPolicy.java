package net.bmahe.genetics4j.core.spec.selection;

import org.immutables.value.Value;

@Value.Immutable
public abstract class RandomSelectionPolicy implements SelectionPolicy {

	public static RandomSelectionPolicy build() {
		return new RandomSelectionPolicy() {
		};
	}
}