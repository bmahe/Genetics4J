package net.bmahe.genetics4j.gpu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.gpu.spec.GPUEAConfiguration;
import net.bmahe.genetics4j.gpu.spec.GPUEAExecutionContext;

public class GPUEASystemFactory {

	private GPUEASystemFactory() {
	}

	public static <T extends Comparable<T>> EASystem<T> from(final GPUEAConfiguration<T> gpuEAConfiguration,
			final GPUEAExecutionContext<T> gpuEAExecutionContext, final ExecutorService executorService) {

		final var gpuFitnessEvaluator = new GPUFitnessEvaluator<T>(gpuEAExecutionContext,
				gpuEAConfiguration,
				executorService);
		return EASystemFactory.from(gpuEAConfiguration, gpuEAExecutionContext, executorService, gpuFitnessEvaluator);
	}

	public static <T extends Comparable<T>> EASystem<T> from(final GPUEAConfiguration<T> gpuEAConfiguration,
			final GPUEAExecutionContext<T> gpuEAExecutionContext) {
		final ExecutorService executorService = ForkJoinPool.commonPool();

		return from(gpuEAConfiguration, gpuEAExecutionContext, executorService);
	}
}