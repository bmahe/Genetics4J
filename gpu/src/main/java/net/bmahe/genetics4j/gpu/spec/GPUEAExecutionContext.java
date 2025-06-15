package net.bmahe.genetics4j.gpu.spec;

import java.util.function.Predicate;

import org.immutables.value.Value;

import net.bmahe.genetics4j.core.spec.AbstractEAExecutionContext;
import net.bmahe.genetics4j.gpu.opencl.model.Device;
import net.bmahe.genetics4j.gpu.opencl.model.Platform;

/**
 * GPU-specific execution context that extends the core EA framework with OpenCL device selection capabilities.
 * 
 * <p>GPUEAExecutionContext extends {@link AbstractEAExecutionContext} to include GPU-specific execution
 * parameters required for OpenCL device discovery and selection. This context combines traditional EA
 * execution settings (population size, termination criteria) with GPU-specific device filtering capabilities.
 * 
 * <p>Key GPU-specific additions:
 * <ul>
 * <li><strong>Platform filtering</strong>: Predicates for selecting appropriate OpenCL platforms</li>
 * <li><strong>Device filtering</strong>: Predicates for selecting compatible OpenCL devices</li>
 * <li><strong>Multi-device support</strong>: Automatic discovery and utilization of multiple GPU devices</li>
 * <li><strong>Hardware abstraction</strong>: Device-agnostic configuration with runtime device selection</li>
 * </ul>
 * 
 * <p>Device selection workflow:
 * <ol>
 * <li><strong>Platform discovery</strong>: Enumerate all available OpenCL platforms</li>
 * <li><strong>Platform filtering</strong>: Apply platform predicates to select compatible platforms</li>
 * <li><strong>Device enumeration</strong>: Discover devices for each selected platform</li>
 * <li><strong>Device filtering</strong>: Apply device predicates to select suitable devices</li>
 * <li><strong>Context creation</strong>: Create OpenCL contexts for selected devices</li>
 * </ol>
 * 
 * <p>Common filtering patterns:
 * <pre>{@code
 * // Select only GPU devices with sufficient memory
 * GPUEAExecutionContext<Double> context = GPUEAExecutionContext.<Double>builder()
 *     .populationSize(2000)
 *     .termination(Generations.of(100))
 *     
 *     // Platform filtering - prefer full OpenCL profiles
 *     .platformFilter(platform -> platform.profile() == PlatformProfile.FULL_PROFILE)
 *     
 *     // Device filtering - GPU devices with at least 2GB memory
 *     .deviceFilter(device -> 
 *         device.type() == DeviceType.GPU && 
 *         device.globalMemSize() >= 2L * 1024 * 1024 * 1024)
 *     
 *     .build();
 * 
 * // Select any available compute device (GPUs or CPUs)
 * GPUEAExecutionContext<Double> flexibleContext = GPUEAExecutionContext.<Double>builder()
 *     .populationSize(1000)
 *     .termination(FitnessTarget.of(0.95))
 *     
 *     // Accept any platform
 *     .platformFilter(platform -> true)
 *     
 *     // Prefer GPUs but accept CPUs as fallback
 *     .deviceFilter(device -> 
 *         device.type() == DeviceType.GPU || 
 *         device.type() == DeviceType.CPU)
 *     
 *     .build();
 * }</pre>
 * 
 * <p>Performance optimization through device selection:
 * <ul>
 * <li><strong>Compute capability</strong>: Filter devices by OpenCL version and feature support</li>
 * <li><strong>Memory capacity</strong>: Ensure devices have sufficient memory for population size</li>
 * <li><strong>Compute units</strong>: Prefer devices with more parallel processing units</li>
 * <li><strong>Memory bandwidth</strong>: Select devices optimized for data-intensive operations</li>
 * </ul>
 * 
 * <p>Multi-device strategies:
 * <ul>
 * <li><strong>Load balancing</strong>: Automatic population distribution across selected devices</li>
 * <li><strong>Heterogeneous computing</strong>: Utilize both GPU and CPU devices simultaneously</li>
 * <li><strong>Fault tolerance</strong>: Graceful degradation when devices become unavailable</li>
 * <li><strong>Resource optimization</strong>: Efficient utilization of available compute resources</li>
 * </ul>
 * 
 * <p>Default behavior:
 * <ul>
 * <li><strong>Platform acceptance</strong>: All platforms accepted by default</li>
 * <li><strong>Device acceptance</strong>: All devices accepted by default</li>
 * <li><strong>Discovery process</strong>: Automatic enumeration of available hardware</li>
 * <li><strong>Validation</strong>: Runtime validation ensures at least one device is selected</li>
 * </ul>
 * 
 * @param <T> the type of fitness values used in the evolutionary algorithm
 * @see AbstractEAExecutionContext
 * @see Platform
 * @see Device
 * @see net.bmahe.genetics4j.gpu.GPUFitnessEvaluator
 */
@Value.Immutable
public abstract class GPUEAExecutionContext<T extends Comparable<T>> extends AbstractEAExecutionContext<T> {

	/**
	 * Returns the predicate used to filter OpenCL platforms during device discovery.
	 * 
	 * <p>Platform filtering allows selective use of OpenCL platforms based on vendor,
	 * version, profile, or other platform characteristics. This enables optimization
	 * for specific hardware configurations or requirements.
	 * 
	 * <p>Common filtering criteria:
	 * <ul>
	 * <li><strong>Profile support</strong>: Filter by FULL_PROFILE vs EMBEDDED_PROFILE</li>
	 * <li><strong>Vendor preference</strong>: Select platforms from specific vendors</li>
	 * <li><strong>Version requirements</strong>: Ensure minimum OpenCL version support</li>
	 * <li><strong>Extension support</strong>: Filter platforms with required extensions</li>
	 * </ul>
	 * 
	 * @return the platform filtering predicate (default accepts all platforms)
	 */
	@Value.Default
	public Predicate<Platform> platformFilters() {
		return (platform) -> true;
	}

	/**
	 * Returns the predicate used to filter OpenCL devices during device discovery.
	 * 
	 * <p>Device filtering enables selection of appropriate compute devices based on
	 * type, capabilities, memory, and performance characteristics. This allows
	 * optimization for specific workload requirements and hardware constraints.
	 * 
	 * <p>Common filtering criteria:
	 * <ul>
	 * <li><strong>Device type</strong>: GPU, CPU, ACCELERATOR, or combinations</li>
	 * <li><strong>Memory capacity</strong>: Minimum global or local memory requirements</li>
	 * <li><strong>Compute units</strong>: Minimum parallel processing capability</li>
	 * <li><strong>OpenCL version</strong>: Required feature support level</li>
	 * <li><strong>Extensions</strong>: Specific OpenCL extension requirements</li>
	 * </ul>
	 * 
	 * @return the device filtering predicate (default accepts all devices)
	 */
	@Value.Default
	public Predicate<Device> deviceFilters() {
		return (device) -> true;
	}

	/**
	 * Creates a new builder for constructing GPU EA execution contexts.
	 * 
	 * <p>The builder provides a fluent interface for specifying both core EA execution
	 * parameters and GPU-specific device selection criteria. Type safety is ensured
	 * through generic parameterization.
	 * 
	 * @param <U> the type of fitness values for the execution context
	 * @return a new builder instance for creating GPU EA execution contexts
	 */
	public static <U extends Comparable<U>> ImmutableGPUEAExecutionContext.Builder<U> builder() {
		return ImmutableGPUEAExecutionContext.builder();
	}
}