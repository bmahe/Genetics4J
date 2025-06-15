package net.bmahe.genetics4j.gpu.opencl;

import org.apache.commons.lang3.Validate;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;

/**
 * Utility class providing convenient methods for querying OpenCL kernel work group information.
 * 
 * <p>KernelInfoUtils encapsulates the low-level OpenCL API calls required for retrieving kernel-specific
 * execution characteristics on target devices. This information is essential for optimizing kernel
 * launch parameters and ensuring efficient resource utilization in GPU-accelerated evolutionary algorithms.
 * 
 * <p>Key functionality includes:
 * <ul>
 * <li><strong>Work group queries</strong>: Retrieve kernel-specific work group size limits and preferences</li>
 * <li><strong>Memory usage queries</strong>: Query local and private memory requirements per work-item</li>
 * <li><strong>Performance optimization</strong>: Access preferred work group size multiples for optimal execution</li>
 * <li><strong>Resource validation</strong>: Obtain kernel resource requirements for launch parameter validation</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Query kernel work group characteristics
 * long maxWorkGroupSize = KernelInfoUtils.getKernelWorkGroupInfoLong(
 *     deviceId, kernel, CL.CL_KERNEL_WORK_GROUP_SIZE);
 * 
 * long preferredMultiple = KernelInfoUtils.getKernelWorkGroupInfoLong(
 *     deviceId, kernel, CL.CL_KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE);
 * 
 * // Query memory requirements
 * long localMemSize = KernelInfoUtils.getKernelWorkGroupInfoLong(
 *     deviceId, kernel, CL.CL_KERNEL_LOCAL_MEM_SIZE);
 * 
 * long privateMemSize = KernelInfoUtils.getKernelWorkGroupInfoLong(
 *     deviceId, kernel, CL.CL_KERNEL_PRIVATE_MEM_SIZE);
 * 
 * // Optimize work group size based on kernel characteristics
 * long optimalWorkGroupSize = (maxWorkGroupSize / preferredMultiple) * preferredMultiple;
 * }</pre>
 * 
 * <p>Kernel optimization workflow:
 * <ol>
 * <li><strong>Kernel compilation</strong>: Compile kernel for target device</li>
 * <li><strong>Characteristic query</strong>: Retrieve kernel-specific execution parameters</li>
 * <li><strong>Launch optimization</strong>: Configure work group sizes based on kernel requirements</li>
 * <li><strong>Resource validation</strong>: Ensure memory requirements don't exceed device limits</li>
 * </ol>
 * 
 * <p>Error handling:
 * <ul>
 * <li><strong>Parameter validation</strong>: Validates all input parameters</li>
 * <li><strong>OpenCL error propagation</strong>: OpenCL errors are propagated as runtime exceptions</li>
 * <li><strong>Memory management</strong>: Automatically handles buffer allocation and cleanup</li>
 * </ul>
 * 
 * @see KernelInfo
 * @see KernelInfoReader
 * @see net.bmahe.genetics4j.gpu.opencl.model.Device
 */
public class KernelInfoUtils {

	private KernelInfoUtils() {

	}

	/**
	 * Queries and returns a long value for kernel work group information on the specified device.
	 * 
	 * <p>This method retrieves kernel-specific execution characteristics that vary by device,
	 * such as maximum work group size, preferred work group size multiples, and memory usage
	 * requirements. This information is essential for optimizing kernel launch parameters.
	 * 
	 * @param deviceId the OpenCL device to query
	 * @param kernel the compiled OpenCL kernel
	 * @param parameter the OpenCL parameter constant (e.g., CL_KERNEL_WORK_GROUP_SIZE, CL_KERNEL_LOCAL_MEM_SIZE)
	 * @return the long value of the requested kernel work group property
	 * @throws IllegalArgumentException if deviceId or kernel is null
	 */
	public static long getKernelWorkGroupInfoLong(final cl_device_id deviceId, final cl_kernel kernel,
			final int parameter) {
		Validate.notNull(deviceId);
		Validate.notNull(kernel);

		final long[] values = new long[1];
		CL.clGetKernelWorkGroupInfo(kernel, deviceId, parameter, Sizeof.cl_long, Pointer.to(values), null);

		return values[0];
	}
}