package net.bmahe.genetics4j.core;

/**
 * Functional interface for evaluating the fitness of a genotype in an evolutionary algorithm.
 * 
 * <p>The fitness function is a crucial component of evolutionary algorithms as it determines
 * the quality or performance of individual solutions. It maps a genotype to a fitness value
 * that can be used for selection, ranking, and determining evolutionary progress.
 * 
 * <p>Implementations should be:
 * <ul>
 * <li><strong>Deterministic</strong>: The same genotype should always produce the same fitness value</li>
 * <li><strong>Thread-safe</strong>: May be called concurrently from multiple threads</li>
 * <li><strong>Fast</strong>: Called frequently during evolution, performance matters</li>
 * </ul>
 * 
 * <p>Common fitness function patterns:
 * <ul>
 * <li><strong>Minimization</strong>: Lower values indicate better solutions (errors, costs)</li>
 * <li><strong>Maximization</strong>: Higher values indicate better solutions (profits, accuracy)</li>
 * <li><strong>Multi-objective</strong>: Use FitnessVector from the MOO module for multiple objectives</li>
 * </ul>
 * 
 * @param <T> the type of the fitness value, must be comparable for selection operations
 * @see Genotype
 * @see Individual
 * @see net.bmahe.genetics4j.core.evaluation.FitnessEvaluator
 */
@FunctionalInterface
public interface Fitness<T extends Comparable<T>> {

	/**
	 * Computes the fitness value for the specified genotype.
	 * 
	 * <p>This method should evaluate how well the genotype solves the problem
	 * and return a comparable fitness value. The interpretation of "better" depends
	 * on whether the optimization is for minimization or maximization.
	 * 
	 * @param genotype the genotype to evaluate
	 * @return the fitness value representing the quality of the genotype
	 * @throws RuntimeException if evaluation fails due to invalid genotype or computation error
	 */
	T compute(Genotype genotype);
}