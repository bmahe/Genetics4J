package net.bmahe.genetics4j.core.combination;

import java.util.List;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.PickFirstParent;

public class PickFirstParentHandler<T extends Comparable<T>> implements ChromosomeCombinatorHandler<T> {

	@Override
	public boolean canHandle(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);

		return combinationPolicy instanceof PickFirstParent;

	}

	@Override
	public ChromosomeCombinator<T> resolve(final ChromosomeCombinatorResolver<T> chromosomeCombinatorResolver,
			final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {

		return new ChromosomeCombinator<T>() {

			@Override
			public List<Chromosome> combine(final AbstractEAConfiguration<T> eaConfiguration, final Chromosome chromosome1,
					final T firstParentFitness, final Chromosome chromosome2, final T secondParentFitness) {
				Validate.notNull(chromosome1);

				return List.of(chromosome1);
			}
		};
	}
}