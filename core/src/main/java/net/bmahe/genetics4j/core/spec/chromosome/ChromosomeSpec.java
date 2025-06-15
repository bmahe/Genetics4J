package net.bmahe.genetics4j.core.spec.chromosome;

/**
 * Marker interface for chromosome specifications in evolutionary algorithms.
 * 
 * <p>ChromosomeSpec defines the contract for specifying the structure, constraints, and properties
 * of chromosomes used in genetic algorithms. Implementations provide the blueprint for creating
 * chromosome instances with specific characteristics such as size, value ranges, and data types.
 * 
 * <p>Chromosome specifications are used by:
 * <ul>
 * <li><strong>Chromosome factories</strong>: To create chromosome instances with correct properties</li>
 * <li><strong>Genetic operators</strong>: To understand chromosome structure for crossover and mutation</li>
 * <li><strong>Validation logic</strong>: To ensure chromosome integrity and constraint satisfaction</li>
 * <li><strong>Algorithm configuration</strong>: To specify the genetic representation for problems</li>
 * </ul>
 * 
 * <p>The framework provides several concrete implementations:
 * <ul>
 * <li>{@link BitChromosomeSpec}: Specifications for binary string chromosomes</li>
 * <li>{@link IntChromosomeSpec}: Specifications for integer array chromosomes</li>
 * <li>{@link DoubleChromosomeSpec}: Specifications for double-precision floating-point chromosomes</li>
 * <li>{@link FloatChromosomeSpec}: Specifications for single-precision floating-point chromosomes</li>
 * </ul>
 * 
 * <p>Chromosome specifications typically define:
 * <ul>
 * <li><strong>Size/Length</strong>: Number of alleles or genes in the chromosome</li>
 * <li><strong>Value constraints</strong>: Valid ranges, domains, or sets of allowed values</li>
 * <li><strong>Data type</strong>: The primitive or object type used for allele representation</li>
 * <li><strong>Structural properties</strong>: Fixed vs. variable length, constraints between alleles</li>
 * </ul>
 * 
 * <p>Example usage in genetic algorithm configuration:
 * <pre>{@code
 * // Create a specification for 100-bit binary chromosome
 * ChromosomeSpec bitSpec = BitChromosomeSpec.of(100);
 * 
 * // Create a specification for integer chromosome with range [0, 50]
 * ChromosomeSpec intSpec = IntChromosomeSpec.of(10, 0, 50);
 * 
 * // Use in genotype specification
 * GenotypeSpec genotypeSpec = GenotypeSpec.of(bitSpec, intSpec);
 * }</pre>
 * 
 * @see net.bmahe.genetics4j.core.chromosomes.Chromosome
 * @see net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory
 * @see BitChromosomeSpec
 * @see IntChromosomeSpec
 * @see DoubleChromosomeSpec
 * @see FloatChromosomeSpec
 */
public interface ChromosomeSpec {

}