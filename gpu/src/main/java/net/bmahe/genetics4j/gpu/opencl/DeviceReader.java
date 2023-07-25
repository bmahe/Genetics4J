package net.bmahe.genetics4j.gpu.opencl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jocl.CL;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

import net.bmahe.genetics4j.gpu.opencl.model.Device;
import net.bmahe.genetics4j.gpu.opencl.model.DeviceType;

public class DeviceReader {
	public static final Logger logger = LogManager.getLogger(DeviceReader.class);

	public Device read(final cl_platform_id platformId, final cl_device_id deviceId) {
		Validate.notNull(platformId);
		Validate.notNull(deviceId);

		final var deviceBuilder = Device.builder();
		deviceBuilder.deviceId(deviceId);

		final String name = DeviceUtils.getDeviceInfoString(deviceId, CL.CL_DEVICE_NAME);
		deviceBuilder.name(name);

		final String vendor = DeviceUtils.getDeviceInfoString(deviceId, CL.CL_DEVICE_VENDOR);
		deviceBuilder.vendor(vendor);

		final String deviceVersion = DeviceUtils.getDeviceInfoString(deviceId, CL.CL_DEVICE_VERSION);
		deviceBuilder.deviceVersion(deviceVersion);

		final String driverVersion = DeviceUtils.getDeviceInfoString(deviceId, CL.CL_DRIVER_VERSION);
		deviceBuilder.driverVersion(driverVersion);

		final String builtInKernelsStr = DeviceUtils.getDeviceInfoString(deviceId, CL.CL_DEVICE_BUILT_IN_KERNELS);
		if (StringUtils.isNotBlank(builtInKernelsStr)) {
			final String[] builtInKernelsArr = builtInKernelsStr.split(";");
			if (builtInKernelsArr.length > 0) {
				deviceBuilder.addBuiltInKernels(builtInKernelsArr);
			}
		}

		final long deviceTypeLong = DeviceUtils.getDeviceInfoLong(deviceId, CL.CL_DEVICE_TYPE);
		if (deviceTypeLong == CL.CL_DEVICE_TYPE_ALL) {
			deviceBuilder.addDeviceType(DeviceType.ALL);
		} else {

			if ((deviceTypeLong & CL.CL_DEVICE_TYPE_DEFAULT) != 0) {
				deviceBuilder.addDeviceType(DeviceType.DEFAULT);
			}
			if ((deviceTypeLong & CL.CL_DEVICE_TYPE_CPU) != 0) {
				deviceBuilder.addDeviceType(DeviceType.CPU);
			}
			if ((deviceTypeLong & CL.CL_DEVICE_TYPE_GPU) != 0) {
				deviceBuilder.addDeviceType(DeviceType.GPU);
			}
			if ((deviceTypeLong & CL.CL_DEVICE_TYPE_ACCELERATOR) != 0) {
				deviceBuilder.addDeviceType(DeviceType.ACCELERATOR);
			}
			if ((deviceTypeLong & CL.CL_DEVICE_TYPE_CUSTOM) != 0) {
				deviceBuilder.addDeviceType(DeviceType.CUSTOM);
			}
		}

		final int maxComputeUnits = DeviceUtils.getDeviceInfoInt(deviceId, CL.CL_DEVICE_MAX_COMPUTE_UNITS);
		deviceBuilder.maxComputeUnits(maxComputeUnits);

		final int maxWorkItemDimensions = DeviceUtils.getDeviceInfoInt(deviceId, CL.CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
		deviceBuilder.maxWorkItemDimensions(maxWorkItemDimensions);

		final int maxClockFrequency = DeviceUtils.getDeviceInfoInt(deviceId, CL.CL_DEVICE_MAX_CLOCK_FREQUENCY);
		deviceBuilder.maxClockFrequency(maxClockFrequency);

		final int hasImageSupport = DeviceUtils.getDeviceInfoInt(deviceId, CL.CL_DEVICE_IMAGE_SUPPORT);
		deviceBuilder.imageSupport(hasImageSupport != 0);

		final long maxWorkGroupSize = DeviceUtils.getDeviceInfoLong(deviceId, CL.CL_DEVICE_MAX_WORK_GROUP_SIZE);
		deviceBuilder.maxWorkGroupSize(maxWorkGroupSize);

		final int preferredVectorWidthFloat = DeviceUtils.getDeviceInfoInt(deviceId,
				CL.CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT);
		deviceBuilder.preferredVectorWidthFloat(preferredVectorWidthFloat);

		final long[] maxWorkItemSizes = DeviceUtils
				.getDeviceInfoLongArray(deviceId, CL.CL_DEVICE_MAX_WORK_ITEM_SIZES, maxWorkItemDimensions);
		deviceBuilder.maxWorkItemSizes(maxWorkItemSizes);

		return deviceBuilder.build();
	}
}