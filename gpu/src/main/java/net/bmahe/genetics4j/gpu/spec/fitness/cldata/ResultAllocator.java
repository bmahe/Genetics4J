package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext;

@FunctionalInterface
public interface ResultAllocator {

	CLData load(final OpenCLExecutionContext openCLExecutionContext, final long generation,
			final List<Genotype> genotypes);
}