package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext;

@FunctionalInterface
public interface StaticDataLoader {

	CLData load(final OpenCLExecutionContext openCLExecutionContext);
}