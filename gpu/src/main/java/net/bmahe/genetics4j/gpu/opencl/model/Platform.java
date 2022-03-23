package net.bmahe.genetics4j.gpu.opencl.model;

import java.util.Set;

import org.immutables.value.Value;
import org.jocl.cl_platform_id;

@Value.Immutable
public interface Platform {

	cl_platform_id platformId();

	PlatformProfile profile();

	String version();

	String name();

	String vendor();

	Set<String> extensions();

	int numDevices();
	
	static ImmutablePlatform.Builder builder() {
		return ImmutablePlatform.builder();
	}
}