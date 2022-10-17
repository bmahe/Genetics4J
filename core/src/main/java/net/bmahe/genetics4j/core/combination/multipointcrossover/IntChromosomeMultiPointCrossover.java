package net.bmahe.genetics4j.core.combination.multipointcrossover;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;

public class IntChromosomeMultiPointCrossover<T extends Comparable<T>> implements ChromosomeCombinator<T> {

	private final RandomGenerator randomGenerator;

	private final MultiPointCrossover multiPointCrossoverPolicy;

	public IntChromosomeMultiPointCrossover(final RandomGenerator _randomGenerator,
			final MultiPointCrossover _multiPointCrossoverPolicy) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_multiPointCrossoverPolicy);

		this.randomGenerator = _randomGenerator;
		this.multiPointCrossoverPolicy = _multiPointCrossoverPolicy;
	}

	@Override
	public List<Chromosome> combine(final AbstractEAConfiguration<T> eaConfiguration, final Chromosome chromosome1,
			final T firstParentFitness, final Chromosome chromosome2, final T secondParentFitness) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(IntChromosome.class, chromosome1);
		Validate.isInstanceOf(IntChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome1.getNumAlleles());
		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome2.getNumAlleles());

		final int[] alleleSplits = randomGenerator.ints(0, chromosome1.getNumAlleles())
				.distinct()
				.limit(multiPointCrossoverPolicy.numCrossovers())
				.sorted()
				.toArray();

		final IntChromosome intChromosome1 = (IntChromosome) chromosome1;
		final IntChromosome intChromosome2 = (IntChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final int[] firstChildValues = new int[numAlleles];
		final int[] secondChildValues = new int[numAlleles];

		boolean useChromosome1 = true;
		int splitIndex = 0;
		for (int i = 0; i < intChromosome1.getNumAlleles(); i++) {

			if (splitIndex < alleleSplits.length && i == alleleSplits[splitIndex]) {
				splitIndex++;
				useChromosome1 = !useChromosome1;
			}

			if (useChromosome1) {
				firstChildValues[i] = intChromosome1.getAllele(i);
				secondChildValues[i] = intChromosome2.getAllele(i);
			} else {
				firstChildValues[i] = intChromosome2.getAllele(i);
				secondChildValues[i] = intChromosome1.getAllele(i);
			}
		}

		return List.of(
				new IntChromosome(numAlleles, intChromosome1.getMinValue(), intChromosome2.getMaxValue(), firstChildValues),
				new IntChromosome(numAlleles,
						intChromosome1.getMinValue(),
						intChromosome2.getMaxValue(),
						secondChildValues));
	}
}