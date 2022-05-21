package net.bmahe.genetics4j.gpu.spec.fitness;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext;

public abstract class OpenCLFitness<T extends Comparable<T>> {
	public static final Logger logger = LogManager.getLogger(OpenCLFitness.class);

	public void beforeAllEvaluations() {
	}

	public void beforeAllEvaluations(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService) {
	}

	public void beforeEvaluation(final long generation, final List<Genotype> genotypes) {
	}

	public void beforeEvaluation(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService, final long generation, final List<Genotype> genotypes) {
	}

	public abstract CompletableFuture<List<T>> compute(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService, final long generation, final List<Genotype> genotypes);

	public void afterEvaluation(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService, final long generation, final List<Genotype> genotypes) {
	}

	public void afterEvaluation(final long generation, final List<Genotype> genotypes) {
	}

	public void afterAllEvaluations(final OpenCLExecutionContext openCLExecutionContext,
			final ExecutorService executorService) {
	}

	public void afterAllEvaluations() {
	}
}