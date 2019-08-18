package net.bmahe.genetics4j.core.spec.selection;

import org.immutables.value.Value;

@Value.Immutable
public abstract class RouletteWheelSelection implements SelectionPolicy {

	public static RouletteWheelSelection build() {
		return new RouletteWheelSelection() {
		};
	}
}