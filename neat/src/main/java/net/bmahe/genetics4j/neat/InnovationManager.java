package net.bmahe.genetics4j.neat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages innovation numbers for the NEAT (NeuroEvolution of Augmenting Topologies) algorithm.
 * 
 * <p>The InnovationManager is a critical component of the NEAT algorithm that tracks structural innovations
 * in neural networks through unique innovation numbers. This system enables NEAT to perform meaningful
 * genetic crossover between neural networks with different topologies while preserving historical information
 * about when specific connections were first added to the population.
 * 
 * <p>Key responsibilities:
 * <ul>
 * <li><strong>Innovation tracking</strong>: Assigns unique numbers to each new connection type (from-to node pair)</li>
 * <li><strong>Historical marking</strong>: Maintains consistent innovation numbers across the population</li>
 * <li><strong>Crossover support</strong>: Enables alignment of neural network structures during recombination</li>
 * <li><strong>Cache management</strong>: Provides efficient lookup and generation of innovation numbers</li>
 * </ul>
 * 
 * <p>NEAT innovation number system:
 * <ul>
 * <li><strong>Unique identification</strong>: Each unique connection (from-node → to-node) gets one innovation number</li>
 * <li><strong>Population consistency</strong>: Same connection type across individuals gets same innovation number</li>
 * <li><strong>Temporal ordering</strong>: Innovation numbers reflect the historical order of structural mutations</li>
 * <li><strong>Crossover alignment</strong>: Enables gene alignment during genetic recombination</li>
 * </ul>
 * 
 * <p>Innovation number workflow:
 * <ol>
 * <li><strong>Mutation occurs</strong>: Add-connection mutation creates new connection type</li>
 * <li><strong>Innovation check</strong>: Manager checks if this connection type was seen before</li>
 * <li><strong>Number assignment</strong>: New types get new innovation numbers, existing types reuse numbers</li>
 * <li><strong>Population tracking</strong>: All individuals with same connection type share same innovation number</li>
 * <li><strong>Crossover alignment</strong>: Innovation numbers enable proper gene alignment during recombination</li>
 * </ol>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create innovation manager for new evolution run
 * InnovationManager innovationManager = new InnovationManager();
 * 
 * // During add-connection mutation
 * int fromNode = 0, toNode = 3;
 * int innovationNumber = innovationManager.computeNewId(fromNode, toNode);
 * Connection newConnection = Connection.of(fromNode, toNode, weight, innovationNumber, true);
 * 
 * // Reset cache between generations if needed
 * innovationManager.resetCache();
 * 
 * // Start with specific innovation number
 * InnovationManager manager = new InnovationManager(1000);
 * }</pre>
 * 
 * <p>Cache management strategy:
 * <ul>
 * <li><strong>Per-generation caching</strong>: Cache innovation numbers within each generation</li>
 * <li><strong>Cross-generation persistence</strong>: Innovation numbers remain consistent across generations</li>
 * <li><strong>Memory management</strong>: Reset cache periodically to prevent memory growth</li>
 * <li><strong>Concurrent access</strong>: Thread-safe operations for parallel genetic operations</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>O(1) lookup</strong>: Fast innovation number retrieval through hash map caching</li>
 * <li><strong>Memory efficiency</strong>: Cache only unique connection types seen in current generation</li>
 * <li><strong>Thread safety</strong>: Concurrent operations supported for parallel evolution</li>
 * <li><strong>Cache lifecycle</strong>: Reset cache between evolution runs to prevent memory leaks</li>
 * </ul>
 * 
 * <p>Integration with NEAT algorithm:
 * <ul>
 * <li><strong>Structural mutations</strong>: Add-connection mutations use innovation manager</li>
 * <li><strong>Genetic crossover</strong>: Innovation numbers enable gene alignment</li>
 * <li><strong>Compatibility distance</strong>: Innovation numbers used to identify matching, excess, and disjoint genes</li>
 * <li><strong>Population management</strong>: Shared innovation manager across entire population</li>
 * </ul>
 * 
 * @see Connection
 * @see ConnectionPair
 * @see NeatChromosome
 * @see net.bmahe.genetics4j.neat.mutation.AddConnectionPolicyHandler
 */
public class InnovationManager {
	public static final Logger logger = LogManager.getLogger(InnovationManager.class);

	public static final int DEFAULT_INITIAL_ID = 0;

	private final AtomicInteger currentId;

	private final ConcurrentHashMap<ConnectionPair, Integer> innovationCache = new ConcurrentHashMap<>();

	/**
	 * Constructs an innovation manager with the specified initial innovation number.
	 * 
	 * @param initialValue the starting innovation number for new innovations
	 */
	public InnovationManager(final int initialValue) {
		currentId = new AtomicInteger(initialValue);
	}

	/**
	 * Constructs an innovation manager with the default initial innovation number (0).
	 */
	public InnovationManager() {
		this(DEFAULT_INITIAL_ID);
	}

	/**
	 * Computes or retrieves the innovation number for a connection between two nodes.
	 * 
	 * <p>If this connection type (from-node → to-node) has been seen before, returns the existing
	 * innovation number from the cache. Otherwise, generates a new innovation number and caches it
	 * for future use. This ensures that the same connection type across different individuals in
	 * the population receives the same innovation number.
	 * 
	 * @param from the source node index of the connection
	 * @param to the target node index of the connection
	 * @return the innovation number for this connection type
	 * @throws IllegalArgumentException if from equals to (self-connections not allowed)
	 */
	public int computeNewId(final int from, final int to) {
		Validate.isTrue(from != to);

		final var connectionPair = new ConnectionPair(from, to);
		return innovationCache.computeIfAbsent(connectionPair, k -> currentId.getAndIncrement());
	}

	/**
	 * Resets the innovation cache, clearing all cached connection-to-innovation-number mappings.
	 * 
	 * <p>This method should be called between evolution runs or generations to prevent memory
	 * growth and ensure that innovation number assignment starts fresh. Note that this does
	 * not reset the current innovation number counter, so new innovations will continue to
	 * receive unique numbers.
	 */
	public void resetCache() {
		logger.trace("Resetting cache with currently {} entries", innovationCache.size());
		innovationCache.clear();
	}
}