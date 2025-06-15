package net.bmahe.genetics4j.neat.spec;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;

/**
 * Specification for NEAT (NeuroEvolution of Augmenting Topologies) neural network chromosomes.
 * 
 * <p>NeatChromosomeSpec defines the structural parameters and constraints for creating NEAT neural network
 * chromosomes. This specification is used by chromosome factories to generate initial network topologies
 * and by genetic operators to understand network boundaries and weight constraints.
 * 
 * <p>Key parameters:
 * <ul>
 * <li><strong>Network topology</strong>: Defines the number of input and output nodes</li>
 * <li><strong>Weight constraints</strong>: Specifies minimum and maximum connection weight values</li>
 * <li><strong>Network structure</strong>: Establishes the foundation for topology evolution</li>
 * <li><strong>Genetic boundaries</strong>: Provides constraints for mutation and crossover operations</li>
 * </ul>
 * 
 * <p>NEAT network architecture:
 * <ul>
 * <li><strong>Input layer</strong>: Fixed number of input nodes (indices 0 to numInputs-1)</li>
 * <li><strong>Output layer</strong>: Fixed number of output nodes (indices numInputs to numInputs+numOutputs-1)</li>
 * <li><strong>Hidden layers</strong>: Variable number of hidden nodes added through evolution</li>
 * <li><strong>Connections</strong>: Weighted links between nodes with enable/disable states</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Simple XOR problem specification
 * NeatChromosomeSpec xorSpec = NeatChromosomeSpec.of(
 *     2,      // 2 inputs (A, B)
 *     1,      // 1 output (A XOR B)
 *     -1.0f,  // minimum weight
 *     1.0f    // maximum weight
 * );
 * 
 * // Complex classification problem
 * NeatChromosomeSpec classificationSpec = NeatChromosomeSpec.of(
 *     784,    // 28x28 pixel inputs
 *     10,     // 10 class outputs
 *     -2.0f,  // wider weight range
 *     2.0f
 * );
 * 
 * // Builder pattern for complex construction
 * NeatChromosomeSpec spec = new NeatChromosomeSpec.Builder()
 *     .numInputs(5)
 *     .numOutputs(3)
 *     .minWeightValue(-1.5f)
 *     .maxWeightValue(1.5f)
 *     .build();
 * }</pre>
 * 
 * <p>Integration with NEAT ecosystem:
 * <ul>
 * <li><strong>Chromosome factories</strong>: Used by NeatConnectedChromosomeFactory for initial network generation</li>
 * <li><strong>Genetic operators</strong>: Provides constraints for weight mutations and structural changes</li>
 * <li><strong>Network evaluation</strong>: Defines input/output interfaces for fitness computation</li>
 * <li><strong>Evolution configuration</strong>: Establishes network parameters for entire evolutionary run</li>
 * </ul>
 * 
 * <p>Weight constraint management:
 * <ul>
 * <li><strong>Mutation boundaries</strong>: Weight mutations respect min/max bounds</li>
 * <li><strong>Initial generation</strong>: New connections use weights within specified range</li>
 * <li><strong>Crossover inheritance</strong>: Parent weights may be clipped to child bounds</li>
 * <li><strong>Network stability</strong>: Bounded weights help prevent gradient explosion/vanishing</li>
 * </ul>
 * 
 * <p>Validation and constraints:
 * <ul>
 * <li><strong>Positive node counts</strong>: Both input and output counts must be greater than zero</li>
 * <li><strong>Weight ordering</strong>: Minimum weight value should be less than maximum (not enforced)</li>
 * <li><strong>Reasonable bounds</strong>: Weight ranges should be appropriate for the problem domain</li>
 * <li><strong>Network capacity</strong>: Input/output counts should match problem requirements</li>
 * </ul>
 * 
 * <p>Problem domain considerations:
 * <ul>
 * <li><strong>Classification</strong>: Output count should match number of classes</li>
 * <li><strong>Regression</strong>: Single or multiple outputs for continuous value prediction</li>
 * <li><strong>Control problems</strong>: Outputs correspond to control signals or actions</li>
 * <li><strong>Feature extraction</strong>: Input count should match feature dimensionality</li>
 * </ul>
 * 
 * @see net.bmahe.genetics4j.neat.chromosomes.NeatChromosome
 * @see net.bmahe.genetics4j.neat.chromosomes.factory.NeatConnectedChromosomeFactory
 * @see net.bmahe.genetics4j.core.spec.EAConfiguration
 * @see ChromosomeSpec
 */
@Value.Immutable
public abstract class NeatChromosomeSpec implements ChromosomeSpec {

	/**
	 * Returns the number of input nodes for the neural network.
	 * 
	 * <p>Input nodes receive external data and form the first layer of the network.
	 * They are assigned indices 0 through numInputs-1 and do not apply activation
	 * functions to their values.
	 * 
	 * @return the number of input nodes (must be positive)
	 */
	@Value.Parameter
	public abstract int numInputs();

	/**
	 * Returns the number of output nodes for the neural network.
	 * 
	 * <p>Output nodes produce the final results of network computation and form the
	 * last layer of the network. They are assigned indices numInputs through
	 * numInputs+numOutputs-1 and apply activation functions to their weighted sums.
	 * 
	 * @return the number of output nodes (must be positive)
	 */
	@Value.Parameter
	public abstract int numOutputs();

	/**
	 * Returns the minimum allowed connection weight value.
	 * 
	 * <p>This constraint is used by genetic operators to bound weight mutations
	 * and ensure network stability. New connections and weight perturbations
	 * should respect this lower bound.
	 * 
	 * @return the minimum connection weight value
	 */
	@Value.Parameter
	public abstract float minWeightValue();

	/**
	 * Returns the maximum allowed connection weight value.
	 * 
	 * <p>This constraint is used by genetic operators to bound weight mutations
	 * and ensure network stability. New connections and weight perturbations
	 * should respect this upper bound.
	 * 
	 * @return the maximum connection weight value
	 */
	@Value.Parameter
	public abstract float maxWeightValue();

	@Value.Check
	protected void check() {
		Validate.isTrue(numInputs() > 0);
		Validate.isTrue(numOutputs() > 0);
	}

	public static class Builder extends ImmutableNeatChromosomeSpec.Builder {
	}

	/**
	 * Creates a new NEAT chromosome specification with the given parameters.
	 * 
	 * <p>This is a convenience factory method for creating chromosome specifications
	 * with all required parameters. The specification will be validated to ensure
	 * positive input and output counts.
	 * 
	 * @param numInputs number of input nodes (must be positive)
	 * @param numOutputs number of output nodes (must be positive)
	 * @param minWeightValue minimum allowed connection weight
	 * @param maxWeightValue maximum allowed connection weight
	 * @return a new chromosome specification with the specified parameters
	 * @throws IllegalArgumentException if numInputs &lt;= 0 or numOutputs &lt;= 0
	 */
	public static NeatChromosomeSpec of(final int numInputs, final int numOutputs, final float minWeightValue,
			final float maxWeightValue) {
		return new Builder().numInputs(numInputs)
				.numOutputs(numOutputs)
				.minWeightValue(minWeightValue)
				.maxWeightValue(maxWeightValue)
				.build();
	}
}