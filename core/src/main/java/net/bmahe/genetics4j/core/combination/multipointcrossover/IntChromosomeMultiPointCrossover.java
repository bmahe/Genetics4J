package net.bmahe.genetics4j.core.combination.multipointcrossover;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;

public class IntChromosomeMultiPointCrossover implements ChromosomeCombinator {

	private final Random random;

	private final MultiPointCrossover multiPointCrossoverPolicy;

	public IntChromosomeMultiPointCrossover(final Random _random, final MultiPointCrossover _multiPointCrossoverPolicy) {
		Validate.notNull(_random);
		Validate.notNull(_multiPointCrossoverPolicy);

		this.random = _random;
		this.multiPointCrossoverPolicy = _multiPointCrossoverPolicy;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(IntChromosome.class, chromosome1);
		Validate.isInstanceOf(IntChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome1.getNumAlleles());
		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome2.getNumAlleles());

		final int[] alleleSplits = random.ints(0, chromosome1.getNumAlleles())
				.distinct()
				.limit(multiPointCrossoverPolicy.numCrossovers())
				.toArray();
		Arrays.sort(alleleSplits);

		final IntChromosome intChromosome1 = (IntChromosome) chromosome1;
		final IntChromosome intChromosome2 = (IntChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final int[] newValues = new int[numAlleles];

		boolean useChromosome1 = true;
		int splitIndex = 0;
		for (int i = 0; i < intChromosome1.getNumAlleles(); i++) {

			if (splitIndex < alleleSplits.length && i == alleleSplits[splitIndex]) {
				splitIndex++;
				useChromosome1 = !useChromosome1;
			}

			if (useChromosome1) {
				newValues[i] = intChromosome1.getAllele(i);
			} else {
				newValues[i] = intChromosome2.getAllele(i);
			}
		}

		return List
				.of(new IntChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome2.getMaxValue(), newValues));
	}
}