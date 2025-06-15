package net.bmahe.genetics4j.core.chromosomes;

/**
 * Base interface for all chromosome types in the genetic algorithm framework.
 * 
 * <p>A chromosome represents a single component of genetic information within a genotype.
 * Different chromosome implementations encode genetic information in various formats such as
 * bit strings, integer arrays, floating-point vectors, or tree structures.
 * 
 * <p>This interface defines the minimal contract that all chromosome types must implement.
 * Specific chromosome types extend this interface to provide type-specific operations
 * and genetic operators.
 * 
 * @see net.bmahe.genetics4j.core.Genotype
 * @see BitChromosome
 * @see IntChromosome
 * @see DoubleChromosome
 * @see FloatChromosome
 * @see TreeChromosome
 */
public interface Chromosome {

	/**
	 * Returns the number of alleles (genetic units) in this chromosome.
	 * 
	 * <p>The interpretation of an allele depends on the specific chromosome type:
	 * <ul>
	 * <li>For bit chromosomes: the number of bits</li>
	 * <li>For numeric chromosomes: the number of numeric values</li>
	 * <li>For tree chromosomes: the number of nodes in the tree</li>
	 * </ul>
	 * 
	 * @return the number of alleles in this chromosome, always positive
	 */
	public int getNumAlleles();
}