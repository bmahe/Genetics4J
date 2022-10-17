package net.bmahe.genetics4j.core.combination.multipointarithmetic;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.combination.MultiPointArithmetic;

public class IntChromosomeMultiPointArithmetic<T extends Comparable<T>> implements ChromosomeCombinator<T> {

	private final RandomGenerator randomGenerator;

	private final MultiPointArithmetic multiPointArithmeticPolicy;

	public IntChromosomeMultiPointArithmetic(final RandomGenerator _randomGenerator,
			final MultiPointArithmetic _multiPointArithmeticPolicy) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_multiPointArithmeticPolicy);

		this.randomGenerator = _randomGenerator;
		this.multiPointArithmeticPolicy = _multiPointArithmeticPolicy;
	}

	@Override
	public List<Chromosome> combine(final AbstractEAConfiguration<T> eaConfiguration, final Chromosome chromosome1,
			final T firstParentFitness, final Chromosome chromosome2, final T secondParentFitness) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(IntChromosome.class, chromosome1);
		Validate.isInstanceOf(IntChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		Validate.isTrue(multiPointArithmeticPolicy.numCrossovers() < chromosome1.getNumAlleles());
		Validate.isTrue(multiPointArithmeticPolicy.numCrossovers() < chromosome2.getNumAlleles());

		final int numCrossovers = multiPointArithmeticPolicy.numCrossovers();
		final double alpha = multiPointArithmeticPolicy.alpha();

		final int[] alleleSplits = randomGenerator.ints(0, chromosome1.getNumAlleles())
				.distinct()
				.limit(numCrossovers)
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

			final int firstAllele = intChromosome1.getAllele(i);
			final int secondAllele = intChromosome2.getAllele(i);

			if (useChromosome1) {
				firstChildValues[i] = (int) (alpha * firstAllele + (1 - alpha) * secondAllele);
				secondChildValues[i] = (int) ((1 - alpha) * firstAllele + alpha * secondAllele);
			} else {
				firstChildValues[i] = (int) ((1 - alpha) * firstAllele + alpha * secondAllele);
				secondChildValues[i] = (int) (alpha * firstAllele + (1 - alpha) * secondAllele);
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