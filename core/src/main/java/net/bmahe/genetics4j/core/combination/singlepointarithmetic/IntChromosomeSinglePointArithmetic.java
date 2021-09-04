package net.bmahe.genetics4j.core.combination.singlepointarithmetic;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;

public class IntChromosomeSinglePointArithmetic implements ChromosomeCombinator {

	private final Random random;
	private final double alpha;

	public IntChromosomeSinglePointArithmetic(final Random _random, final double _alpha) {
		Validate.notNull(_random);
		Validate.inclusiveBetween(0.0d, 1.0d, _alpha);

		this.random = _random;
		this.alpha = _alpha;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(IntChromosome.class, chromosome1);
		Validate.isInstanceOf(IntChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final int alleleSplit = random.nextInt(chromosome1.getNumAlleles());

		final IntChromosome intChromosome1 = (IntChromosome) chromosome1;
		final IntChromosome intChromosome2 = (IntChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final int[] firstChildValues = new int[numAlleles];
		final int[] secondChildValues = new int[numAlleles];

		for (int i = 0; i < numAlleles; i++) {

			final int firstAllele = intChromosome1.getAllele(i);
			final int secondAllele = intChromosome2.getAllele(i);

			if (i < alleleSplit) {
				firstChildValues[i] = (int) (alpha * firstAllele + (1 - alpha) * secondAllele);
				secondChildValues[i] = (int) ((1 - alpha) * firstAllele + alpha * secondAllele);
			} else {
				firstChildValues[i] = (int) ((1 - alpha) * firstAllele + alpha * secondAllele);
				secondChildValues[i] = (int) (alpha * firstAllele + (1 - alpha) * secondAllele);
			}
		}

		return List.of(
				new IntChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome1.getMaxValue(), firstChildValues),
				new IntChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome1.getMaxValue(),
						secondChildValues));
	}
}