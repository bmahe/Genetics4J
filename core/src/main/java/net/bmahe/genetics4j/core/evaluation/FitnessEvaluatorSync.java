package net.bmahe.genetics4j.core.evaluation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Fitness;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;

/**
 * Wrapper around {@link net.bmahe.genetics4j.core.Fitness} for computing the
 * fitness of a population
 * <p>
 * In order to leverage multi-core systems and speed up computations, the
 * population will be split according to the number of partitions defined in
 * {@link net.bmahe.genetics4j.core.EASystemnet.bmahe.genetics4j.core.spec.EAExecutionContext#numberOfPartitions()}
 * and processed concurrently
 * 
 * @param <T>
 */
public class FitnessEvaluatorSync<T extends Comparable<T>> implements FitnessEvaluator<T> {
	public static final Logger logger = LogManager.getLogger(FitnessEvaluatorSync.class);

	private final EAExecutionContext<T> eaExecutionContext;
	private final EAConfiguration<T> eaConfigurationSync;

	private final ExecutorService executorService;

	public FitnessEvaluatorSync(final EAExecutionContext<T> _eaExecutionContext,
			final EAConfiguration<T> _eaConfigurationSync, final ExecutorService _executorService) {
		Validate.notNull(_eaExecutionContext);
		Validate.notNull(_eaConfigurationSync);
		Validate.notNull(_executorService);

		this.eaExecutionContext = _eaExecutionContext;
		this.eaConfigurationSync = _eaConfigurationSync;
		this.executorService = _executorService;
	}

	@Override
	public List<T> evaluate(final List<Genotype> population) {
		Validate.notNull(population);
		Validate.isTrue(population.size() > 0);

		final Fitness<T> fitness = eaConfigurationSync.fitness();
		final int numPartitions = eaExecutionContext.numberOfPartitions();
		final int partitionSize = (int) Math.ceil(population.size() / numPartitions);

		final List<CompletableFuture<TaskResult<T>>> tasks = new ArrayList<>();
		for (int i = 0; i < population.size();) {
			final int numSubPopulation = population.size() - i > partitionSize ? partitionSize : population.size() - i;
			final int partitionStart = i;
			final int partitionEnd = partitionStart + numSubPopulation;
			final List<Genotype> partition = population.subList(partitionStart, partitionEnd);
			final CompletableFuture<TaskResult<T>> asyncFitnessCompute = CompletableFuture.supplyAsync(() -> {
				final List<T> fitnessPartition = new ArrayList<>(numSubPopulation);
				for (int j = 0; j < partition.size(); j++) {
					final T fitnessIndividual = fitness.compute(partition.get(j));
					fitnessPartition.add(fitnessIndividual);
				}
				final var taskResult = new TaskResult<>(partitionStart, fitnessPartition);
				return taskResult;
			}, executorService);
			tasks.add(asyncFitnessCompute);

			i += numSubPopulation;
		}

		CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()]));

		final List<T> fitnessScores = new ArrayList<>(population.size());
		tasks.stream()
				.map(CompletableFuture::join)
				.sorted(Comparator.comparingInt(TaskResult::from))
				.forEach(taskResult -> {
					fitnessScores.addAll(taskResult.fitness());
				});

		return fitnessScores;
	}
}