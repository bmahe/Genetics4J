package net.bmahe.genetics4j.gp.spec.combination;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;

@Value.Immutable
public abstract class ProgramRandomCombine implements CombinationPolicy {

	public static ProgramRandomCombine build() {
		return new ProgramRandomCombine() {
		};
	}
}