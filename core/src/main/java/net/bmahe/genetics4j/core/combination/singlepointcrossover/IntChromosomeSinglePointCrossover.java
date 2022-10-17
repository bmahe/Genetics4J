package net.bmahe.genetics4j.core.combination.singlepointcrossover;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

public class IntChromosomeSinglePointCrossover<T extends Comparable<T>> implements ChromosomeCombinator<T> {

	private final RandomGenerator randomGenerator;

	public IntChromosomeSinglePointCrossover(final RandomGenerator _randomGenerator) {
		Validate.notNull(_randomGenerator);

		this.randomGenerator = _randomGenerator;
	}

	@Override
	public List<Chromosome> combine(final AbstractEAConfiguration<T> eaConfiguration, final Chromosome chromosome1,
			final T firstParentFitness, final Chromosome chromosome2, final T secondParentFitness) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(IntChromosome.class, chromosome1);
		Validate.isInstanceOf(IntChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final int alleleSplit = randomGenerator.nextInt(chromosome1.getNumAlleles());

		final IntChromosome intChromosome1 = (IntChromosome) chromosome1;
		final IntChromosome intChromosome2 = (IntChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final int[] firstChildValues = new int[numAlleles];
		final int[] secondChildValues = new int[numAlleles];

		for (int i = 0; i < numAlleles; i++) {

			if (i < alleleSplit) {
				firstChildValues[i] = intChromosome1.getAllele(i);
				secondChildValues[i] = intChromosome2.getAllele(i);
			} else {
				firstChildValues[i] = intChromosome2.getAllele(i);
				secondChildValues[i] = intChromosome1.getAllele(i);
			}
		}

		return List.of(
				new IntChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome1.getMaxValue(), firstChildValues),
				new IntChromosome(numAlleles,
						intChromosome1.getMinValue(),
						intChromosome1.getMaxValue(),
						secondChildValues));
	}
}