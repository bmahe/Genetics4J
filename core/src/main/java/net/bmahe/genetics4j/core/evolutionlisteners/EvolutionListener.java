package net.bmahe.genetics4j.core.evolutionlisteners;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;

/**
 * Functional interface for monitoring and responding to evolution progress during genetic algorithm execution.
 * 
 * <p>EvolutionListener provides a callback mechanism that allows external components to observe the
 * evolutionary process as it unfolds. Listeners are notified after each generation with complete
 * information about the current population state, enabling real-time monitoring, logging, and analysis.
 * 
 * <p>Evolution listeners are commonly used for:
 * <ul>
 * <li><strong>Progress monitoring</strong>: Track fitness improvements and convergence trends</li>
 * <li><strong>Data logging</strong>: Record population statistics and best solutions</li>
 * <li><strong>Visualization</strong>: Update real-time charts and graphs of evolution progress</li>
 * <li><strong>Adaptive control</strong>: Modify algorithm parameters based on evolution state</li>
 * <li><strong>Early stopping</strong>: Implement custom termination conditions</li>
 * <li><strong>Checkpointing</strong>: Save intermediate results for recovery and analysis</li>
 * </ul>
 * 
 * <p>Listeners receive comprehensive information about each generation:
 * <ul>
 * <li><strong>Generation number</strong>: Current iteration count (0-based)</li>
 * <li><strong>Population snapshot</strong>: All genotypes in the current generation</li>
 * <li><strong>Fitness values</strong>: Corresponding fitness scores for each individual</li>
 * <li><strong>Completion status</strong>: Whether termination criteria have been met</li>
 * </ul>
 * 
 * <p>Implementation considerations:
 * <ul>
 * <li><strong>Performance impact</strong>: Listeners are called frequently; keep implementations efficient</li>
 * <li><strong>Thread safety</strong>: May be called from different threads in parallel execution contexts</li>
 * <li><strong>Exception handling</strong>: Uncaught exceptions may terminate the evolution process</li>
 * <li><strong>Memory usage</strong>: Be careful with references to population data to avoid memory leaks</li>
 * </ul>
 * 
 * <p>Example implementations:
 * <pre>{@code
 * // Simple fitness tracker
 * EvolutionListener<Double> fitnessTracker = (generation, population, fitness, isDone) -> {
 *     double bestFitness = fitness.stream().max(Double::compare).orElse(0.0);
 *     double avgFitness = fitness.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
 *     System.out.printf("Generation %d: Best=%.3f, Avg=%.3f%n", generation, bestFitness, avgFitness);
 * };
 * 
 * // CSV logging listener
 * EvolutionListener<Double> csvLogger = (generation, population, fitness, isDone) -> {
 *     if (generation % 10 == 0) { // Log every 10 generations
 *         logToCSV(generation, fitness);
 *     }
 * };
 * 
 * // Convergence monitor
 * EvolutionListener<Double> convergenceMonitor = new ConvergenceListener(0.001, 50);
 * }</pre>
 * 
 * <p>The framework provides several built-in listener implementations:
 * <ul>
 * <li>{@link EvolutionListenerLogTopN}: Logs top N individuals each generation</li>
 * <li>{@link SimpleEvolutionListener}: Basic console output for development</li>
 * <li>CSV loggers in the extras module for data export</li>
 * </ul>
 * 
 * @param <T> the type of fitness values being used in the evolutionary algorithm
 * @see net.bmahe.genetics4j.core.EASystem
 * @see EvolutionListeners
 * @see EvolutionListenerLogTopN
 * @see SimpleEvolutionListener
 */
@FunctionalInterface
public interface EvolutionListener<T> {

	/**
	 * Called after each generation to notify about evolution progress.
	 * 
	 * <p>This method is invoked by the evolutionary algorithm after each generation has been
	 * completed, providing access to the current population state and fitness values. The
	 * implementation can use this information for monitoring, logging, or adaptive control.
	 * 
	 * <p>The method is called with:
	 * <ul>
	 * <li>Current generation number (starting from 0)</li>
	 * <li>Complete population of genotypes for this generation</li>
	 * <li>Corresponding fitness values for each individual</li>
	 * <li>Flag indicating whether evolution has completed</li>
	 * </ul>
	 * 
	 * <p>Important notes:
	 * <ul>
	 * <li>Population and fitness lists are guaranteed to have the same size</li>
	 * <li>Fitness values correspond to genotypes at the same index</li>
	 * <li>Data may be shared with the evolution algorithm; avoid modification</li>
	 * <li>Method should execute quickly to avoid impacting evolution performance</li>
	 * </ul>
	 * 
	 * @param generation the current generation number (0-based)
	 * @param population the list of genotypes in the current generation
	 * @param fitness the list of fitness values corresponding to each genotype
	 * @param isDone {@code true} if the evolution has completed, {@code false} otherwise
	 * @throws RuntimeException if the listener encounters an error that should halt evolution
	 */
	void onEvolution(final long generation, final List<Genotype> population, final List<T> fitness,
			final boolean isDone);
}