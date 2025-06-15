package net.bmahe.genetics4j.neat;

/**
 * Represents a pair of node indices defining a potential connection in NEAT neural networks.
 * 
 * <p>ConnectionPair is a simple record that encapsulates the source and target node indices
 * for a neural network connection. This immutable data structure is primarily used as a key
 * for innovation number caching in the InnovationManager, enabling efficient lookup and
 * assignment of innovation numbers based on connection topology.
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Immutable</strong>: Record provides immutability for safe use as hash keys</li>
 * <li><strong>Value semantics</strong>: Equality based on both from and to node indices</li>
 * <li><strong>Hash key</strong>: Optimized for use in hash-based collections</li>
 * <li><strong>Lightweight</strong>: Minimal memory overhead with just two integer fields</li>
 * </ul>
 * 
 * <p>Usage in innovation tracking:
 * <ul>
 * <li><strong>Cache key</strong>: Used as key in InnovationManager's innovation cache</li>
 * <li><strong>Connection identification</strong>: Uniquely identifies connection types</li>
 * <li><strong>Historical tracking</strong>: Enables consistent innovation number assignment</li>
 * <li><strong>Population consistency</strong>: Same connection pairs get same innovation numbers</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create connection pair for innovation tracking
 * ConnectionPair pair = new ConnectionPair(0, 3);  // Input 0 -> Output 3
 * 
 * // Use as cache key in innovation manager
 * Map<ConnectionPair, Integer> innovationCache = new HashMap<>();
 * int innovationNumber = innovationCache.computeIfAbsent(pair, 
 *     k -> innovationManager.getNextInnovationNumber());
 * 
 * // Connection pair represents potential connection topology
 * List<ConnectionPair> possibleConnections = List.of(
 *     new ConnectionPair(0, 2),  // Input 0 -> Output 2
 *     new ConnectionPair(1, 2),  // Input 1 -> Output 2
 *     new ConnectionPair(0, 3),  // Input 0 -> Output 3
 *     new ConnectionPair(1, 3)   // Input 1 -> Output 3
 * );
 * }</pre>
 * 
 * <p>Integration with NEAT algorithm:
 * <ul>
 * <li><strong>Innovation management</strong>: Key component of innovation number caching</li>
 * <li><strong>Structural mutations</strong>: Used when adding new connections to networks</li>
 * <li><strong>Genetic tracking</strong>: Enables consistent gene identification across population</li>
 * <li><strong>Crossover operations</strong>: Supports gene alignment during recombination</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Efficient hashing</strong>: Record provides optimized hash code implementation</li>
 * <li><strong>Memory efficient</strong>: Compact representation with minimal overhead</li>
 * <li><strong>Cache friendly</strong>: Small size improves cache locality</li>
 * <li><strong>GC efficient</strong>: Immutable objects reduce garbage collection pressure</li>
 * </ul>
 * 
 * <p>Value semantics:
 * <ul>
 * <li><strong>Equality</strong>: Two pairs are equal if both from and to indices match</li>
 * <li><strong>Hash code</strong>: Computed from both node indices for efficient hash table usage</li>
 * <li><strong>String representation</strong>: Readable format showing connection direction</li>
 * <li><strong>Comparison</strong>: Natural ordering based on from index, then to index</li>
 * </ul>
 * 
 * @param from the source node index of the connection
 * @param to the target node index of the connection
 * @see InnovationManager
 * @see Connection
 * @see net.bmahe.genetics4j.neat.mutation.AddConnectionPolicyHandler
 */
public record ConnectionPair(int from, int to) {

}