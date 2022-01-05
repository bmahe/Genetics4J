package net.bmahe.genetics4j.core.combination.singlepointarithmetic;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;

public class FloatChromosomeSinglePointArithmetic implements ChromosomeCombinator {

	private final RandomGenerator randomGenerator;
	private final float alpha;

	public FloatChromosomeSinglePointArithmetic(final RandomGenerator _randomGenerator, final float _alpha) {
		Validate.notNull(_randomGenerator);
		Validate.inclusiveBetween(0.0d, 1.0d, _alpha);

		this.randomGenerator = _randomGenerator;
		this.alpha = _alpha;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(FloatChromosome.class, chromosome1);
		Validate.isInstanceOf(FloatChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final int alleleSplit = randomGenerator.nextInt(chromosome1.getNumAlleles());

		final FloatChromosome intChromosome1 = (FloatChromosome) chromosome1;
		final FloatChromosome intChromosome2 = (FloatChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final float[] firstChildValues = new float[numAlleles];
		final float[] secondChildValues = new float[numAlleles];

		for (int i = 0; i < numAlleles; i++) {

			final float firstAllele = intChromosome1.getAllele(i);
			final float secondAllele = intChromosome2.getAllele(i);

			if (i < alleleSplit) {
				firstChildValues[i] = alpha * firstAllele + (1 - alpha) * secondAllele;
				secondChildValues[i] = (1 - alpha) * firstAllele + alpha * secondAllele;
			} else {
				firstChildValues[i] = (1 - alpha) * firstAllele + alpha * secondAllele;
				secondChildValues[i] = alpha * firstAllele + (1 - alpha) * secondAllele;
			}
		}

		return List.of(
				new FloatChromosome(numAlleles,
						intChromosome1.getMinValue(),
						intChromosome1.getMaxValue(),
						firstChildValues),
				new FloatChromosome(numAlleles,
						intChromosome1.getMinValue(),
						intChromosome1.getMaxValue(),
						secondChildValues));
	}
}