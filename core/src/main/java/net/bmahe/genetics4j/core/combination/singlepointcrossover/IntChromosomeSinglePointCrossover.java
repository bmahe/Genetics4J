package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;

public class IntChromosomeSinglePointCrossover implements ChromosomeCombinator {

	private final Random random;

	public IntChromosomeSinglePointCrossover(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof SinglePointCrossover && chromosome instanceof IntChromosomeSpec;
	}

	@Override
	public IntChromosome combine(final CombinationPolicy combinationPolicy, final Chromosome chromosome1,
			final Chromosome chromosome2) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(IntChromosome.class, chromosome1);
		Validate.isInstanceOf(IntChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final int alleleSplit = random.nextInt(chromosome1.getNumAlleles());

		final IntChromosome intChromosome1 = (IntChromosome) chromosome1;
		final IntChromosome intChromosome2 = (IntChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final int[] newValues = new int[numAlleles];

		for (int i = 0; i < numAlleles; i++) {

			if (i < alleleSplit) {
				newValues[i] = intChromosome1.getAllele(i);
			} else {
				newValues[i] = intChromosome2.getAllele(i);
			}
		}

		return new IntChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome1.getMaxValue(), newValues);
	}
}