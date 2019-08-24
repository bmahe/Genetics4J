package net.bmahe.genetics4j.core.spec.combination;

import org.immutables.value.Value;

@Value.Immutable
public abstract class OrderCrossover implements CombinationPolicy {

	public static OrderCrossover build() {
		return new OrderCrossover() {
		};
	}
}