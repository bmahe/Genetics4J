package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import java.util.BitSet;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;

public class BitChromosomeSinglePointCrossover implements ChromosomeCombinator {

	private final Random random;

	public BitChromosomeSinglePointCrossover(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof SinglePointCrossover && chromosome instanceof BitChromosomeSpec;
	}

	@Override
	public BitChromosome combine(final CombinationPolicy combinationPolicy, final Chromosome chromosome1,
			final Chromosome chromosome2) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(BitChromosome.class, chromosome1);
		Validate.isInstanceOf(BitChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final int alleleSplit = random.nextInt(chromosome1.getNumAlleles());

		final BitChromosome bitChromosome1 = (BitChromosome) chromosome1;
		final BitChromosome bitChromosome2 = (BitChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final BitSet bitSet = new BitSet(numAlleles);

		for (int i = 0; i < alleleSplit; i++) {
			bitSet.set(i, bitChromosome1.getBit(i));
		}
		for (int i = alleleSplit; i < numAlleles; i++) {
			bitSet.set(i, bitChromosome2.getBit(i));
		}

		return new BitChromosome(numAlleles, bitSet);
	}

}