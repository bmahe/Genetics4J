package net.bmahe.genetics4j.gpu.spec;

import java.util.function.Predicate;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.gpu.opencl.model.Device;
import net.bmahe.genetics4j.gpu.opencl.model.Platform;

@Value.Immutable
public abstract class GPUEAExecutionContext<T extends Comparable<T>> extends AbstractEAExecutionContext<T> {

	@Value.Default
	public Predicate<Platform> platformFilters() {
		return (platform) -> true;
	}

	@Value.Default
	public Predicate<Device> deviceFilters() {
		return (device) -> true;
	}

	public static <U extends Comparable<U>> ImmutableGPUEAExecutionContext.Builder<U> builder() {
		return ImmutableGPUEAExecutionContext.builder();
	}
}