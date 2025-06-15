package net.bmahe.genetics4j.gpu.opencl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

/**
 * Utility class providing convenient methods for OpenCL device discovery and information queries.
 * 
 * <p>DeviceUtils encapsulates the low-level OpenCL API calls required for device enumeration and
 * property retrieval, providing a higher-level interface for GPU-accelerated evolutionary algorithm
 * implementations. This class handles the OpenCL buffer management and type conversions necessary
 * for interacting with the native OpenCL runtime.
 * 
 * <p>Key functionality includes:
 * <ul>
 * <li><strong>Device enumeration</strong>: Discover available devices on OpenCL platforms</li>
 * <li><strong>Property queries</strong>: Retrieve device characteristics and capabilities</li>
 * <li><strong>Type conversions</strong>: Convert between OpenCL native types and Java types</li>
 * <li><strong>Buffer management</strong>: Handle memory allocation for OpenCL information queries</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Enumerate devices on a platform
 * int deviceCount = DeviceUtils.numDevices(platformId);
 * List<cl_device_id> deviceIds = DeviceUtils.getDeviceIds(platformId, deviceCount);
 * 
 * // Query device properties
 * String deviceName = DeviceUtils.getDeviceInfoString(deviceId, CL.CL_DEVICE_NAME);
 * int computeUnits = DeviceUtils.getDeviceInfoInt(deviceId, CL.CL_DEVICE_MAX_COMPUTE_UNITS);
 * long maxWorkGroupSize = DeviceUtils.getDeviceInfoLong(deviceId, CL.CL_DEVICE_MAX_WORK_GROUP_SIZE);
 * 
 * // Query array properties
 * int maxDimensions = DeviceUtils.getDeviceInfoInt(deviceId, CL.CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
 * long[] maxWorkItemSizes = DeviceUtils.getDeviceInfoLongArray(deviceId, 
 *     CL.CL_DEVICE_MAX_WORK_ITEM_SIZES, maxDimensions);
 * }</pre>
 * 
 * <p>Device type filtering:
 * <ul>
 * <li><strong>CL_DEVICE_TYPE_ALL</strong>: All available devices</li>
 * <li><strong>CL_DEVICE_TYPE_GPU</strong>: GPU devices only</li>
 * <li><strong>CL_DEVICE_TYPE_CPU</strong>: CPU devices only</li>
 * <li><strong>CL_DEVICE_TYPE_ACCELERATOR</strong>: Accelerator devices only</li>
 * </ul>
 * 
 * <p>Error handling:
 * <ul>
 * <li><strong>Parameter validation</strong>: Validates all input parameters</li>
 * <li><strong>OpenCL error propagation</strong>: OpenCL errors are propagated as runtime exceptions</li>
 * <li><strong>Memory management</strong>: Automatically handles buffer allocation and cleanup</li>
 * </ul>
 * 
 * @see Device
 * @see DeviceReader
 * @see net.bmahe.genetics4j.gpu.opencl.model.DeviceType
 */
public class DeviceUtils {

	private DeviceUtils() {

	}

	/**
	 * Returns the number of OpenCL devices of the specified type available on the platform.
	 * 
	 * @param platformId the OpenCL platform to query
	 * @param deviceType the type of devices to count (e.g., CL_DEVICE_TYPE_GPU, CL_DEVICE_TYPE_ALL)
	 * @return the number of available devices of the specified type
	 * @throws IllegalArgumentException if platformId is null
	 */
	public static int numDevices(final cl_platform_id platformId, final long deviceType) {
		Validate.notNull(platformId);

		final int numDevices[] = new int[1];
		CL.clGetDeviceIDs(platformId, deviceType, 0, null, numDevices);

		return numDevices[0];
	}

	/**
	 * Returns the total number of OpenCL devices available on the platform.
	 * 
	 * <p>This is equivalent to calling {@link #numDevices(cl_platform_id, long)} with
	 * {@code CL_DEVICE_TYPE_ALL} as the device type.
	 * 
	 * @param platformId the OpenCL platform to query
	 * @return the total number of available devices on the platform
	 * @throws IllegalArgumentException if platformId is null
	 */
	public static int numDevices(final cl_platform_id platformId) {
		return numDevices(platformId, CL.CL_DEVICE_TYPE_ALL);
	}

	/**
	 * Returns a list of OpenCL device identifiers of the specified type from the platform.
	 * 
	 * @param platformId the OpenCL platform to query
	 * @param numDevices the number of devices to retrieve
	 * @param deviceType the type of devices to retrieve (e.g., CL_DEVICE_TYPE_GPU, CL_DEVICE_TYPE_ALL)
	 * @return list of OpenCL device identifiers
	 * @throws IllegalArgumentException if platformId is null or numDevices is not positive
	 */
	public static List<cl_device_id> getDeviceIds(final cl_platform_id platformId, final int numDevices,
			final long deviceType) {
		Validate.notNull(platformId);
		Validate.isTrue(numDevices > 0);

		cl_device_id deviceIds[] = new cl_device_id[numDevices];
		CL.clGetDeviceIDs(platformId, deviceType, numDevices, deviceIds, null);

		return Arrays.asList(deviceIds);
	}

	/**
	 * Returns a list of all OpenCL device identifiers from the platform.
	 * 
	 * <p>This is equivalent to calling {@link #getDeviceIds(cl_platform_id, int, long)} with
	 * {@code CL_DEVICE_TYPE_ALL} as the device type.
	 * 
	 * @param platformId the OpenCL platform to query
	 * @param numDevices the number of devices to retrieve
	 * @return list of all OpenCL device identifiers
	 * @throws IllegalArgumentException if platformId is null or numDevices is not positive
	 */
	public static List<cl_device_id> getDeviceIds(final cl_platform_id platformId, final int numDevices) {
		return getDeviceIds(platformId, numDevices, CL.CL_DEVICE_TYPE_ALL);
	}

	/**
	 * Queries and returns a string property of the specified OpenCL device.
	 * 
	 * <p>This method handles the OpenCL API calls and buffer management required to retrieve
	 * string properties from devices, such as device name, vendor, or version information.
	 * 
	 * @param deviceId the OpenCL device to query
	 * @param parameter the OpenCL parameter constant (e.g., CL_DEVICE_NAME, CL_DEVICE_VENDOR)
	 * @return the string value of the requested device property
	 * @throws IllegalArgumentException if deviceId is null
	 */
	public static String getDeviceInfoString(final cl_device_id deviceId, final int parameter) {
		Validate.notNull(deviceId);

		final long[] size = new long[1];
		CL.clGetDeviceInfo(deviceId, parameter, 0, null, size);

		final byte[] buffer = new byte[(int) size[0]];
		CL.clGetDeviceInfo(deviceId, parameter, buffer.length, Pointer.to(buffer), null);

		return new String(buffer, 0, buffer.length - 1);
	}

	/**
	 * Queries and returns a long array property of the specified OpenCL device.
	 * 
	 * <p>This method is useful for retrieving array properties such as maximum work-item sizes
	 * per dimension, which require multiple values to fully describe the device capability.
	 * 
	 * @param deviceId the OpenCL device to query
	 * @param parameter the OpenCL parameter constant (e.g., CL_DEVICE_MAX_WORK_ITEM_SIZES)
	 * @param size the number of long values to retrieve
	 * @return array of long values for the requested device property
	 * @throws IllegalArgumentException if deviceId is null or size is not positive
	 */
	public static long[] getDeviceInfoLongArray(final cl_device_id deviceId, final int parameter, final int size) {
		Validate.notNull(deviceId);
		Validate.isTrue(size > 0);

		final long[] values = new long[size];
		CL.clGetDeviceInfo(deviceId, parameter, Sizeof.cl_long * size, Pointer.to(values), null);

		return values;
	}

	/**
	 * Queries and returns a single long property of the specified OpenCL device.
	 * 
	 * <p>This method is useful for retrieving single long value properties such as maximum
	 * work group size, global memory size, or local memory size.
	 * 
	 * @param deviceId the OpenCL device to query
	 * @param parameter the OpenCL parameter constant (e.g., CL_DEVICE_MAX_WORK_GROUP_SIZE)
	 * @return the long value of the requested device property
	 * @throws IllegalArgumentException if deviceId is null
	 */
	public static long getDeviceInfoLong(final cl_device_id deviceId, final int parameter) {
		Validate.notNull(deviceId);

		final long[] values = getDeviceInfoLongArray(deviceId, parameter, 1);

		return values[0];
	}

	/**
	 * Queries and returns a single integer property of the specified OpenCL device.
	 * 
	 * <p>This method is useful for retrieving integer properties such as maximum compute units,
	 * maximum clock frequency, or maximum work-item dimensions.
	 * 
	 * @param deviceId the OpenCL device to query
	 * @param parameter the OpenCL parameter constant (e.g., CL_DEVICE_MAX_COMPUTE_UNITS)
	 * @return the integer value of the requested device property
	 * @throws IllegalArgumentException if deviceId is null
	 */
	public static int getDeviceInfoInt(final cl_device_id deviceId, final int parameter) {
		Validate.notNull(deviceId);

		final int[] values = new int[1];
		CL.clGetDeviceInfo(deviceId, parameter, Sizeof.cl_int, Pointer.to(values), null);

		return values[0];
	}
}