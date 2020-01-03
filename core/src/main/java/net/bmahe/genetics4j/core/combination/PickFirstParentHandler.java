package net.bmahe.genetics4j.core.combination;

import java.util.List;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.PickFirstParent;

public class PickFirstParentHandler implements ChromosomeCombinatorHandler {

	@Override
	public boolean canHandle(ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			CombinationPolicy combinationPolicy, ChromosomeSpec chromosome) {
		return combinationPolicy instanceof PickFirstParent;

	}

	@Override
	public ChromosomeCombinator resolve(ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			CombinationPolicy combinationPolicy, ChromosomeSpec chromosome) {
		return new ChromosomeCombinator() {

			@Override
			public List<Chromosome> combine(Chromosome chromosome1, Chromosome chromosome2) {
				return List.of(chromosome1);
			}
		};
	}

}