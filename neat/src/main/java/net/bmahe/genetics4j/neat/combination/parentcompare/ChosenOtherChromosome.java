package net.bmahe.genetics4j.neat.combination.parentcompare;

import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;

/**
 * Represents the result of parent comparison during NEAT genetic crossover.
 * 
 * <p>ChosenOtherChromosome is a simple record that encapsulates the result of comparing two
 * parent chromosomes for NEAT crossover operations. It designates which parent should be
 * considered "chosen" (typically the fitter parent) and which should be "other" for the
 * purposes of biased gene inheritance.
 * 
 * <p>Usage in NEAT crossover:
 * <ul>
 * <li><strong>Inheritance bias</strong>: Chosen parent typically contributes disjoint and excess genes</li>
 * <li><strong>Gene selection</strong>: Inheritance threshold applied relative to chosen parent</li>
 * <li><strong>Topology guidance</strong>: Chosen parent's structure often forms the offspring base</li>
 * <li><strong>Equal fitness handling</strong>: Both parents may be treated equally when fitness is identical</li>
 * </ul>
 * 
 * <p>Parent role significance:
 * <ul>
 * <li><strong>Chosen parent</strong>: Usually the fitter parent, contributes more genes to offspring</li>
 * <li><strong>Other parent</strong>: Usually the less fit parent, contributes fewer genes</li>
 * <li><strong>Matching genes</strong>: Both parents can contribute genes with equal probability (biased)</li>
 * <li><strong>Gene re-enabling</strong>: Both parents considered for connection state decisions</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Result of parent comparison
 * ChosenOtherChromosome comparison = parentHandler.compare(
 *     policy, parent1, parent2, fitnessComparison
 * );
 * 
 * // Extract parents for crossover
 * NeatChromosome chosenParent = comparison.chosen();
 * NeatChromosome otherParent = comparison.other();
 * 
 * // Use in gene inheritance decisions
 * if (fitnessComparison != 0 || random.nextDouble() < inheritanceThreshold) {
 *     // Inherit from chosen parent
 *     inheritGene(chosenParent.getConnections().get(index));
 * } else {
 *     // Inherit from other parent
 *     inheritGene(otherParent.getConnections().get(index));
 * }
 * 
 * // Access parent properties
 * int numInputs = chosenParent.getNumInputs();
 * float minWeight = chosenParent.getMinWeightValue();
 * List<Connection> chosenConnections = chosenParent.getConnections();
 * List<Connection> otherConnections = otherParent.getConnections();
 * }</pre>
 * 
 * <p>Immutability and value semantics:
 * <ul>
 * <li><strong>Immutable</strong>: Record provides immutability for safe passing between methods</li>
 * <li><strong>Value equality</strong>: Two instances are equal if both chosen and other chromosomes match</li>
 * <li><strong>Compact representation</strong>: Minimal memory overhead with clear semantic meaning</li>
 * <li><strong>Null safety</strong>: Both chromosomes are expected to be non-null in valid comparisons</li>
 * </ul>
 * 
 * <p>Integration with comparison handlers:
 * <ul>
 * <li><strong>Return type</strong>: Standard return type for ParentComparisonHandler.compare()</li>
 * <li><strong>Type safety</strong>: Ensures correct parent roles in crossover operations</li>
 * <li><strong>Clear semantics</strong>: Eliminates confusion about which parent is which</li>
 * <li><strong>Consistent interface</strong>: All comparison handlers return same type</li>
 * </ul>
 * 
 * @param chosen the chromosome designated as "chosen" (typically fitter) for preferred inheritance
 * @param other the chromosome designated as "other" (typically less fit) for secondary inheritance
 * @see ParentComparisonHandler
 * @see net.bmahe.genetics4j.neat.combination.NeatChromosomeCombinator
 * @see NeatChromosome
 */
public record ChosenOtherChromosome(NeatChromosome chosen, NeatChromosome other) {
}