package net.bmahe.genetics4j.neat.spec.combination;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.combination.CombinationPolicy;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.ParentComparisonPolicy;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.FitnessComparison;

/**
 * Configuration policy for NEAT (NeuroEvolution of Augmenting Topologies) genetic crossover operations.
 * 
 * <p>NeatCombination defines how neural network chromosomes should be recombined during genetic crossover,
 * including inheritance biases, gene re-enabling policies, and parent comparison strategies. This policy
 * controls the fundamental genetic operators that shape network topology evolution in NEAT.
 * 
 * <p>Key crossover parameters:
 * <ul>
 * <li><strong>Inheritance threshold</strong>: Bias toward fitter parent for gene inheritance</li>
 * <li><strong>Gene re-enabling</strong>: Probability of re-enabling disabled genes during crossover</li>
 * <li><strong>Parent comparison</strong>: Strategy for determining relative parent fitness</li>
 * <li><strong>Genetic alignment</strong>: Innovation-number-based gene matching</li>
 * </ul>
 * 
 * <p>NEAT genetic crossover process:
 * <ol>
 * <li><strong>Gene alignment</strong>: Match genes by innovation number between parents</li>
 * <li><strong>Matching genes</strong>: Randomly inherit from either parent (biased by inheritance threshold)</li>
 * <li><strong>Disjoint genes</strong>: Inherit from fitter parent based on parent comparison</li>
 * <li><strong>Excess genes</strong>: Inherit from fitter parent beyond less fit parent's range</li>
 * <li><strong>Gene state</strong>: Apply re-enabling policy to disabled genes</li>
 * </ol>
 * 
 * <p>Gene inheritance strategies:
 * <ul>
 * <li><strong>Matching genes</strong>: Present in both parents with same innovation number</li>
 * <li><strong>Disjoint genes</strong>: Present in one parent within other parent's innovation range</li>
 * <li><strong>Excess genes</strong>: Present in one parent beyond other parent's highest innovation</li>
 * <li><strong>Disabled genes</strong>: May be re-enabled based on re-enabling threshold</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Default NEAT crossover configuration
 * NeatCombination defaultPolicy = NeatCombination.build();
 * 
 * // Custom crossover with fitness bias
 * NeatCombination biasedPolicy = NeatCombination.builder()
 *     .inheritanceThresold(0.7)  // 70% bias toward fitter parent
 *     .reenableGeneInheritanceThresold(0.3)  // 30% chance to re-enable genes
 *     .parentComparisonPolicy(FitnessComparison.build())
 *     .build();
 * 
 * // Unbiased crossover for diversity
 * NeatCombination unbiasedPolicy = NeatCombination.builder()
 *     .inheritanceThresold(0.5)  // No bias toward either parent
 *     .reenableGeneInheritanceThresold(0.1)  // Low re-enabling rate
 *     .build();
 * 
 * // Use in EA configuration
 * var combinationSpec = ChromosomeCombinatorSpec.builder()
 *     .combinationPolicy(biasedPolicy)
 *     .build();
 * }</pre>
 * 
 * <p>Inheritance threshold effects:
 * <ul>
 * <li><strong>0.5 (default)</strong>: Unbiased inheritance, equal probability from both parents</li>
 * <li><strong>&gt; 0.5</strong>: Bias toward fitter parent, promotes convergence</li>
 * <li><strong>&lt; 0.5</strong>: Bias toward less fit parent, increases diversity</li>
 * <li><strong>1.0</strong>: Always inherit from fitter parent (if determinable)</li>
 * </ul>
 * 
 * <p>Gene re-enabling mechanism:
 * <ul>
 * <li><strong>Historical information</strong>: Disabled genes preserve connection topology</li>
 * <li><strong>Re-activation chance</strong>: Allows previously disabled connections to contribute again</li>
 * <li><strong>Topology exploration</strong>: Enables rediscovery of useful connection patterns</li>
 * <li><strong>Genetic diversity</strong>: Prevents permanent loss of structural information</li>
 * </ul>
 * 
 * <p>Parent comparison integration:
 * <ul>
 * <li><strong>Fitness comparison</strong>: Standard fitness-based parent ranking</li>
 * <li><strong>Custom strategies</strong>: Pluggable comparison policies for different problem domains</li>
 * <li><strong>Multi-objective support</strong>: Compatible with complex fitness landscapes</li>
 * <li><strong>Equal fitness handling</strong>: Special rules when parents have identical fitness</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Innovation sorting</strong>: Leverages pre-sorted connection lists for O(n) crossover</li>
 * <li><strong>Memory efficiency</strong>: Minimal allocation during gene inheritance</li>
 * <li><strong>Cache-friendly</strong>: Sequential access patterns for better cache performance</li>
 * <li><strong>Parallelizable</strong>: Crossover operations can be executed concurrently</li>
 * </ul>
 * 
 * @see ParentComparisonPolicy
 * @see FitnessComparison
 * @see net.bmahe.genetics4j.neat.combination.NeatChromosomeCombinator
 * @see CombinationPolicy
 */
@Value.Immutable
public interface NeatCombination extends CombinationPolicy {

	public static final double DEFAULT_INHERITANCE_THRESHOLD = 0.5d;

	public static final double DEFAULT_REENABLE_GENE_INHERITANCE_THRESHOLD = 0.25d;

	/**
	 * Returns the inheritance threshold for biasing gene selection toward fitter parents.
	 * 
	 * <p>This threshold controls the probability of inheriting genes from the fitter parent
	 * during crossover. Higher values bias inheritance toward the better performing parent,
	 * while lower values provide more equal inheritance or even bias toward the less fit parent.
	 * 
	 * <p>Inheritance behavior:
	 * <ul>
	 * <li><strong>0.5 (default)</strong>: Unbiased inheritance, equal probability from both parents</li>
	 * <li><strong>&gt; 0.5</strong>: Bias toward fitter parent, promotes convergence to good solutions</li>
	 * <li><strong>&lt; 0.5</strong>: Bias toward less fit parent, increases population diversity</li>
	 * <li><strong>1.0</strong>: Always inherit from fitter parent when fitness differs</li>
	 * <li><strong>0.0</strong>: Always inherit from less fit parent when fitness differs</li>
	 * </ul>
	 * 
	 * @return inheritance threshold value between 0.0 and 1.0 (inclusive)
	 */
	@Value.Default
	default public double inheritanceThresold() {
		return DEFAULT_INHERITANCE_THRESHOLD;
	}

	/**
	 * Returns the threshold for re-enabling disabled genes during crossover.
	 * 
	 * <p>When a gene (connection) is disabled in one parent but enabled in the other,
	 * this threshold determines the probability that the gene will be enabled in the
	 * offspring. This mechanism prevents permanent loss of potentially useful connections
	 * and allows rediscovery of structural innovations.
	 * 
	 * <p>Re-enabling behavior:
	 * <ul>
	 * <li><strong>0.25 (default)</strong>: 25% chance to re-enable disabled connections</li>
	 * <li><strong>0.0</strong>: Never re-enable disabled connections</li>
	 * <li><strong>1.0</strong>: Always re-enable connections that are enabled in either parent</li>
	 * <li><strong>Higher values</strong>: More aggressive topology exploration</li>
	 * <li><strong>Lower values</strong>: More conservative structural preservation</li>
	 * </ul>
	 * 
	 * @return re-enabling threshold value between 0.0 and 1.0 (inclusive)
	 */
	@Value.Default
	default public double reenableGeneInheritanceThresold() {
		return DEFAULT_REENABLE_GENE_INHERITANCE_THRESHOLD;
	}

	/**
	 * Returns the policy used to compare parent fitness for inheritance decisions.
	 * 
	 * <p>The parent comparison policy determines which parent is considered "fitter"
	 * for the purposes of biased gene inheritance. This affects how disjoint and excess
	 * genes are inherited and how the inheritance threshold is applied.
	 * 
	 * <p>Available comparison strategies:
	 * <ul>
	 * <li><strong>FitnessComparison (default)</strong>: Compare parents based on their fitness values</li>
	 * <li><strong>Custom policies</strong>: Pluggable strategies for domain-specific comparisons</li>
	 * <li><strong>Multi-objective</strong>: Specialized comparisons for multi-objective optimization</li>
	 * <li><strong>Equal fitness handling</strong>: Specific behavior when parents have identical fitness</li>
	 * </ul>
	 * 
	 * @return the parent comparison policy (defaults to fitness-based comparison)
	 */
	@Value.Default
	default public ParentComparisonPolicy parentComparisonPolicy() {
		return FitnessComparison.build();
	}

	@Value.Check
	default void check() {
		Validate.inclusiveBetween(0, 1, inheritanceThresold());
		Validate.inclusiveBetween(0, 1, reenableGeneInheritanceThresold());
	}

	class Builder extends ImmutableNeatCombination.Builder {
	}

	static Builder builder() {
		return new Builder();
	}

	/**
	 * Creates a NEAT combination policy with default settings.
	 * 
	 * <p>Default configuration:
	 * <ul>
	 * <li>Inheritance threshold: 0.5 (unbiased)</li>
	 * <li>Gene re-enabling threshold: 0.25 (25% chance)</li>
	 * <li>Parent comparison: Fitness-based comparison</li>
	 * </ul>
	 * 
	 * @return a new NEAT combination policy with default settings
	 */
	static NeatCombination build() {
		return builder().build();
	}

}