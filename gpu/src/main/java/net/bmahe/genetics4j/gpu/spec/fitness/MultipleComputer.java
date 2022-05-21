package net.bmahe.genetics4j.gpu.spec.fitness;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext;
import net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext.KernelExecutionContext;

@FunctionalInterface
public interface MultipleComputer {

	int compute(final OpenCLExecutionContext openCLExecutionContext, final KernelExecutionContext kernelExecutionContext,
			final long generation, final List<Genotype> genotypes);
}