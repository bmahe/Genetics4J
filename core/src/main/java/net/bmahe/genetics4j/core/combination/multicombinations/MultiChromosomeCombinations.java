package net.bmahe.genetics4j.core.combination.multicombinations;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

public class MultiChromosomeCombinations<T extends Comparable<T>> implements ChromosomeCombinator<T> {

	private final RandomGenerator randomGenerator;
	private final List<ChromosomeCombinator<T>> chromosomeCombinators;

	public MultiChromosomeCombinations(final RandomGenerator _randomGenerator,
			final List<ChromosomeCombinator<T>> _chromosomeCombinators) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_chromosomeCombinators);

		this.randomGenerator = _randomGenerator;
		this.chromosomeCombinators = _chromosomeCombinators;
	}

	@Override
	public List<Chromosome> combine(final AbstractEAConfiguration<T> eaConfiguration, final Chromosome chromosome1,
			final T firstParentFitness, final Chromosome chromosome2, final T secondParentFitness) {

		final int chromosomeCombinatorIndex = randomGenerator.nextInt(chromosomeCombinators.size());
		final ChromosomeCombinator<T> chromosomeCombinator = chromosomeCombinators.get(chromosomeCombinatorIndex);

		return chromosomeCombinator
				.combine(eaConfiguration, chromosome1, firstParentFitness, chromosome2, secondParentFitness);
	}

}