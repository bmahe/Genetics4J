package net.bmahe.genetics4j.core.evaluation;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;

/**
 * Facade to abstract the various ways that fitnesses could be evaluated
 *
 * @param <T>
 */
public interface FitnessEvaluator<T extends Comparable<T>> {

	/**
	 * Compute the fitness for a list of genotypes
	 * 
	 * @param genotypes Population to evaluate
	 * @return Their associated fitnesses
	 */
	List<T> evaluate(final List<Genotype> genotypes);
}