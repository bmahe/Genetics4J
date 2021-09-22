package net.bmahe.genetics4j.core.combination.multicombinations;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;

public class MultiChromosomeCombinations implements ChromosomeCombinator {

	private final RandomGenerator randomGenerator;
	private final List<ChromosomeCombinator> chromosomeCombinators;

	public MultiChromosomeCombinations(final RandomGenerator _randomGenerator,
			final List<ChromosomeCombinator> _chromosomeCombinators) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_chromosomeCombinators);

		this.randomGenerator = _randomGenerator;
		this.chromosomeCombinators = _chromosomeCombinators;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {

		final int chromosomeCombinatorIndex = randomGenerator.nextInt(chromosomeCombinators.size());
		final ChromosomeCombinator chromosomeCombinator = chromosomeCombinators.get(chromosomeCombinatorIndex);

		return chromosomeCombinator.combine(chromosome1, chromosome2);
	}

}
