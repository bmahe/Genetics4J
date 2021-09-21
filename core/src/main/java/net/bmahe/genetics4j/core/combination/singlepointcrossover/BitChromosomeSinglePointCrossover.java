package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import java.util.BitSet;
import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;

public class BitChromosomeSinglePointCrossover implements ChromosomeCombinator {

	private final RandomGenerator randomGenerator;

	public BitChromosomeSinglePointCrossover(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(BitChromosome.class, chromosome1);
		Validate.isInstanceOf(BitChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final int alleleSplit = randomGenerator.nextInt(chromosome1.getNumAlleles());

		final BitChromosome bitChromosome1 = (BitChromosome) chromosome1;
		final BitChromosome bitChromosome2 = (BitChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final BitSet firstChildBitSet = new BitSet(numAlleles);
		final BitSet secondChildBitSet = new BitSet(numAlleles);

		for (int i = 0; i < alleleSplit; i++) {
			firstChildBitSet.set(i, bitChromosome1.getBit(i));
			secondChildBitSet.set(i, bitChromosome2.getBit(i));
		}
		for (int i = alleleSplit; i < numAlleles; i++) {
			firstChildBitSet.set(i, bitChromosome2.getBit(i));
			secondChildBitSet.set(i, bitChromosome1.getBit(i));
		}

		return List.of(new BitChromosome(numAlleles, firstChildBitSet), new BitChromosome(numAlleles, secondChildBitSet));
	}
}