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

	int maxClockFrequency();

	Set<DeviceType> deviceType();

	Set<String> builtInKernels();

	int maxComputeUnits();

	int maxWorkItemDimensions();

	long maxWorkGroupSize();

	long[] maxWorkItemSizes();

	boolean imageSupport();

	int preferredVectorWidthFloat();

	static ImmutableDevice.Builder builder() {
		return ImmutableDevice.builder();
	}
}