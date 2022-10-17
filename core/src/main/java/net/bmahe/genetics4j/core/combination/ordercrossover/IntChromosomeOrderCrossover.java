package net.bmahe.genetics4j.core.combination.ordercrossover;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

public class IntChromosomeOrderCrossover<T extends Comparable<T>> implements ChromosomeCombinator<T> {

	private final RandomGenerator randomGenerator;

	public IntChromosomeOrderCrossover(final RandomGenerator _randomGenerator) {
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

		final IntChromosome intChromosome1 = (IntChromosome) chromosome1;
		final IntChromosome intChromosome2 = (IntChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final int[] newValues = new int[numAlleles];

		final int random1 = randomGenerator.nextInt(chromosome1.getNumAlleles());
		final int random2 = randomGenerator.nextInt(chromosome1.getNumAlleles());

		final int rangeStart = Math.min(random1, random2);
		final int rangeEnd = Math.max(random1, random2);

		final Set<Integer> valuesInRange = new HashSet<Integer>();
		for (int i = rangeStart; i < rangeEnd; i++) {
			valuesInRange.add(intChromosome1.getAllele(i));
		}

		int newValueIndex = 0;
		int chromosome2Idx = 0;

		while (newValueIndex < numAlleles) {
			if (newValueIndex < rangeStart || newValueIndex >= rangeEnd) {
				final int chromosome2Value = intChromosome2.getAllele(chromosome2Idx);
				if (valuesInRange.contains(chromosome2Value) == false) {
					newValues[newValueIndex] = chromosome2Value;
					newValueIndex++;
				}
				chromosome2Idx++;
			} else if (newValueIndex < rangeEnd) {
				newValues[newValueIndex] = intChromosome1.getAllele(newValueIndex);
				newValueIndex++;
			}
		}

		return List
				.of(new IntChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome1.getMaxValue(), newValues));
	}
}