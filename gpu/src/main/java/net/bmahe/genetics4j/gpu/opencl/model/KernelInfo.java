package net.bmahe.genetics4j.gpu.opencl.model;

import org.immutables.value.Value;

/**
 * Represents kernel-specific execution characteristics and resource requirements for an OpenCL kernel on a specific device.
 * 
 * <p>KernelInfo encapsulates the device-specific compilation and execution characteristics of an OpenCL kernel,
 * providing essential information for optimal work group configuration and resource allocation in GPU-accelerated
 * evolutionary algorithms. This information is determined at kernel compilation time and varies by device.
 * 
 * <p>Key kernel characteristics include:
 * <ul>
 * <li><strong>Work group constraints</strong>: Maximum and preferred work group sizes for efficient execution</li>
 * <li><strong>Memory usage</strong>: Local and private memory requirements per work-item</li>
 * <li><strong>Performance optimization</strong>: Preferred work group size multiples for optimal resource utilization</li>
 * <li><strong>Resource validation</strong>: Constraints for validating kernel launch parameters</li>
 * </ul>
 * 
 * <p>Kernel optimization considerations for evolutionary algorithms:
 * <ul>
 * <li><strong>Work group sizing</strong>: Configure launch parameters within device-specific limits</li>
 * <li><strong>Memory allocation</strong>: Ensure sufficient local memory for parallel fitness evaluation</li>
 * <li><strong>Performance tuning</strong>: Align work group sizes with preferred multiples</li>
 * <li><strong>Resource planning</strong>: Account for per-work-item memory requirements</li>
 * </ul>
 * 
 * <p>Common usage patterns for kernel configuration:
 * <pre>{@code
 * // Query kernel information after compilation
 * KernelInfo kernelInfo = kernelInfoReader.read(deviceId, kernel, "fitness_evaluation");
 * 
 * // Configure work group size within device limits
 * long maxWorkGroupSize = Math.min(kernelInfo.workGroupSize(), device.maxWorkGroupSize());
 * 
 * // Optimize for preferred work group size multiple
 * long preferredMultiple = kernelInfo.preferredWorkGroupSizeMultiple();
 * long optimalWorkGroupSize = (maxWorkGroupSize / preferredMultiple) * preferredMultiple;
 * 
 * // Validate memory requirements for population size
 * long populationSize = 1000;
 * long totalLocalMem = kernelInfo.localMemSize() * optimalWorkGroupSize;
 * long totalPrivateMem = kernelInfo.privateMemSize() * populationSize;
 * 
 * // Configure kernel execution with validated parameters
 * clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, 
 *     new long[]{populationSize}, new long[]{optimalWorkGroupSize}, 0, null, null);
 * }</pre>
 * 
 * <p>Performance optimization workflow:
 * <ol>
 * <li><strong>Kernel compilation</strong>: Compile kernel for target device</li>
 * <li><strong>Information query</strong>: Read kernel-specific execution characteristics</li>
 * <li><strong>Work group optimization</strong>: Calculate optimal work group size based on preferences</li>
 * <li><strong>Memory validation</strong>: Ensure memory requirements fit within device limits</li>
 * <li><strong>Launch configuration</strong>: Configure kernel execution with optimized parameters</li>
 * </ol>
 * 
 * <p>Memory management considerations:
 * <ul>
 * <li><strong>Local memory</strong>: Shared among work-items in the same work group</li>
 * <li><strong>Private memory</strong>: Individual memory per work-item</li>
 * <li><strong>Total allocation</strong>: Sum of all work-items' memory requirements</li>
 * <li><strong>Device limits</strong>: Validate against device memory constraints</li>
 * </ul>
 * 
 * <p>Error handling and validation:
 * <ul>
 * <li><strong>Work group limits</strong>: Ensure launch parameters don't exceed kernel limits</li>
 * <li><strong>Memory constraints</strong>: Validate total memory usage against device capabilities</li>
 * <li><strong>Performance degradation</strong>: Monitor for suboptimal work group configurations</li>
 * <li><strong>Resource conflicts</strong>: Handle multiple kernels competing for device resources</li>
 * </ul>
 * 
 * @see Device
 * @see net.bmahe.genetics4j.gpu.opencl.KernelInfoReader
 * @see net.bmahe.genetics4j.gpu.opencl.KernelInfoUtils
 */
@Value.Immutable
public interface KernelInfo {

	/**
	 * Returns the name of the kernel function.
	 * 
	 * @return the kernel function name as specified in the OpenCL program
	 */
	String name();

	/**
	 * Returns the maximum work group size that can be used when executing this kernel on the device.
	 * 
	 * <p>This value represents the maximum number of work-items that can be in a work group when
	 * executing this specific kernel on the target device. It may be smaller than the device's
	 * general maximum work group size due to kernel-specific resource requirements.
	 * 
	 * @return the maximum work group size for this kernel
	 */
	long workGroupSize();

	/**
	 * Returns the preferred work group size multiple for optimal kernel execution performance.
	 * 
	 * <p>For optimal performance, the work group size should be a multiple of this value.
	 * This represents the native vector width or wavefront size of the device and helps
	 * achieve better resource utilization and memory coalescing.
	 * 
	 * @return the preferred work group size multiple for performance optimization
	 */
	long preferredWorkGroupSizeMultiple();

	/**
	 * Returns the amount of local memory in bytes used by this kernel.
	 * 
	 * <p>Local memory is shared among all work-items in a work group and includes both
	 * statically allocated local variables and dynamically allocated local memory passed
	 * as kernel arguments. This value is used to validate that the total local memory
	 * usage doesn't exceed the device's local memory capacity.
	 * 
	 * @return the local memory usage in bytes per work group
	 */
	long localMemSize();

	/**
	 * Returns the minimum amount of private memory in bytes used by each work-item.
	 * 
	 * <p>Private memory is individual to each work-item and includes local variables,
	 * function call stacks, and other per-work-item data. This value helps estimate
	 * the total memory footprint when launching kernels with large work group sizes.
	 * 
	 * @return the private memory usage in bytes per work-item
	 */
	long privateMemSize();

	/**
	 * Creates a new builder for constructing KernelInfo instances.
	 * 
	 * @return a new builder for creating kernel information objects
	 */
	static ImmutableKernelInfo.Builder builder() {
		return ImmutableKernelInfo.builder();
	}
}