package net.bmahe.genetics4j.core.combination;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.PickFirstParent;

public class PickFirstParentHandler implements ChromosomeCombinatorHandler {

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);

		return combinationPolicy instanceof PickFirstParent;

	}

	@Override
	public ChromosomeCombinator resolve(final ChromosomeCombinatorResolver chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {

		return new ChromosomeCombinator() {

			@Override
			public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
				Validate.notNull(chromosome1);

				return List.of(chromosome1);
			}
		};
	}
}