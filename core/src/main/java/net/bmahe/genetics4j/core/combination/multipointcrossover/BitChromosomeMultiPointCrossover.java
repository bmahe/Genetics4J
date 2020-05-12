package net.bmahe.genetics4j.core.combination.multipointcrossover;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;

public class BitChromosomeMultiPointCrossover implements ChromosomeCombinator {

	private final Random random;

	private final MultiPointCrossover multiPointCrossoverPolicy;

	public BitChromosomeMultiPointCrossover(final Random _random,
			final MultiPointCrossover _multiPointCrossoverPolicy) {
		Validate.notNull(_random);
		Validate.notNull(_multiPointCrossoverPolicy);

		this.random = _random;
		this.multiPointCrossoverPolicy = _multiPointCrossoverPolicy;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(BitChromosome.class, chromosome1);
		Validate.isInstanceOf(BitChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome1.getNumAlleles());
		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome2.getNumAlleles());

		final int[] alleleSplits = random.ints(0, chromosome1.getNumAlleles())
				.distinct()
				.limit(multiPointCrossoverPolicy.numCrossovers())
				.sorted()
				.toArray();

		final BitChromosome bitChromosome1 = (BitChromosome) chromosome1;
		final BitChromosome bitChromosome2 = (BitChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final BitSet firstChildBitSet = new BitSet(numAlleles);
		final BitSet secondChildBitSet = new BitSet(numAlleles);

		boolean useChromosome1 = true;
		int splitIndex = 0;
		for (int i = 0; i < bitChromosome1.getNumAlleles(); i++) {

			if (splitIndex < alleleSplits.length && i == alleleSplits[splitIndex]) {
				splitIndex++;
				useChromosome1 = !useChromosome1;
			}

			if (useChromosome1) {
				firstChildBitSet.set(i, bitChromosome1.getBit(i));
				secondChildBitSet.set(i, bitChromosome2.getBit(i));
			} else {
				firstChildBitSet.set(i, bitChromosome2.getBit(i));
				secondChildBitSet.set(i, bitChromosome1.getBit(i));
			}
		}

		return List.of(new BitChromosome(numAlleles, firstChildBitSet),
				new BitChromosome(numAlleles, secondChildBitSet));
	}
}