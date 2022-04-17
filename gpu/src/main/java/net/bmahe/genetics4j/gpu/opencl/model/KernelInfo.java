package net.bmahe.genetics4j.gpu.opencl.model;

import org.immutables.value.Value;

@Value.Immutable
public interface KernelInfo {

	String name();

	long workGroupSize();

	long preferredWorkGroupSizeMultiple();

	long localMemSize();

	long privateMemSize();

	static ImmutableKernelInfo.Builder builder() {
		return ImmutableKernelInfo.builder();
	}
}