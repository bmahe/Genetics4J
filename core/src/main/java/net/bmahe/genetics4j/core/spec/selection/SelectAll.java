package net.bmahe.genetics4j.core.spec.selection;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SelectAll implements SelectionPolicy {

	public static SelectAll build() {
		return new SelectAll() {
		};
	}
}