package net.bmahe.genetics4j.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Defines a way to evaluate the fitness of a population in an asynchronous
 * manner
 *
 * @param <T>
 */
@FunctionalInterface
public interface FitnessBulkAsync<T extends Comparable<T>> {

	/**
	 * Compute the fitness of a population
	 * 
	 * @param executorService {@link java.util.concurrent.ExecutorService}
	 *                        configured in
	 *                        {@link net.bmahe.genetics4j.core.EASystem}
	 * @param genotypes       Population to evaluate
	 * @return Their fitness
	 */
	CompletableFuture<List<T>> compute(final ExecutorService executorService, final List<Genotype> genotypes);
}