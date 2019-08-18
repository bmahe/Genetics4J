package net.bmahe.genetics4j.core.spec.combination;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SinglePointCrossover implements CombinationPolicy {

	public static SinglePointCrossover build() {
		return new SinglePointCrossover() {
		};
	}
}