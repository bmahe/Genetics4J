package net.bmahe.genetics4j.gpu.opencl.model;

import java.util.Set;

import org.immutables.value.Value;
import org.jocl.cl_device_id;

@Value.Immutable
public interface Device {

	cl_device_id deviceId();

	String name();

	String vendor();

	String deviceVersion();

	String driverVersion();

	Set<DeviceType> deviceType();

	Set<String> builtInKernels();

	int maxComputeUnits();

	int maxWorkItemDimensions();

	int maxClockFrequency();

	boolean imageSupport();

	long maxWorkGroupSize();

	int preferredVectorWidthFloat();

	static ImmutableDevice.Builder builder() {
		return ImmutableDevice.builder();
	}
}