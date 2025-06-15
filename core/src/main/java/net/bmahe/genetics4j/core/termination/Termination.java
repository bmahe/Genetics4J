package net.bmahe.genetics4j.core.termination;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

/**
 * Functional interface for determining when to stop the evolutionary algorithm.
 * 
 * <p>Termination criteria are essential for controlling when an evolutionary algorithm should
 * stop evolving and return its best solutions. The choice of termination criteria significantly
 * affects both the quality of results and computational efficiency.
 * 
 * <p>Termination conditions provide access to various aspects of the evolution state including:
 * <ul>
 * <li><strong>Generation count</strong>: Number of evolutionary cycles completed</li>
 * <li><strong>Population state</strong>: Current genotypes and their fitness values</li>
 * <li><strong>Configuration</strong>: Algorithm parameters and settings</li>
 * <li><strong>Evolution progress</strong>: Fitness improvements and convergence indicators</li>
 * </ul>
 * 
 * <p>Common termination strategies include:
 * <ul>
 * <li><strong>Generation limit</strong>: Stop after a fixed number of generations</li>
 * <li><strong>Fitness threshold</strong>: Stop when best fitness reaches a target value</li>
 * <li><strong>Convergence detection</strong>: Stop when population diversity becomes too low</li>
 * <li><strong>Stagnation detection</strong>: Stop when fitness stops improving for N generations</li>
 * <li><strong>Time limit</strong>: Stop after a specified duration</li>
 * <li><strong>Resource limit</strong>: Stop when computational budget is exhausted</li>
 * </ul>
 * 
 * <p>Best practices for termination criteria:
 * <ul>
 * <li><strong>Multiple criteria</strong>: Combine several conditions using logical operators</li>
 * <li><strong>Problem-specific</strong>: Adapt criteria to the characteristics of the optimization problem</li>
 * <li><strong>Configurable</strong>: Allow users to adjust termination parameters</li>
 * <li><strong>Efficient</strong>: Minimize computational overhead of termination checks</li>
 * </ul>
 * 
 * @param <T> the type of the fitness values, must be comparable for fitness-based termination
 * @see Terminations
 * @see net.bmahe.genetics4j.core.EASystem
 */
@FunctionalInterface
public interface Termination<T extends Comparable<T>> {
	
	/**
	 * Determines whether the evolutionary algorithm should terminate.
	 * 
	 * <p>This method is called after each generation to check if any termination criteria
	 * have been satisfied. The implementation should evaluate the current state of evolution
	 * and return {@code true} if the algorithm should stop, {@code false} otherwise.
	 * 
	 * <p>The method has access to:
	 * <ul>
	 * <li>Current generation number (starting from 0)</li>
	 * <li>Current population genotypes</li>
	 * <li>Current fitness values for all individuals</li>
	 * <li>Algorithm configuration and parameters</li>
	 * </ul>
	 * 
	 * <p>Implementations should be efficient as this method is called frequently during evolution.
	 * Complex termination logic should cache intermediate results when possible.
	 * 
	 * @param eaConfiguration the evolutionary algorithm configuration containing parameters
	 * @param generation the current generation number (0-based)
	 * @param population the list of genotypes in the current population
	 * @param fitness the list of fitness values corresponding to each genotype
	 * @return {@code true} if the algorithm should terminate, {@code false} to continue evolving
	 * @throws IllegalArgumentException if any parameter is null or if population and fitness
	 *                                  lists have different sizes
	 * @throws RuntimeException if termination evaluation fails due to computational errors
	 */
	boolean isDone(final AbstractEAConfiguration<T> eaConfiguration, final long generation, final List<Genotype> population,
			final List<T> fitness);
}