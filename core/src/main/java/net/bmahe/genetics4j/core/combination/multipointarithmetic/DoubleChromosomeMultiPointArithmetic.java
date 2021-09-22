package net.bmahe.genetics4j.core.combination.multipointarithmetic;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.combination.MultiPointArithmetic;

public class DoubleChromosomeMultiPointArithmetic implements ChromosomeCombinator {

	private final RandomGenerator randomGenerator;

	private final MultiPointArithmetic multiPointArithmeticPolicy;

	public DoubleChromosomeMultiPointArithmetic(final RandomGenerator _randomGenerator,
			final MultiPointArithmetic _multiPointArithmeticPolicy) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_multiPointArithmeticPolicy);

		this.randomGenerator = _randomGenerator;
		this.multiPointArithmeticPolicy = _multiPointArithmeticPolicy;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(DoubleChromosome.class, chromosome1);
		Validate.isInstanceOf(DoubleChromosome.class, chromosome2);
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

		final DoubleChromosome doubleChromosome1 = (DoubleChromosome) chromosome1;
		final DoubleChromosome doubleChromosome2 = (DoubleChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final double[] firstChildValues = new double[numAlleles];
		final double[] secondChildValues = new double[numAlleles];

		boolean useChromosome1 = true;
		int splitIndex = 0;
		for (int i = 0; i < doubleChromosome1.getNumAlleles(); i++) {

			if (splitIndex < alleleSplits.length && i == alleleSplits[splitIndex]) {
				splitIndex++;
				useChromosome1 = !useChromosome1;
			}

			final double firstAllele = doubleChromosome1.getAllele(i);
			final double secondAllele = doubleChromosome2.getAllele(i);

			if (useChromosome1) {
				firstChildValues[i] = alpha * firstAllele + (1 - alpha) * secondAllele;
				secondChildValues[i] = (1 - alpha) * firstAllele + alpha * secondAllele;
			} else {
				firstChildValues[i] = (1 - alpha) * firstAllele + alpha * secondAllele;
				secondChildValues[i] = alpha * firstAllele + (1 - alpha) * secondAllele;
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