package net.bmahe.genetics4j.gpu.opencl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.cl_platform_id;

/**
 * Utility class providing convenient methods for OpenCL platform discovery and information queries.
 * 
 * <p>PlatformUtils encapsulates the low-level OpenCL API calls required for platform enumeration and
 * property retrieval, providing a higher-level interface for GPU-accelerated evolutionary algorithm
 * implementations. This class handles the OpenCL buffer management and type conversions necessary
 * for interacting with the native OpenCL runtime.
 * 
 * <p>Key functionality includes:
 * <ul>
 * <li><strong>Platform enumeration</strong>: Discover available OpenCL platforms in the system</li>
 * <li><strong>Property queries</strong>: Retrieve platform characteristics and capabilities</li>
 * <li><strong>Device counting</strong>: Count available devices on each platform</li>
 * <li><strong>Buffer management</strong>: Handle memory allocation for OpenCL information queries</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Enumerate platforms in the system
 * int platformCount = PlatformUtils.numPlatforms();
 * List<cl_platform_id> platformIds = PlatformUtils.platformIds(platformCount);
 * 
 * // Query platform properties
 * for (cl_platform_id platformId : platformIds) {
 *     String platformName = PlatformUtils.getStringParameter(platformId, CL.CL_PLATFORM_NAME);
 *     String vendor = PlatformUtils.getStringParameter(platformId, CL.CL_PLATFORM_VENDOR);
 *     String version = PlatformUtils.getStringParameter(platformId, CL.CL_PLATFORM_VERSION);
 *     
 *     // Count devices on this platform
 *     int deviceCount = PlatformUtils.numDevices(platformId);
 *     int gpuCount = PlatformUtils.numDevices(platformId, CL.CL_DEVICE_TYPE_GPU);
 * }
 * }</pre>
 * 
 * <p>Platform discovery workflow:
 * <ol>
 * <li><strong>System enumeration</strong>: Query the number of available platforms</li>
 * <li><strong>Platform retrieval</strong>: Get platform identifiers for all available platforms</li>
 * <li><strong>Property queries</strong>: Retrieve platform characteristics for filtering</li>
 * <li><strong>Device counting</strong>: Determine available devices for each platform</li>
 * </ol>
 * 
 * <p>Error handling:
 * <ul>
 * <li><strong>Parameter validation</strong>: Validates all input parameters</li>
 * <li><strong>OpenCL error propagation</strong>: OpenCL errors are propagated as runtime exceptions</li>
 * <li><strong>Memory management</strong>: Automatically handles buffer allocation and cleanup</li>
 * <li><strong>Empty platform handling</strong>: Gracefully handles systems with no OpenCL platforms</li>
 * </ul>
 * 
 * @see Platform
 * @see PlatformReader
 * @see net.bmahe.genetics4j.gpu.opencl.model.PlatformProfile
 */
public class PlatformUtils {

	private PlatformUtils() {

	}

	/**
	 * Returns the number of OpenCL platforms available in the system.
	 * 
	 * @return the number of available OpenCL platforms
	 */
	public static int numPlatforms() {
		final int[] numPlatforms = new int[1];
		CL.clGetPlatformIDs(0, null, numPlatforms);

		return numPlatforms[0];
	}

	/**
	 * Returns a list of OpenCL platform identifiers for all available platforms.
	 * 
	 * @param numPlatforms the number of platforms to retrieve
	 * @return list of OpenCL platform identifiers, or empty list if numPlatforms is 0
	 * @throws IllegalArgumentException if numPlatforms is negative
	 */
	public static List<cl_platform_id> platformIds(final int numPlatforms) {
		Validate.isTrue(numPlatforms >= 0);

		if (numPlatforms == 0) {
			return List.of();
		}

		final cl_platform_id[] platformIds = new cl_platform_id[numPlatforms];
		CL.clGetPlatformIDs(platformIds.length, platformIds, null);

		return Arrays.asList(platformIds);
	}

	/**
	 * Queries and returns a string property of the specified OpenCL platform.
	 * 
	 * <p>This method handles the OpenCL API calls and buffer management required to retrieve
	 * string properties from platforms, such as platform name, vendor, or version information.
	 * 
	 * @param platformId the OpenCL platform to query
	 * @param platformName the OpenCL parameter constant (e.g., CL_PLATFORM_NAME, CL_PLATFORM_VENDOR)
	 * @return the string value of the requested platform property
	 * @throws IllegalArgumentException if platformId is null
	 */
	public static String getStringParameter(final cl_platform_id platformId, final int platformName) {
		Validate.notNull(platformId);

		final long[] parameterSize = new long[1];
		CL.clGetPlatformInfo(platformId, platformName, 0, null, parameterSize);

		final byte[] buffer = new byte[(int) parameterSize[0]];
		CL.clGetPlatformInfo(platformId, platformName, buffer.length, Pointer.to(buffer), null);

		return new String(buffer, 0, buffer.length - 1);
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

		int[] numDevices = new int[1];
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
		Validate.notNull(platformId);
		
		return numDevices(platformId, CL.CL_DEVICE_TYPE_ALL);
	}
}