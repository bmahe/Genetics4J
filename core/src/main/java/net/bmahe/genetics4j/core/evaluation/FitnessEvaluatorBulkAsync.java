package net.bmahe.genetics4j.core.evaluation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.FitnessBulkAsync;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.EAConfigurationBulkAsync;

/**
 * Wrapper around {@link net.bmahe.genetics4j.core.FitnessBulkAsync} for
 * computing the fitness of a population
 *
 * @param <T>
 */
public class FitnessEvaluatorBulkAsync<T extends Comparable<T>> implements FitnessEvaluator<T> {

	public static final Logger logger = LogManager.getLogger(FitnessEvaluatorBulkAsync.class);

	private final EAConfigurationBulkAsync<T> eaConfigurationBulkAsync;

	private final ExecutorService executorService;

	public FitnessEvaluatorBulkAsync(final EAConfigurationBulkAsync<T> _eaConfigurationBulkAsync,
			final ExecutorService _executorService) {
		Validate.notNull(_eaConfigurationBulkAsync);
		Validate.notNull(_executorService);

		this.eaConfigurationBulkAsync = _eaConfigurationBulkAsync;
		this.executorService = _executorService;
	}

	@Override
	public List<T> evaluate(final long generation, final List<Genotype> genotypes) {
		Validate.isTrue(generation >= 0);
		Validate.notNull(genotypes);
		Validate.isTrue(genotypes.size() > 0);

		final FitnessBulkAsync<T> fitnessBulkAsync = eaConfigurationBulkAsync.fitness();

		logger.trace("Submitting fitness computation task");
		final CompletableFuture<List<T>> fitnessesCF = fitnessBulkAsync.compute(executorService, genotypes);

		return fitnessesCF.join();
	}
}