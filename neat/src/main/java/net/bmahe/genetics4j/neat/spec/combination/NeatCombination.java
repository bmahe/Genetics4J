package net.bmahe.genetics4j.neat.spec.combination;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;

@Value.Immutable
public abstract class NeatCombination implements CombinationPolicy {

	public static NeatCombination build() {
		return ImmutableNeatCombination.builder()
				.build();
	}
}