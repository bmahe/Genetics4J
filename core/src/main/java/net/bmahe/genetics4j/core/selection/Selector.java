package net.bmahe.genetics4j.core.selection;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;

/**
 * Functional interface for selecting individuals from a population in evolutionary algorithms.
 * 
 * <p>Selection is a fundamental operator in evolutionary algorithms that determines which individuals
 * from the current population will be chosen to participate in reproduction. The selection pressure
 * influences the direction and speed of evolution by favoring individuals with better fitness values.
 * 
 * <p>Selection operates on a population with associated fitness values and returns a subset of
 * individuals based on the specific selection strategy. The selected individuals typically become
 * parents for the next generation through crossover and mutation operations.
 * 
 * <p>Common selection strategies include:
 * <ul>
 * <li><strong>Tournament selection</strong>: Randomly sample candidates and select the best</li>
 * <li><strong>Roulette wheel selection</strong>: Probabilistic selection based on fitness proportion</li>
 * <li><strong>Rank-based selection</strong>: Selection based on fitness ranking rather than raw values</li>
 * <li><strong>Proportional selection</strong>: Probability proportional to relative fitness</li>
 * <li><strong>Elitism</strong>: Always select the best individuals</li>
 * </ul>
 * 
 * <p>Selection balances two competing objectives:
 * <ul>
 * <li><strong>Exploitation</strong>: Favor high-fitness individuals to improve solution quality</li>
 * <li><strong>Exploration</strong>: Maintain diversity by giving lower-fitness individuals a chance</li>
 * </ul>
 * 
 * <p>Implementations should be:
 * <ul>
 * <li><strong>Stochastic</strong>: Use randomization to prevent premature convergence</li>
 * <li><strong>Fitness-aware</strong>: Consider fitness values when making selection decisions</li>
 * <li><strong>Configurable</strong>: Support parameters to adjust selection pressure</li>
 * </ul>
 * 
 * @param <T> the type of the fitness values, must be comparable for ranking operations
 * @see net.bmahe.genetics4j.core.spec.selection.SelectionPolicy
 * @see Population
 * @see Genotype
 */
@FunctionalInterface
public interface Selector<T extends Comparable<T>> {

	/**
	 * Selects a specified number of individuals from the given population based on their fitness values.
	 * 
	 * <p>This method implements the core selection logic, choosing individuals according to the
	 * specific selection strategy. The selection process typically involves evaluating fitness
	 * values and applying probabilistic or deterministic rules to choose parents for reproduction.
	 * 
	 * <p>The selection process should:
	 * <ul>
	 * <li>Consider fitness values when making selection decisions</li>
	 * <li>Return exactly the requested number of individuals</li>
	 * <li>Allow for the possibility of selecting the same individual multiple times</li>
	 * <li>Maintain the integrity of the original population</li>
	 * </ul>
	 * 
	 * @param eaConfiguration the evolutionary algorithm configuration containing selection parameters
	 * @param numIndividuals the number of individuals to select from the population
	 * @param population the list of genotypes in the current population
	 * @param fitnessScore the list of fitness values corresponding to each genotype
	 * @return a new population containing the selected individuals
	 * @throws IllegalArgumentException if numIndividuals is negative, if population and fitnessScore
	 *                                  have different sizes, or if any parameter is null
	 * @throws IllegalStateException if the selection process cannot complete successfully
	 */
	Population<T> select(final AbstractEAConfiguration<T> eaConfiguration, final int numIndividuals,
			final List<Genotype> population, final List<T> fitnessScore);
}