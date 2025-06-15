package net.bmahe.genetics4j.core.spec.chromosome;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

/**
 * Specification for integer array chromosomes in evolutionary algorithms.
 * 
 * <p>IntChromosomeSpec defines the blueprint for creating chromosomes that represent solutions
 * as arrays of integer values. This is one of the most commonly used chromosome types, suitable
 * for discrete optimization problems, permutation problems, and integer programming.
 * 
 * <p>The specification includes:
 * <ul>
 * <li><strong>Size</strong>: The number of integer genes in the chromosome</li>
 * <li><strong>Value range</strong>: Minimum and maximum bounds for each integer value</li>
 * <li><strong>Validation</strong>: Automatic constraint checking for valid parameters</li>
 * </ul>
 * 
 * <p>Common use cases for integer chromosomes:
 * <ul>
 * <li><strong>Combinatorial optimization</strong>: Knapsack, bin packing, scheduling problems</li>
 * <li><strong>Parameter optimization</strong>: Integer hyperparameters, configuration values</li>
 * <li><strong>Permutation problems</strong>: Traveling salesman, job scheduling (with appropriate operators)</li>
 * <li><strong>Graph problems</strong>: Node selection, path optimization, network design</li>
 * <li><strong>Resource allocation</strong>: Assignment problems, load balancing</li>
 * </ul>
 * 
 * <p>The integer values in the chromosome are constrained to the specified range [minValue, maxValue],
 * inclusive on both ends. The genetic operators (crossover and mutation) respect these constraints
 * and ensure that offspring remain within valid bounds.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Create specification for 10 integers in range [0, 100]
 * IntChromosomeSpec spec = IntChromosomeSpec.of(10, 0, 100);
 * 
 * // Create specification for permutation of 20 items (values 0-19)
 * IntChromosomeSpec permutationSpec = IntChromosomeSpec.of(20, 0, 19);
 * 
 * // Use in EA configuration
 * EAConfiguration<Double> config = EAConfigurationBuilder.<Double>builder()
 *     .chromosomeSpecs(spec)
 *     .parentSelectionPolicy(Tournament.of(3))
 *     .build();
 * }</pre>
 * 
 * @see net.bmahe.genetics4j.core.chromosomes.IntChromosome
 * @see net.bmahe.genetics4j.core.chromosomes.factory.IntChromosomeFactory
 * @see ChromosomeSpec
 */
@Value.Immutable
public abstract class IntChromosomeSpec implements ChromosomeSpec {

	/**
	 * Returns the number of integer values in chromosomes created from this specification.
	 * 
	 * @return the chromosome size, always positive
	 */
	@Value.Parameter
	public abstract int size();

	/**
	 * Returns the minimum value (inclusive) for integer alleles in the chromosome.
	 * 
	 * @return the minimum allowed integer value
	 */
	@Value.Parameter
	public abstract int minValue();

	/**
	 * Returns the maximum value (inclusive) for integer alleles in the chromosome.
	 * 
	 * @return the maximum allowed integer value
	 */
	@Value.Parameter
	public abstract int maxValue();

	@Value.Check
	protected void check() {
		Validate.isTrue(size() > 0);
		Validate.isTrue(minValue() <= maxValue());
	}

	public static class Builder extends ImmutableIntChromosomeSpec.Builder {
	}

	/**
	 * Creates a new integer chromosome specification with the given parameters.
	 * 
	 * @param size the number of integer values in the chromosome, must be positive
	 * @param minValue the minimum value (inclusive) for each integer in the chromosome
	 * @param maxValue the maximum value (inclusive) for each integer in the chromosome
	 * @return a new IntChromosomeSpec instance with the specified parameters
	 * @throws IllegalArgumentException if size is not positive or if minValue > maxValue
	 */
	public static IntChromosomeSpec of(final int size, final int minValue, final int maxValue) {
		return ImmutableIntChromosomeSpec.of(size, minValue, maxValue);
	}

}