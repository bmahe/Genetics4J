package net.bmahe.genetics4j.gpu.spec.fitness;

import java.util.List;
import java.util.concurrent.ExecutorService;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext;
import net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext.KernelExecutionContext;

@FunctionalInterface
public interface FitnessExtractor<T extends Comparable<T>> {

	List<T> compute(final OpenCLExecutionContext openCLExecutionContext,
			final KernelExecutionContext kernelExecutionContext, final ExecutorService executorService,
			final long generation, final List<Genotype> genotypes, final ResultExtractor resultExtractor);
}
