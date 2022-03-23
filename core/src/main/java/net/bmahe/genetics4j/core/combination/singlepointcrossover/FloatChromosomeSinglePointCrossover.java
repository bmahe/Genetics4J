package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;

public class FloatChromosomeSinglePointCrossover implements ChromosomeCombinator {

	private final RandomGenerator randomGenerator;

	public FloatChromosomeSinglePointCrossover(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(FloatChromosome.class, chromosome1);
		Validate.isInstanceOf(FloatChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final int alleleSplit = randomGenerator.nextInt(chromosome1.getNumAlleles());

		final var floatChromosome1 = (FloatChromosome) chromosome1;
		final var floatChromosome2 = (FloatChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final float[] firstChildValues = new float[numAlleles];
		final float[] secondChildValues = new float[numAlleles];

		for (int i = 0; i < numAlleles; i++) {

			if (i < alleleSplit) {
				firstChildValues[i] = floatChromosome1.getAllele(i);
				secondChildValues[i] = floatChromosome2.getAllele(i);
			} else {
				firstChildValues[i] = floatChromosome2.getAllele(i);
				secondChildValues[i] = floatChromosome1.getAllele(i);
			}
		}

		/**
		 * TODO Should the min/max values be extended based on the lowest/highest
		 * values?
		 */
		final float minValue = floatChromosome1.getMinValue();
		final float maxValue = floatChromosome2.getMaxValue();

		return List.of(new FloatChromosome(numAlleles, minValue, maxValue, firstChildValues),
				new FloatChromosome(numAlleles, minValue, maxValue, secondChildValues));
	}
}