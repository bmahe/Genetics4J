package net.bmahe.genetics4j.core.chromosomes.factory;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

/**
 * Interface for any class wishing to generate chromosomes
 *
 * @param <T> Type of the fitness measurement
 */
public interface ChromosomeFactory<T extends Chromosome> {

	/**
	 * Validates if this factory is appropriate for generating a chromosome based on
	 * the specifications passed as a parameter
	 * 
	 * @param chromosomeSpec Specifications of the chromosome we wish to generate
	 * @return true if the implementation can generate a chromosome based on the
	 *         specifications
	 */
	boolean canHandle(final ChromosomeSpec chromosomeSpec);

	/**
	 * Generate a chromosome based on the specifications passed as a parameter
	 * 
	 * @param chromosomeSpec Specifications of the chromosome we wish to generate
	 * @return Generated chromosome
	 */
	T generate(final ChromosomeSpec chromosomeSpec);
}