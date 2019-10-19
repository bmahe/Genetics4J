package net.bmahe.genetics4j.core.combination.multipointcrossover;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;

public class BitChromosomeMultiPointCrossover implements ChromosomeCombinator {

	private final Random random;

	public BitChromosomeMultiPointCrossover(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof MultiPointCrossover && chromosome instanceof BitChromosomeSpec;
	}

	@Override
	public BitChromosome combine(final CombinationPolicy combinationPolicy, final Chromosome chromosome1,
			final Chromosome chromosome2) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(MultiPointCrossover.class, combinationPolicy);
		Validate.isInstanceOf(BitChromosome.class, chromosome1);
		Validate.isInstanceOf(BitChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final MultiPointCrossover multiPointCrossoverPolicy = (MultiPointCrossover) combinationPolicy;
		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome1.getNumAlleles());
		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome2.getNumAlleles());

		final int[] alleleSplits = random.ints(0, chromosome1.getNumAlleles())
				.distinct()
				.limit(multiPointCrossoverPolicy.numCrossovers())
				.toArray();
		Arrays.sort(alleleSplits);

		final BitChromosome bitChromosome1 = (BitChromosome) chromosome1;
		final BitChromosome bitChromosome2 = (BitChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final BitSet bitSet = new BitSet(numAlleles);

		boolean useChromosome1 = true;
		int splitIndex = 0;
		for (int i = 0; i < bitChromosome1.getNumAlleles(); i++) {

			if (i == alleleSplits[splitIndex]) {
				splitIndex++;
				useChromosome1 = !useChromosome1;
			}

			if (useChromosome1) {
				bitSet.set(i, bitChromosome1.getBit(i));
			} else {
				bitSet.set(i, bitChromosome2.getBit(i));
			}
		}

		return new BitChromosome(numAlleles, bitSet);
	}

}