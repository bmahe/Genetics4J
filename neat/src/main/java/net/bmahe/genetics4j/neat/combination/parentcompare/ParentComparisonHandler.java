package net.bmahe.genetics4j.neat.combination.parentcompare;

import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.ParentComparisonPolicy;

/**
 * Interface for handling parent comparison strategies during NEAT genetic crossover.
 * 
 * <p>ParentComparisonHandler defines the contract for comparing two parent chromosomes
 * to determine which should be considered "chosen" (typically the fitter parent) and
 * which should be considered "other" (typically the less fit parent) for inheritance
 * decisions during NEAT crossover operations.
 * 
 * <p>Purpose in NEAT crossover:
 * <ul>
 * <li><strong>Inheritance bias</strong>: Determines which parent contributes disjoint and excess genes</li>
 * <li><strong>Fitness-based selection</strong>: Guides gene inheritance based on parent performance</li>
 * <li><strong>Equal fitness handling</strong>: Provides strategy when parents have identical fitness</li>
 * <li><strong>Policy abstraction</strong>: Allows pluggable comparison strategies</li>
 * </ul>
 * 
 * <p>Comparison result usage:
 * <ul>
 * <li><strong>Chosen parent</strong>: Usually contributes disjoint and excess genes</li>
 * <li><strong>Other parent</strong>: May contribute genes when inheritance is unbiased</li>
 * <li><strong>Matching genes</strong>: Randomly inherited from either parent with bias</li>
 * <li><strong>Gene re-enabling</strong>: Both parents considered for connection state decisions</li>
 * </ul>
 * 
 * <p>Common implementation patterns:
 * <pre>{@code
 * // Simple fitness-based comparison handler
 * public class FitnessComparisonHandler implements ParentComparisonHandler {
 *     
 *     public boolean canHandle(ParentComparisonPolicy policy) {
 *         return policy instanceof FitnessComparison;
 *     }
 *     
 *     public ChosenOtherChromosome compare(ParentComparisonPolicy policy, 
 *             NeatChromosome first, NeatChromosome second, int fitnessComparison) {
 *         if (fitnessComparison >= 0) {
 *             return new ChosenOtherChromosome(first, second);  // First is fitter
 *         } else {
 *             return new ChosenOtherChromosome(second, first); // Second is fitter
 *         }
 *     }
 * }
 * 
 * // Usage in crossover
 * ParentComparisonHandler handler = new FitnessComparisonHandler();
 * ChosenOtherChromosome result = handler.compare(
 *     policy, parent1, parent2, fitnessComparator.compare(fitness1, fitness2)
 * );
 * NeatChromosome chosenParent = result.chosen();
 * NeatChromosome otherParent = result.other();
 * }</pre>
 * 
 * <p>Integration with crossover:
 * <ul>
 * <li><strong>Gene inheritance</strong>: Chosen parent typically dominates gene contribution</li>
 * <li><strong>Bias application</strong>: Inheritance threshold applied relative to chosen parent</li>
 * <li><strong>Topology preservation</strong>: Chosen parent's topology forms the base structure</li>
 * <li><strong>Innovation tracking</strong>: Both parents contribute to innovation alignment</li>
 * </ul>
 * 
 * <p>Policy-based design:
 * <ul>
 * <li><strong>Strategy pattern</strong>: Different policies enable different comparison strategies</li>
 * <li><strong>Extensibility</strong>: New comparison strategies can be added without changing crossover code</li>
 * <li><strong>Configuration</strong>: Comparison behavior controlled by policy parameters</li>
 * <li><strong>Testing</strong>: Easy to test different comparison strategies in isolation</li>
 * </ul>
 * 
 * @see ParentComparisonPolicy
 * @see ChosenOtherChromosome
 * @see NeatChromosome
 * @see net.bmahe.genetics4j.neat.combination.NeatChromosomeCombinator
 */
public interface ParentComparisonHandler {

	/**
	 * Determines whether this handler can process the given parent comparison policy.
	 * 
	 * <p>This method allows the handler registry to determine which handler is appropriate
	 * for a given comparison policy type, enabling polymorphic behavior in the crossover
	 * process.
	 * 
	 * @param parentComparisonPolicy the comparison policy to check
	 * @return true if this handler can process the policy, false otherwise
	 */
	boolean canHandle(final ParentComparisonPolicy parentComparisonPolicy);

	/**
	 * Compares two parent chromosomes and determines which should be chosen for preferred inheritance.
	 * 
	 * <p>This method analyzes the two parent chromosomes and their relative fitness to determine
	 * which parent should be considered "chosen" (typically the fitter parent) and which should
	 * be "other" for the purposes of biased gene inheritance during crossover.
	 * 
	 * <p>The fitness comparison parameter provides the result of comparing the parents' fitness
	 * values: positive if first is fitter, negative if second is fitter, zero if equal.
	 * 
	 * @param parentComparisonPolicy the comparison policy defining the comparison strategy
	 * @param first the first parent chromosome
	 * @param second the second parent chromosome
	 * @param fitnessComparison result of fitness comparison (positive: first fitter, negative: second fitter, zero: equal)
	 * @return ChosenOtherChromosome containing the chosen parent and other parent
	 */
	ChosenOtherChromosome compare(final ParentComparisonPolicy parentComparisonPolicy, final NeatChromosome first,
			final NeatChromosome second, final int fitnessComparison);
}