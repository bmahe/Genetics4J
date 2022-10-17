package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

public class DoubleChromosomeSinglePointCrossover<T extends Comparable<T>> implements ChromosomeCombinator<T> {

	private final RandomGenerator randomGenerator;

	public DoubleChromosomeSinglePointCrossover(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public List<Chromosome> combine(final AbstractEAConfiguration<T> eaConfiguration, final Chromosome chromosome1,
			final T firstParentFitness, final Chromosome chromosome2, final T secondParentFitness) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(DoubleChromosome.class, chromosome1);
		Validate.isInstanceOf(DoubleChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final int alleleSplit = randomGenerator.nextInt(chromosome1.getNumAlleles());

		final var doubleChromosome1 = (DoubleChromosome) chromosome1;
		final var doubleChromosome2 = (DoubleChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final double[] firstChildValues = new double[numAlleles];
		final double[] secondChildValues = new double[numAlleles];

		for (int i = 0; i < numAlleles; i++) {

			if (i < alleleSplit) {
				firstChildValues[i] = doubleChromosome1.getAllele(i);
				secondChildValues[i] = doubleChromosome2.getAllele(i);
			} else {
				firstChildValues[i] = doubleChromosome2.getAllele(i);
				secondChildValues[i] = doubleChromosome1.getAllele(i);
			}
		}

		/**
		 * TODO Should the min/max values be extended based on the lowest/highest
		 * values?
		 */
		final double minValue = doubleChromosome1.getMinValue();
		final double maxValue = doubleChromosome2.getMaxValue();

		return List.of(new DoubleChromosome(numAlleles, minValue, maxValue, firstChildValues),
				new DoubleChromosome(numAlleles, minValue, maxValue, secondChildValues));
	}
}