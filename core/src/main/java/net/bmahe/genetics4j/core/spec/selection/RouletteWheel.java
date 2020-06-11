package net.bmahe.genetics4j.core.spec.selection;

import org.immutables.value.Value;

@Value.Immutable
public abstract class RouletteWheel implements SelectionPolicy {

	public static RouletteWheel build() {
		return new RouletteWheel() {
		};
	}
}