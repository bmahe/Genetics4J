package net.bmahe.genetics4j.core.combination.multipointcrossover;

import java.util.List;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.combination.ChromosomeCombinator;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;

public class FloatChromosomeMultiPointCrossover implements ChromosomeCombinator {

	private final RandomGenerator randomGenerator;

	private final MultiPointCrossover multiPointCrossoverPolicy;

	public FloatChromosomeMultiPointCrossover(final RandomGenerator _randomGenerator,
			final MultiPointCrossover _multiPointCrossoverPolicy) {
		Validate.notNull(_randomGenerator);
		Validate.notNull(_multiPointCrossoverPolicy);

		this.randomGenerator = _randomGenerator;
		this.multiPointCrossoverPolicy = _multiPointCrossoverPolicy;
	}

	@Override
	public List<Chromosome> combine(final Chromosome chromosome1, final Chromosome chromosome2) {
		Validate.notNull(chromosome1);
		Validate.notNull(chromosome2);
		Validate.isInstanceOf(FloatChromosome.class, chromosome1);
		Validate.isInstanceOf(FloatChromosome.class, chromosome2);
		Validate.isTrue(chromosome1.getNumAlleles() == chromosome2.getNumAlleles());

		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome1.getNumAlleles());
		Validate.isTrue(multiPointCrossoverPolicy.numCrossovers() < chromosome2.getNumAlleles());

		final int[] alleleSplits = randomGenerator.ints(0, chromosome1.getNumAlleles())
				.distinct()
				.limit(multiPointCrossoverPolicy.numCrossovers())
				.sorted()
				.toArray();

		final FloatChromosome floatChromosome1 = (FloatChromosome) chromosome1;
		final FloatChromosome floatChromosome2 = (FloatChromosome) chromosome2;

		final int numAlleles = chromosome1.getNumAlleles();
		final float[] firstChildValues = new float[numAlleles];
		final float[] secondChildValues = new float[numAlleles];

		boolean useChromosome1 = true;
		int splitIndex = 0;
		for (int i = 0; i < floatChromosome1.getNumAlleles(); i++) {

			if (splitIndex < alleleSplits.length && i == alleleSplits[splitIndex]) {
				splitIndex++;
				useChromosome1 = !useChromosome1;
			}

			if (useChromosome1) {
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