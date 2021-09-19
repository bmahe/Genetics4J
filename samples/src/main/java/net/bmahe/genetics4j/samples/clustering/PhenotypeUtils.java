package net.bmahe.genetics4j.samples.clustering;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;

public class PhenotypeUtils {

	// tag::to_phenotype[]
	public static double[][] toPhenotype(final Genotype genotype) {
		Validate.notNull(genotype);
		Validate.isTrue(genotype.getSize() > 0);

		final var doubleChromosome = genotype.getChromosome(0, DoubleChromosome.class);

		final int numClusters = doubleChromosome.getSize() / 2;
		final double[][] clusters = new double[numClusters][2];

		for (int i = 0; i < numClusters; i++) {
			clusters[i][0] = doubleChromosome.getAllele(i * 2);
			clusters[i][1] = doubleChromosome.getAllele(i * 2 + 1);
		}

		return clusters;
	}
	// end::to_phenotype[]
}