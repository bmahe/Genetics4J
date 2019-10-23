package net.bmahe.genetics4j.core.spec.combination;

public abstract class EdgeRecombinationCrossover implements CombinationPolicy {

	public static EdgeRecombinationCrossover build() {
		return new EdgeRecombinationCrossover() {
		};
	}
}