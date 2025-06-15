package net.bmahe.genetics4j.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Functional interface for asynchronous batch fitness evaluation in evolutionary algorithms.
 * 
 * <p>FitnessBulkAsync enables efficient evaluation of entire populations through asynchronous,
 * batch-oriented fitness computation. This approach is particularly beneficial for expensive
 * fitness functions that can be optimized through parallel processing, external service calls,
 * or specialized hardware acceleration like GPUs.
 * 
 * <p>Key advantages of bulk asynchronous evaluation:
 * <ul>
 * <li><strong>Batch optimization</strong>: Process multiple individuals simultaneously for better resource utilization</li>
 * <li><strong>External integration</strong>: Efficient interfacing with databases, web services, or simulation engines</li>
 * <li><strong>Hardware acceleration</strong>: Leverage GPUs, TPUs, or distributed computing resources</li>
 * <li><strong>I/O efficiency</strong>: Minimize network round-trips and database connections through batching</li>
 * <li><strong>Non-blocking execution</strong>: Allow other operations to proceed while fitness computation occurs</li>
 * </ul>
 * 
 * <p>Common use cases include:
 * <ul>
 * <li><strong>Neural network evaluation</strong>: Batch processing through deep learning frameworks</li>
 * <li><strong>Simulation-based fitness</strong>: Running multiple simulations in parallel or on clusters</li>
 * <li><strong>Database queries</strong>: Efficient batch retrieval of fitness data from external sources</li>
 * <li><strong>Web service calls</strong>: Minimize API calls through bulk evaluation requests</li>
 * <li><strong>Scientific computing</strong>: Utilize high-performance computing resources</li>
 * </ul>
 * 
 * <p>Implementation considerations:
 * <ul>
 * <li><strong>Return order</strong>: Fitness values must correspond to input genotypes in the same order</li>
 * <li><strong>Error handling</strong>: Handle individual evaluation failures gracefully within the batch</li>
 * <li><strong>Resource management</strong>: Properly utilize the provided executor service for concurrent operations</li>
 * <li><strong>Cancellation support</strong>: Consider supporting cancellation through CompletableFuture mechanisms</li>
 * </ul>
 * 
 * <p>Example implementations:
 * <pre>{@code
 * // GPU-accelerated batch evaluation
 * FitnessBulkAsync<Double> gpuEvaluator = (executorService, genotypes) -> {
 *     return CompletableFuture.supplyAsync(() -> {
 *         // Convert genotypes to GPU-friendly format
 *         float[][] inputData = convertToGPUFormat(genotypes);
 *         
 *         // Execute batch computation on GPU
 *         float[] results = gpuKernel.evaluate(inputData);
 *         
 *         // Convert back to fitness values
 *         return Arrays.stream(results)
 *                 .boxed()
 *                 .map(Double::valueOf)
 *                 .collect(toList());
 *     }, executorService);
 * };
 * 
 * // External service batch evaluation
 * FitnessBulkAsync<Double> serviceEvaluator = (executorService, genotypes) -> {
 *     return CompletableFuture.supplyAsync(() -> {
 *         // Prepare batch request
 *         BatchEvaluationRequest request = new BatchEvaluationRequest(genotypes);
 *         
 *         // Make single API call for entire batch
 *         BatchEvaluationResponse response = fitnessService.evaluateBatch(request);
 *         
 *         return response.getFitnessValues();
 *     }, executorService);
 * };
 * 
 * // Parallel simulation evaluation
 * FitnessBulkAsync<Double> simulationEvaluator = (executorService, genotypes) -> {
 *     List<CompletableFuture<Double>> futures = genotypes.stream()
 *         .map(genotype -> CompletableFuture.supplyAsync(
 *             () -> runSimulation(genotype), executorService))
 *         .collect(toList());
 *     
 *     return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
 *         .thenApply(ignored -> futures.stream()
 *             .map(CompletableFuture::join)
 *             .collect(toList()));
 * };
 * }</pre>
 * 
 * <p>Integration with EA system:
 * <ul>
 * <li><strong>Configuration</strong>: Used with {@link net.bmahe.genetics4j.core.spec.EAConfigurationBulkAsync}</li>
 * <li><strong>Evaluation flow</strong>: Called by {@link net.bmahe.genetics4j.core.evaluation.FitnessEvaluatorBulkAsync}</li>
 * <li><strong>Thread management</strong>: Receives configured executor service for optimal resource usage</li>
 * <li><strong>Performance monitoring</strong>: Can be wrapped with metrics collection and caching strategies</li>
 * </ul>
 * 
 * @param <T> the type of fitness values produced, must be comparable for selection operations
 * @see net.bmahe.genetics4j.core.spec.EAConfigurationBulkAsync
 * @see net.bmahe.genetics4j.core.evaluation.FitnessEvaluatorBulkAsync
 * @see net.bmahe.genetics4j.core.Fitness
 * @see CompletableFuture
 */
@FunctionalInterface
public interface FitnessBulkAsync<T extends Comparable<T>> {

	/**
	 * Asynchronously computes fitness values for an entire population of genotypes.
	 * 
	 * <p>This method implements the core fitness evaluation logic for bulk asynchronous processing.
	 * Implementations should leverage the provided executor service for optimal concurrency and
	 * return a CompletableFuture that will complete with fitness values corresponding to each
	 * input genotype in the same order.
	 * 
	 * <p>Implementation requirements:
	 * <ul>
	 * <li><strong>Order preservation</strong>: Return fitness values in the same order as input genotypes</li>
	 * <li><strong>Size consistency</strong>: Return exactly one fitness value per input genotype</li>
	 * <li><strong>Executor usage</strong>: Utilize the provided executor service for concurrent operations</li>
	 * <li><strong>Error handling</strong>: Handle evaluation failures gracefully or propagate through CompletableFuture</li>
	 * </ul>
	 * 
	 * <p>Performance considerations:
	 * <ul>
	 * <li>Batch operations where possible to minimize overhead</li>
	 * <li>Use the executor service for CPU-bound parallel operations</li>
	 * <li>Consider async I/O for external service calls</li>
	 * <li>Implement appropriate timeouts for long-running operations</li>
	 * </ul>
	 * 
	 * @param executorService the executor service configured in the EA system for parallel operations
	 * @param genotypes the population of genotypes to evaluate
	 * @return a CompletableFuture that will complete with fitness values corresponding to each genotype
	 * @throws IllegalArgumentException if genotypes is null or empty (implementation-dependent)
	 * @throws RuntimeException if evaluation setup fails (propagated through CompletableFuture)
	 */
	CompletableFuture<List<T>> compute(final ExecutorService executorService, final List<Genotype> genotypes);
}