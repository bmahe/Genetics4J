package net.bmahe.genetics4j.core.combination.ordercrossover;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.core.spec.combination.OrderCrossover;

public class IntChromosomeOrderCrossover implements ChromosomeCombinator {

	private final Random random;

	public IntChromosomeOrderCrossover(final Random _random) {
		Validate.notNull(_random);

		this.random = _random;
	}

	@Override
	public boolean canHandle(final CombinationPolicy combinationPolicy, final ChromosomeSpec chromosome) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome);

		return combinationPolicy instanceof OrderCrossover && chromosome instanceof IntChromosomeSpec;
	}

	@Override
	public IntChromosome combine(final CombinationPolicy combinationPolicy, final Chromosome chromosome1,
			final Chromosome chromosome2) {
		Validate.notNull(combinationPolicy);
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(OrderCrossover.class, combinationPolicy);
		Validate.isInstanceOf(IntChromosome.class, chromosome1);
		Validate.isInstanceOf(IntChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		final IntChromosome intChromosome1 = (IntChromosome) chromosome1;
		final IntChromosome intChromosome2 = (IntChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final int[] newValues = new int[numAlleles];

		final int random1 = random.nextInt(chromosome1.getNumAlleles());
		final int random2 = random.nextInt(chromosome1.getNumAlleles());

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

		return new IntChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome1.getMaxValue(), newValues);
	}
}