package net.bmahe.genetics4j.gpu.opencl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

public class DeviceUtils {

	private DeviceUtils() {

	}

	public static int numDevices(final cl_platform_id platformId, final long deviceType) {
		Validate.notNull(platformId);

		final int numDevices[] = new int[1];
		CL.clGetDeviceIDs(platformId, deviceType, 0, null, numDevices);

		return numDevices[0];
	}

	public static int numDevices(final cl_platform_id platformId) {
		return numDevices(platformId, CL.CL_DEVICE_TYPE_ALL);
	}

	public static List<cl_device_id> getDeviceIds(final cl_platform_id platformId, final int numDevices,
			final long deviceType) {
		Validate.notNull(platformId);
		Validate.isTrue(numDevices > 0);

		cl_device_id deviceIds[] = new cl_device_id[numDevices];
		CL.clGetDeviceIDs(platformId, deviceType, numDevices, deviceIds, null);

		return Arrays.asList(deviceIds);
	}

	public static List<cl_device_id> getDeviceIds(final cl_platform_id platformId, final int numDevices) {
		return getDeviceIds(platformId, numDevices, CL.CL_DEVICE_TYPE_ALL);
	}

	public static String getDeviceInfoString(final cl_device_id deviceId, final int parameter) {
		Validate.notNull(deviceId);

		final long[] size = new long[1];
		CL.clGetDeviceInfo(deviceId, parameter, 0, null, size);

		final byte[] buffer = new byte[(int) size[0]];
		CL.clGetDeviceInfo(deviceId, parameter, buffer.length, Pointer.to(buffer), null);

		return new String(buffer, 0, buffer.length - 1);
	}

	public static long[] getDeviceInfoLongArray(final cl_device_id deviceId, final int parameter, final int size) {
		Validate.notNull(deviceId);
		Validate.isTrue(size > 0);

		final long[] values = new long[size];
		CL.clGetDeviceInfo(deviceId, parameter, Sizeof.cl_long * size, Pointer.to(values), null);

		return values;
	}

	public static long getDeviceInfoLong(final cl_device_id deviceId, final int parameter) {
		Validate.notNull(deviceId);

		final long[] values = getDeviceInfoLongArray(deviceId, parameter, 1);

		return values[0];
	}

	public static int getDeviceInfoInt(final cl_device_id deviceId, final int parameter) {
		Validate.notNull(deviceId);

		final int[] values = new int[1];
		CL.clGetDeviceInfo(deviceId, parameter, Sizeof.cl_int, Pointer.to(values), null);

		return values[0];
	}
}