package net.bmahe.genetics4j.gpu.opencl.model;

import java.util.Set;

import org.immutables.value.Value;
import org.jocl.cl_device_id;

/**
 * Represents an OpenCL compute device with its capabilities and characteristics for GPU-accelerated evolutionary algorithms.
 * 
 * <p>Device encapsulates the properties and capabilities of an OpenCL compute device (GPU, CPU, or accelerator)
 * that can be used for fitness evaluation in evolutionary algorithms. This information is essential for
 * device selection, kernel optimization, and workload configuration to achieve optimal performance.
 * 
 * <p>Key device characteristics include:
 * <ul>
 * <li><strong>Device identification</strong>: Name, vendor, and version information</li>
 * <li><strong>Compute capabilities</strong>: Number of compute units and maximum work group sizes</li>
 * <li><strong>Memory hierarchy</strong>: Global, local, and constant memory sizes and characteristics</li>
 * <li><strong>Processing features</strong>: Vector width preferences, image support, and built-in kernels</li>
 * <li><strong>Performance metrics</strong>: Clock frequency and execution capabilities</li>
 * </ul>
 * 
 * <p>Device selection considerations for evolutionary algorithms:
 * <ul>
 * <li><strong>Device type</strong>: GPU devices typically provide highest parallelism for large populations</li>
 * <li><strong>Compute units</strong>: More compute units allow better utilization of large populations</li>
 * <li><strong>Work group sizes</strong>: Must accommodate the parallelism patterns of fitness kernels</li>
 * <li><strong>Memory capacity</strong>: Must be sufficient for population data and intermediate results</li>
 * <li><strong>Vector operations</strong>: Vector width preferences can optimize numerical computations</li>
 * </ul>
 * 
 * <p>Common device filtering patterns:
 * <pre>{@code
 * // Select GPU devices with sufficient parallel processing capability
 * Predicate<Device> gpuFilter = device -> 
 *     device.deviceType().contains(DeviceType.GPU) &&
 *     device.maxComputeUnits() >= 8;
 * 
 * // Select devices with large work group support for population processing
 * Predicate<Device> workGroupFilter = device ->
 *     device.maxWorkGroupSize() >= 256;
 * 
 * // Select devices with high clock frequency for compute-intensive fitness
 * Predicate<Device> performanceFilter = device ->
 *     device.maxClockFrequency() >= 1000; // MHz
 * 
 * // Select devices that support floating-point vector operations
 * Predicate<Device> vectorFilter = device ->
 *     device.preferredVectorWidthFloat() >= 4;
 * 
 * // Comprehensive filter for evolutionary algorithm suitability
 * Predicate<Device> eaOptimizedFilter = device ->
 *     device.deviceType().contains(DeviceType.GPU) &&
 *     device.maxComputeUnits() >= 4 &&
 *     device.maxWorkGroupSize() >= 128 &&
 *     device.preferredVectorWidthFloat() >= 2;
 * }</pre>
 * 
 * <p>Performance optimization using device information:
 * <ul>
 * <li><strong>Work group sizing</strong>: Configure kernel work groups based on {@link #maxWorkGroupSize()}</li>
 * <li><strong>Parallel dispatch</strong>: Scale parallelism based on {@link #maxComputeUnits()}</li>
 * <li><strong>Vector operations</strong>: Optimize data layouts for {@link #preferredVectorWidthFloat()}</li>
 * <li><strong>Memory access patterns</strong>: Design kernels considering memory hierarchy characteristics</li>
 * </ul>
 * 
 * <p>Device capability assessment workflow:
 * <ol>
 * <li><strong>Device discovery</strong>: Enumerate devices from selected platforms</li>
 * <li><strong>Capability query</strong>: Read device properties from OpenCL runtime</li>
 * <li><strong>Model creation</strong>: Create device objects with discovered capabilities</li>
 * <li><strong>Filtering</strong>: Apply user-defined predicates to select suitable devices</li>
 * <li><strong>Context creation</strong>: Create OpenCL contexts for selected devices</li>
 * </ol>
 * 
 * <p>Common device types in evolutionary computation:
 * <ul>
 * <li><strong>GPU devices</strong>: Provide massive parallelism for large population fitness evaluation</li>
 * <li><strong>CPU devices</strong>: Offer good sequential performance and large memory capacity</li>
 * <li><strong>Accelerator devices</strong>: Specialized hardware for specific computational patterns</li>
 * <li><strong>Custom devices</strong>: FPGA or other specialized compute devices</li>
 * </ul>
 * 
 * <p>Error handling and compatibility:
 * <ul>
 * <li><strong>Device availability</strong>: Devices may become unavailable during execution</li>
 * <li><strong>Capability validation</strong>: Ensure device supports required kernel features</li>
 * <li><strong>Memory constraints</strong>: Validate device memory is sufficient for population size</li>
 * <li><strong>Work group limits</strong>: Ensure kernels respect device work group size limits</li>
 * </ul>
 * 
 * @see Platform
 * @see DeviceType
 * @see net.bmahe.genetics4j.gpu.spec.GPUEAExecutionContext#deviceFilters()
 * @see net.bmahe.genetics4j.gpu.opencl.DeviceUtils
 */
@Value.Immutable
public interface Device {

	/**
	 * Returns the native OpenCL device identifier.
	 * 
	 * @return the OpenCL device ID for low-level operations
	 */
	cl_device_id deviceId();

	/**
	 * Returns the device name provided by the vendor.
	 * 
	 * @return the human-readable device name (e.g., "GeForce RTX 3080", "Intel Core i7")
	 */
	String name();

	/**
	 * Returns the device vendor name.
	 * 
	 * @return the vendor name (e.g., "NVIDIA Corporation", "Intel", "AMD")
	 */
	String vendor();

	/**
	 * Returns the OpenCL version supported by this device.
	 * 
	 * @return the device OpenCL version string (e.g., "OpenCL 2.1")
	 */
	String deviceVersion();

	/**
	 * Returns the device driver version.
	 * 
	 * @return the driver version string provided by the vendor
	 */
	String driverVersion();

	/**
	 * Returns the maximum configured clock frequency of the device compute units in MHz.
	 * 
	 * @return the maximum clock frequency in megahertz
	 */
	int maxClockFrequency();

	/**
	 * Returns the set of device types that classify this device.
	 * 
	 * @return set of device types (e.g., GPU, CPU, ACCELERATOR)
	 */
	Set<DeviceType> deviceType();

	/**
	 * Returns the set of built-in kernel names available on this device.
	 * 
	 * @return set of built-in kernel names provided by the device
	 */
	Set<String> builtInKernels();

	/**
	 * Returns the number of parallel compute units on the device.
	 * 
	 * <p>Compute units represent the primary parallel processing elements and directly
	 * impact the device's ability to execute work groups concurrently.
	 * 
	 * @return the number of parallel compute units available
	 */
	int maxComputeUnits();

	/**
	 * Returns the maximum number of work-item dimensions supported by the device.
	 * 
	 * @return the maximum number of dimensions for work-item indexing
	 */
	int maxWorkItemDimensions();

	/**
	 * Returns the maximum number of work-items in a work group for kernel execution.
	 * 
	 * <p>This limit constrains the local work group size that can be used when
	 * launching kernels on this device. Larger work groups can improve memory
	 * locality and reduce synchronization overhead.
	 * 
	 * @return the maximum work group size for kernel execution
	 */
	long maxWorkGroupSize();

	/**
	 * Returns the maximum number of work-items in each dimension of a work group.
	 * 
	 * <p>The array contains the maximum work-item count for each dimension,
	 * providing more granular control over work group configuration than
	 * the overall {@link #maxWorkGroupSize()} limit.
	 * 
	 * @return array of maximum work-item counts per dimension
	 */
	long[] maxWorkItemSizes();

	/**
	 * Returns whether the device supports image objects in kernels.
	 * 
	 * @return true if the device supports image processing operations
	 */
	boolean imageSupport();

	/**
	 * Returns the preferred vector width for float operations.
	 * 
	 * <p>This indicates the optimal vector width for floating-point operations
	 * on this device, which can be used to optimize numerical computations
	 * in fitness evaluation kernels.
	 * 
	 * @return the preferred vector width for float operations
	 */
	int preferredVectorWidthFloat();

	/**
	 * Creates a new builder for constructing Device instances.
	 * 
	 * @return a new builder for creating device objects
	 */
	static ImmutableDevice.Builder builder() {
		return ImmutableDevice.builder();
	}
}