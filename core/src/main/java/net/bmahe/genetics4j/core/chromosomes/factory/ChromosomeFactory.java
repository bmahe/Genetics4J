package net.bmahe.genetics4j.core.chromosomes.factory;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

/**
 * Factory interface for creating chromosome instances based on specifications.
 * 
 * <p>ChromosomeFactory implements the Factory pattern to decouple chromosome creation from
 * the specific implementation details. Each factory is responsible for creating chromosomes
 * of a specific type (bit, integer, double, etc.) based on the provided specifications.
 * 
 * <p>The factory pattern provides several benefits:
 * <ul>
 * <li><strong>Type safety</strong>: Compile-time guarantees about chromosome types</li>
 * <li><strong>Extensibility</strong>: Easy addition of new chromosome types</li>
 * <li><strong>Configuration-driven</strong>: Chromosome creation based on specifications</li>
 * <li><strong>Encapsulation</strong>: Hides complex chromosome initialization logic</li>
 * </ul>
 * 
 * <p>Factory implementations typically handle:
 * <ul>
 * <li><strong>Random initialization</strong>: Creating chromosomes with random values within constraints</li>
 * <li><strong>Constraint validation</strong>: Ensuring generated chromosomes meet specification requirements</li>
 * <li><strong>Value range handling</strong>: Respecting minimum/maximum bounds for numeric types</li>
 * <li><strong>Size management</strong>: Creating chromosomes with the correct number of alleles</li>
 * </ul>
 * 
 * <p>The framework provides concrete factories for common chromosome types:
 * <ul>
 * <li>{@link BitChromosomeFactory}: Creates binary string chromosomes</li>
 * <li>{@link IntChromosomeFactory}: Creates integer array chromosomes</li>
 * <li>{@link DoubleChromosomeFactory}: Creates double-precision floating-point chromosomes</li>
 * <li>{@link FloatChromosomeFactory}: Creates single-precision floating-point chromosomes</li>
 * </ul>
 * 
 * <p>Factory selection is typically handled automatically by {@link ChromosomeFactoryProvider}
 * which maintains a registry of available factories and selects the appropriate one based
 * on the chromosome specification type.
 * 
 * <p>Example custom factory implementation:
 * <pre>{@code
 * public class CustomChromosomeFactory implements ChromosomeFactory<CustomChromosome> {
 *     
 *     @Override
 *     public boolean canHandle(ChromosomeSpec chromosomeSpec) {
 *         return chromosomeSpec instanceof CustomChromosomeSpec;
 *     }
 *     
 *     @Override
 *     public CustomChromosome generate(ChromosomeSpec chromosomeSpec) {
 *         CustomChromosomeSpec spec = (CustomChromosomeSpec) chromosomeSpec;
 *         // Create chromosome based on specification
 *         return new CustomChromosome(spec.getParameters());
 *     }
 * }
 * }</pre>
 * 
 * @param <T> the type of chromosome this factory creates
 * @see ChromosomeSpec
 * @see ChromosomeFactoryProvider
 * @see net.bmahe.genetics4j.core.chromosomes.Chromosome
 */
public interface ChromosomeFactory<T extends Chromosome> {

	/**
	 * Determines if this factory can create chromosomes for the given specification.
	 * 
	 * <p>This method implements the type-checking logic that allows the factory system
	 * to automatically select the appropriate factory for each chromosome specification.
	 * Implementations typically check if the specification is of the expected type.
	 * 
	 * <p>The method should be:
	 * <ul>
	 * <li><strong>Fast</strong>: Called frequently during factory selection</li>
	 * <li><strong>Accurate</strong>: Return true only if generation will succeed</li>
	 * <li><strong>Safe</strong>: Handle null and unexpected specification types gracefully</li>
	 * </ul>
	 * 
	 * @param chromosomeSpec the chromosome specification to evaluate
	 * @return {@code true} if this factory can generate chromosomes for the given specification,
	 *         {@code false} otherwise
	 * @throws IllegalArgumentException if chromosomeSpec is null (optional, implementation-dependent)
	 */
	boolean canHandle(final ChromosomeSpec chromosomeSpec);

	/**
	 * Creates a new chromosome instance based on the provided specification.
	 * 
	 * <p>This method implements the core factory logic, creating and initializing a new
	 * chromosome according to the parameters defined in the specification. The generated
	 * chromosome should be ready for use in genetic operations.
	 * 
	 * <p>The generation process typically involves:
	 * <ul>
	 * <li>Extracting parameters from the specification (size, bounds, constraints)</li>
	 * <li>Generating random values within the specified constraints</li>
	 * <li>Creating and initializing the chromosome instance</li>
	 * <li>Validating that the result meets all requirements</li>
	 * </ul>
	 * 
	 * <p>Implementation requirements:
	 * <ul>
	 * <li><strong>Specification compatibility</strong>: Only call after {@link #canHandle} returns true</li>
	 * <li><strong>Randomization</strong>: Use appropriate random number generation for initialization</li>
	 * <li><strong>Constraint compliance</strong>: Ensure generated values respect specification bounds</li>
	 * <li><strong>Thread safety</strong>: Support concurrent calls in multi-threaded environments</li>
	 * </ul>
	 * 
	 * @param chromosomeSpec the specification defining the chromosome to create
	 * @return a newly created chromosome instance conforming to the specification
	 * @throws IllegalArgumentException if the specification is null, invalid, or not supported by this factory
	 * @throws RuntimeException if chromosome generation fails due to constraint conflicts or other errors
	 */
	T generate(final ChromosomeSpec chromosomeSpec);
}