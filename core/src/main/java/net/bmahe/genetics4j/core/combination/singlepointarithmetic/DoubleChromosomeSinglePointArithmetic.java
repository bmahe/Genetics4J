package net.bmahe.genetics4j.core.combination.singlepointarithmetic;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;

public class DoubleChromosomeSinglePointArithmetic implements ChromosomeCombinator {

	private final Random random;
	private final double alpha;

	public DoubleChromosomeSinglePointArithmetic(final Random _random, final double _alpha) {
		Validate.notNull(_random);
		Validate.inclusiveBetween(0.0d, 1.0d, _alpha);

		this.random = _random;
		this.alpha = _alpha;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(DoubleChromosome.class, chromosome1);
		Validate.isInstanceOf(DoubleChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final int alleleSplit = random.nextInt(chromosome1.getNumAlleles());

		final DoubleChromosome intChromosome1 = (DoubleChromosome) chromosome1;
		final DoubleChromosome intChromosome2 = (DoubleChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final double[] firstChildValues = new double[numAlleles];
		final double[] secondChildValues = new double[numAlleles];

		for (int i = 0; i < numAlleles; i++) {

			final double firstAllele = intChromosome1.getAllele(i);
			final double secondAllele = intChromosome2.getAllele(i);

			if (i < alleleSplit) {
				firstChildValues[i] = alpha * firstAllele + (1 - alpha) * secondAllele;
				secondChildValues[i] = (1 - alpha) * firstAllele + alpha * secondAllele;
			} else {
				firstChildValues[i] = (1 - alpha) * firstAllele + alpha * secondAllele;
				secondChildValues[i] = alpha * firstAllele + (1 - alpha) * secondAllele;
			}
		}

		return List.of(
				new DoubleChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome1.getMaxValue(),
						firstChildValues),
				new DoubleChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome1.getMaxValue(),
						secondChildValues));
	}
}