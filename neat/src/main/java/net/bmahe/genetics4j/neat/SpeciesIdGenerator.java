package net.bmahe.genetics4j.neat;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique identifiers for NEAT (NeuroEvolution of Augmenting Topologies) species.
 * 
 * <p>SpeciesIdGenerator provides thread-safe generation of unique species identifiers used
 * to distinguish between different species in the NEAT population. Each species receives
 * a unique ID that remains constant throughout its lifecycle, enabling species tracking
 * across generations and proper species management.
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Thread safety</strong>: Concurrent ID generation for parallel species creation</li>
 * <li><strong>Uniqueness guarantee</strong>: Each generated ID is unique across the generator's lifetime</li>
 * <li><strong>Sequential ordering</strong>: IDs are generated in sequential order for consistent tracking</li>
 * <li><strong>Configurable start</strong>: Initial ID value can be customized for different scenarios</li>
 * </ul>
 * 
 * <p>Species lifecycle integration:
 * <ul>
 * <li><strong>Species creation</strong>: New species receive unique IDs upon formation</li>
 * <li><strong>Species tracking</strong>: IDs enable consistent species identification across generations</li>
 * <li><strong>Population management</strong>: Species IDs facilitate population organization and statistics</li>
 * <li><strong>Evolution monitoring</strong>: IDs enable tracking of species formation, growth, and extinction</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Default species ID generator (starts from 0)
 * SpeciesIdGenerator generator = new SpeciesIdGenerator();
 * 
 * // Custom starting ID
 * SpeciesIdGenerator customGenerator = new SpeciesIdGenerator(1000);
 * 
 * // Generate unique species IDs
 * int speciesId1 = generator.computeNewId();  // Returns 0
 * int speciesId2 = generator.computeNewId();  // Returns 1
 * int speciesId3 = generator.computeNewId();  // Returns 2
 * 
 * // Create species with generated IDs
 * Species<Double> species1 = new Species<>(speciesId1, ancestors1);
 * Species<Double> species2 = new Species<>(speciesId2, ancestors2);
 * 
 * // Thread-safe concurrent generation
 * CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(generator::computeNewId);
 * CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(generator::computeNewId);
 * // Both futures will receive unique IDs
 * }</pre>
 * 
 * <p>Integration with NEAT species management:
 * <ul>
 * <li><strong>Species formation</strong>: New species created during population organization get unique IDs</li>
 * <li><strong>Selection handlers</strong>: Used by NeatSelectionPolicyHandler for species tracking</li>
 * <li><strong>Population statistics</strong>: Enables species-based metrics and analysis</li>
 * <li><strong>Evolution context</strong>: Integrated into NEAT execution contexts for species management</li>
 * </ul>
 * 
 * <p>Thread safety considerations:
 * <ul>
 * <li><strong>Atomic operations</strong>: Uses AtomicInteger for thread-safe ID generation</li>
 * <li><strong>Concurrent access</strong>: Multiple threads can safely generate IDs simultaneously</li>
 * <li><strong>Lock-free</strong>: No synchronization overhead for high-performance scenarios</li>
 * <li><strong>Consistency</strong>: Guarantees unique IDs even under high concurrency</li>
 * </ul>
 * 
 * <p>Performance characteristics:
 * <ul>
 * <li><strong>O(1) generation</strong>: Constant time ID generation</li>
 * <li><strong>Memory efficient</strong>: Minimal memory footprint with single atomic counter</li>
 * <li><strong>High throughput</strong>: Suitable for high-frequency species creation</li>
 * <li><strong>No contention</strong>: Lock-free implementation avoids thread contention</li>
 * </ul>
 * 
 * @see Species
 * @see net.bmahe.genetics4j.neat.selection.NeatSelectionPolicyHandler
 * @see NeatEAExecutionContexts
 */
public class SpeciesIdGenerator {

	public static final int DEFAULT_INITIAL_ID = 0;

	private final AtomicInteger currentId;

	/**
	 * Constructs a new species ID generator with the specified initial value.
	 * 
	 * <p>The initial value determines the first ID that will be generated.
	 * Subsequent IDs will be incremented sequentially from this starting point.
	 * 
	 * @param initialValue the first ID value to generate
	 */
	public SpeciesIdGenerator(final int initialValue) {
		currentId = new AtomicInteger(initialValue);
	}

	/**
	 * Constructs a new species ID generator with the default initial value (0).
	 */
	public SpeciesIdGenerator() {
		this(DEFAULT_INITIAL_ID);
	}

	/**
	 * Generates and returns a new unique species ID.
	 * 
	 * <p>This method atomically increments the internal counter and returns
	 * the previous value, ensuring that each call produces a unique identifier.
	 * The operation is thread-safe and can be called concurrently from multiple
	 * threads without risk of duplicate IDs.
	 * 
	 * @return a unique species identifier
	 */
	public int computeNewId() {
		return currentId.getAndIncrement();
	}
}