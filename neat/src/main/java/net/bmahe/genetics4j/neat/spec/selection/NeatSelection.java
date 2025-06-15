package net.bmahe.genetics4j.neat.spec.selection;

import java.util.function.BiPredicate;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.spec.selection.SelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.neat.NeatUtils;

/**
 * Selection policy for NEAT (NeuroEvolution of Augmenting Topologies) species-based selection.
 * 
 * <p>NeatSelection implements the species-based selection mechanism that is fundamental to NEAT's
 * ability to maintain population diversity and protect structural innovations. It organizes the
 * population into species based on genetic compatibility, applies fitness sharing within species,
 * and manages reproduction allocation to prevent dominant topologies from eliminating exploration.
 * 
 * <p>Key features:
 * <ul>
 * <li><strong>Species formation</strong>: Groups genetically similar individuals using compatibility predicates</li>
 * <li><strong>Fitness sharing</strong>: Reduces fitness pressure within species to promote diversity</li>
 * <li><strong>Species preservation</strong>: Maintains minimum viable species sizes</li>
 * <li><strong>Reproduction allocation</strong>: Distributes offspring based on species average fitness</li>
 * </ul>
 * 
 * <p>NEAT species-based selection process:
 * <ol>
 * <li><strong>Compatibility testing</strong>: Apply species predicate to group similar individuals</li>
 * <li><strong>Species assignment</strong>: Assign individuals to species based on genetic distance</li>
 * <li><strong>Fitness adjustment</strong>: Apply fitness sharing within each species</li>
 * <li><strong>Species filtering</strong>: Remove species below minimum size threshold</li>
 * <li><strong>Reproduction allocation</strong>: Determine offspring count per species</li>
 * <li><strong>Within-species selection</strong>: Select parents using specified selection policy</li>
 * </ol>
 * 
 * <p>Species management parameters:
 * <ul>
 * <li><strong>Keep ratio</strong>: Proportion of each species to preserve for reproduction</li>
 * <li><strong>Minimum size</strong>: Smallest viable species size to prevent extinction</li>
 * <li><strong>Compatibility predicate</strong>: Function determining species membership</li>
 * <li><strong>Selection policy</strong>: Within-species selection strategy</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Default NEAT selection with standard parameters
 * NeatSelection<Double> defaultSelection = NeatSelection.ofDefault();
 * 
 * // Custom compatibility threshold
 * BiPredicate<Individual<Double>, Individual<Double>> compatibilityPredicate = 
 *     (i1, i2) -> NeatUtils.compatibilityDistance(
 *         i1.genotype(), i2.genotype(), 0, 2, 2, 1.0f
 *     ) < 3.0;  // Higher threshold = fewer, larger species
 * 
 * NeatSelection<Double> customSelection = NeatSelection.<Double>builder()
 *     .perSpeciesKeepRatio(0.8f)  // Keep top 80% of each species
 *     .minSpeciesSize(3)  // Minimum 3 individuals per species
 *     .speciesPredicate(compatibilityPredicate)
 *     .speciesSelection(Tournament.of(5))  // Tournament size 5 within species
 *     .build();
 * 
 * // Aggressive diversity preservation
 * NeatSelection<Double> diverseSelection = NeatSelection.of(
 *     0.95f,  // Keep 95% of each species
 *     compatibilityPredicate,
 *     new ProportionalSelection()  // Proportional selection within species
 * );
 * }</pre>
 * 
 * <p>Compatibility distance calculation:
 * <ul>
 * <li><strong>Matching genes</strong>: Genes with same innovation numbers in both individuals</li>
 * <li><strong>Disjoint genes</strong>: Genes in one individual within the other's innovation range</li>
 * <li><strong>Excess genes</strong>: Genes beyond the other individual's highest innovation number</li>
 * <li><strong>Weight differences</strong>: Average difference in matching gene weights</li>
 * </ul>
 * 
 * <p>Species preservation strategies:
 * <ul>
 * <li><strong>Keep ratio</strong>: Ensures a proportion of each species survives selection pressure</li>
 * <li><strong>Minimum size</strong>: Prevents viable species from going extinct due to random drift</li>
 * <li><strong>Fitness sharing</strong>: Reduces competition between similar individuals</li>
 * <li><strong>Innovation protection</strong>: Gives new topologies time to optimize</li>
 * </ul>
 * 
 * <p>Integration with genetic operators:
 * <ul>
 * <li><strong>Crossover compatibility</strong>: Species ensure genetic similarity for meaningful recombination</li>
 * <li><strong>Mutation guidance</strong>: Species composition can influence mutation rates</li>
 * <li><strong>Structural innovation</strong>: Protected evolution of different network topologies</li>
 * <li><strong>Population dynamics</strong>: Species formation and extinction drive exploration</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Compatibility caching</strong>: Distance calculations cached for efficiency</li>
 * <li><strong>Species reuse</strong>: Species structures maintained across generations</li>
 * <li><strong>Parallel evaluation</strong>: Species-based organization enables concurrent processing</li>
 * <li><strong>Memory efficiency</strong>: Efficient species membership tracking</li>
 * </ul>
 * 
 * @param <T> the fitness value type (typically Double)
 * @see SelectionPolicy
 * @see NeatUtils#compatibilityDistance
 * @see net.bmahe.genetics4j.neat.selection.NeatSelectionPolicyHandler
 * @see net.bmahe.genetics4j.neat.Species
 */
@Value.Immutable
public abstract class NeatSelection<T extends Comparable<T>> implements SelectionPolicy {

	/**
	 * Returns the proportion of each species to preserve for reproduction.
	 * 
	 * <p>This ratio determines what fraction of each species will be retained
	 * after fitness-based culling. Higher values preserve more diversity within
	 * species but may slow convergence, while lower values increase selection
	 * pressure but may lose beneficial genetic variations.
	 * 
	 * <p>Typical values:
	 * <ul>
	 * <li><strong>0.9 (default)</strong>: Preserve top 90% of each species</li>
	 * <li><strong>0.8-0.95</strong>: Balanced diversity preservation</li>
	 * <li><strong>&lt; 0.8</strong>: Aggressive selection pressure</li>
	 * <li><strong>&gt; 0.95</strong>: Minimal selection pressure, maximum diversity</li>
	 * </ul>
	 * 
	 * @return keep ratio between 0.0 and 1.0 (exclusive of 0.0, inclusive of 1.0)
	 */
	@Value.Default
	public float perSpeciesKeepRatio() {
		return 0.90f;
	}

	/**
	 * Returns the minimum number of individuals required to maintain a species.
	 * 
	 * <p>Species with fewer members than this threshold will be eliminated to
	 * prevent resource waste on non-viable populations. This helps focus
	 * evolutionary resources on species with sufficient genetic diversity
	 * to explore their local fitness landscape effectively.
	 * 
	 * <p>Typical values:
	 * <ul>
	 * <li><strong>5 (default)</strong>: Balanced viability threshold</li>
	 * <li><strong>3-10</strong>: Reasonable range for most problems</li>
	 * <li><strong>&lt; 3</strong>: Very permissive, allows small species to survive</li>
	 * <li><strong>&gt; 10</strong>: Strict threshold, eliminates marginal species</li>
	 * </ul>
	 * 
	 * @return minimum species size (must be positive)
	 */
	@Value.Default
	public int minSpeciesSize() {
		return 5;
	}

	/**
	 * Returns the predicate used to determine species membership.
	 * 
	 * <p>This bi-predicate takes two individuals and returns true if they should
	 * belong to the same species based on their genetic compatibility. Typically
	 * implemented using NEAT compatibility distance with a threshold value.
	 * 
	 * <p>Common implementations:
	 * <ul>
	 * <li><strong>Compatibility distance</strong>: Based on matching, disjoint, excess genes and weight differences</li>
	 * <li><strong>Topological similarity</strong>: Based on network structure similarity</li>
	 * <li><strong>Behavioral similarity</strong>: Based on network output patterns</li>
	 * <li><strong>Custom metrics</strong>: Domain-specific similarity measures</li>
	 * </ul>
	 * 
	 * @return bi-predicate for determining species membership
	 */
	public abstract BiPredicate<Individual<T>, Individual<T>> speciesPredicate();

	/**
	 * Returns the selection policy used within each species.
	 * 
	 * <p>After individuals are organized into species, this policy determines
	 * how parents are selected within each species for reproduction. Common
	 * choices include tournament selection, proportional selection, or rank-based
	 * selection.
	 * 
	 * <p>Selection policy considerations:
	 * <ul>
	 * <li><strong>Tournament selection</strong>: Good balance of selection pressure and diversity</li>
	 * <li><strong>Proportional selection</strong>: Fitness-proportionate selection within species</li>
	 * <li><strong>Rank selection</strong>: Rank-based selection to avoid fitness scaling issues</li>
	 * <li><strong>Elite selection</strong>: Always select best individuals within species</li>
	 * </ul>
	 * 
	 * @return selection policy for within-species parent selection
	 */
	public abstract SelectionPolicy speciesSelection();

	@Value.Check
	public void check() {
		Validate.inclusiveBetween(0.0f, 1.0f, perSpeciesKeepRatio());
		Validate.isTrue(perSpeciesKeepRatio() > 0.0f);
	}

	public static class Builder<T extends Comparable<T>> extends ImmutableNeatSelection.Builder<T> {
	}

	public static <U extends Comparable<U>> Builder<U> builder() {
		return new Builder<U>();
	}

	/**
	 * Creates a NEAT selection policy with custom keep ratio and specified parameters.
	 * 
	 * @param <U> the fitness value type
	 * @param perSpeciesKeepRatio proportion of each species to preserve (0.0 < ratio <= 1.0)
	 * @param speciesPredicate predicate for determining species membership
	 * @param speciesSelection selection policy for within-species parent selection
	 * @return a new NEAT selection policy with the specified parameters
	 */
	public static <U extends Comparable<U>> NeatSelection<U> of(final float perSpeciesKeepRatio,
			final BiPredicate<Individual<U>, Individual<U>> speciesPredicate, final SelectionPolicy speciesSelection) {
		return new Builder<U>().perSpeciesKeepRatio(perSpeciesKeepRatio)
				.speciesPredicate(speciesPredicate)
				.speciesSelection(speciesSelection)
				.build();
	}

	/**
	 * Creates a NEAT selection policy with default keep ratio and specified parameters.
	 * 
	 * @param <U> the fitness value type
	 * @param speciesPredicate predicate for determining species membership
	 * @param speciesSelection selection policy for within-species parent selection
	 * @return a new NEAT selection policy with default keep ratio (0.9)
	 */
	public static <U extends Comparable<U>> NeatSelection<U> of(
			final BiPredicate<Individual<U>, Individual<U>> speciesPredicate, final SelectionPolicy speciesSelection) {
		return new Builder<U>().speciesPredicate(speciesPredicate)
				.speciesSelection(speciesSelection)
				.build();
	}

	/**
	 * Creates a NEAT selection policy with standard default parameters.
	 * 
	 * <p>Default configuration:
	 * <ul>
	 * <li><strong>Keep ratio</strong>: 0.9 (preserve top 90% of each species)</li>
	 * <li><strong>Minimum species size</strong>: 5 individuals</li>
	 * <li><strong>Compatibility distance</strong>: Threshold of 1.0 with standard coefficients</li>
	 * <li><strong>Species selection</strong>: Tournament selection with size 3</li>
	 * </ul>
	 * 
	 * <p>Compatibility distance uses:
	 * <ul>
	 * <li>Weight coefficient: 1.0</li>
	 * <li>Excess gene coefficient: 2.0</li>
	 * <li>Disjoint gene coefficient: 2.0</li>
	 * <li>Distance threshold: 1.0</li>
	 * </ul>
	 * 
	 * @param <U> the fitness value type
	 * @return a new NEAT selection policy with standard default parameters
	 */
	public static <U extends Comparable<U>> NeatSelection<U> ofDefault() {
		return new Builder<U>()
				.speciesPredicate(
						(i1, i2) -> NeatUtils.compatibilityDistance(i1.genotype(), i2.genotype(), 0, 2, 2, 1f) < 1.0)
				.speciesSelection(Tournament.of(3))
				.build();
	}
}