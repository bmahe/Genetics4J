package net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public interface KernelExecutionContext {

	long[] globalWorkSize();

	Optional<long[]> workGroupSize();

	@Value.Derived
	default int globalWorkDimensions() {
		return globalWorkSize().length;
	}

	static class Builder extends ImmutableKernelExecutionContext.Builder {
	}

	static Builder builder() {
		return new Builder();
	}
}