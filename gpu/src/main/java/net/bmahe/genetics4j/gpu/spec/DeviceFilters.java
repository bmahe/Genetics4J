package net.bmahe.genetics4j.gpu.spec;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.gpu.opencl.model.Device;
import net.bmahe.genetics4j.gpu.opencl.model.DeviceType;

/**
 * Utility class providing predicate-based filters for selecting OpenCL devices in GPU-accelerated evolutionary algorithms.
 * 
 * <p>DeviceFilters offers a fluent API for creating device selection criteria based on device characteristics
 * such as type, capabilities, and performance metrics. These filters are used to automatically select
 * appropriate OpenCL devices for GPU-accelerated evolutionary algorithm execution.
 * 
 * <p>Key functionality includes:
 * <ul>
 * <li><strong>Type-based filtering</strong>: Select devices by type (GPU, CPU, accelerator)</li>
 * <li><strong>Logical combinations</strong>: Combine filters using AND and OR operations</li>
 * <li><strong>Performance filtering</strong>: Filter devices based on computational capabilities</li>
 * <li><strong>Predicate composition</strong>: Build complex selection criteria from simple predicates</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Select GPU devices only
 * Predicate<Device> gpuFilter = DeviceFilters.ofGPU();
 * 
 * // Select CPU devices only
 * Predicate<Device> cpuFilter = DeviceFilters.ofCPU();
 * 
 * // Select GPU or accelerator devices
 * Predicate<Device> computeFilter = DeviceFilters.or(
 *     DeviceFilters.ofGPU(),
 *     DeviceFilters.ofType(DeviceType.ACCELERATOR)
 * );
 * 
 * // Select high-performance GPU devices
 * Predicate<Device> highPerformanceGPU = DeviceFilters.and(
 *     DeviceFilters.ofGPU(),
 *     device -> device.maxComputeUnits() >= 8,
 *     device -> device.maxWorkGroupSize() >= 256
 * );
 * 
 * // Apply filter to device selection
 * GPUEAExecutionContext context = GPUEAExecutionContext.builder()
 *     .deviceFilters(gpuFilter)
 *     .build();
 * }</pre>
 * 
 * <p>Device selection workflow:
 * <ol>
 * <li><strong>Platform discovery</strong>: Enumerate available OpenCL platforms</li>
 * <li><strong>Device enumeration</strong>: Discover devices on selected platforms</li>
 * <li><strong>Filter application</strong>: Apply device filters to candidate devices</li>
 * <li><strong>Device selection</strong>: Select filtered devices for EA execution</li>
 * </ol>
 * 
 * <p>Filter composition patterns:
 * <ul>
 * <li><strong>Type filtering</strong>: Select devices by computational type</li>
 * <li><strong>Capability filtering</strong>: Filter by device computational capabilities</li>
 * <li><strong>Performance filtering</strong>: Select devices meeting performance criteria</li>
 * <li><strong>Logical combinations</strong>: Combine multiple criteria using boolean logic</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Device enumeration overhead</strong>: Filters are applied during device discovery</li>
 * <li><strong>Capability validation</strong>: Ensure selected devices meet algorithm requirements</li>
 * <li><strong>Load balancing</strong>: Consider device performance when selecting multiple devices</li>
 * <li><strong>Fallback strategies</strong>: Implement fallback filters for systems with limited devices</li>
 * </ul>
 * 
 * @see Device
 * @see DeviceType
 * @see GPUEAExecutionContext
 */
public class DeviceFilters {

	private DeviceFilters() {

	}

	/**
	 * Creates a predicate that filters devices by the specified device type.
	 * 
	 * @param deviceType the OpenCL device type to filter for
	 * @return predicate that returns true for devices of the specified type
	 * @throws IllegalArgumentException if deviceType is null
	 */
	public static Predicate<Device> ofType(final DeviceType deviceType) {
		Validate.notNull(deviceType);

		return (device) -> device.deviceType()
				.contains(deviceType);
	}

	/**
	 * Creates a predicate that filters for GPU devices only.
	 * 
	 * <p>This is a convenience method equivalent to {@code ofType(DeviceType.GPU)}.
	 * 
	 * @return predicate that returns true for GPU devices
	 */
	public static Predicate<Device> ofGPU() {
		return ofType(DeviceType.GPU);
	}

	/**
	 * Creates a predicate that filters for CPU devices only.
	 * 
	 * <p>This is a convenience method equivalent to {@code ofType(DeviceType.CPU)}.
	 * 
	 * @return predicate that returns true for CPU devices
	 */
	public static Predicate<Device> ofCPU() {
		return ofType(DeviceType.CPU);
	}

	/**
	 * Creates a predicate that returns true if any of the provided predicates return true (logical OR).
	 * 
	 * @param predicates array of device predicates to combine with OR logic
	 * @return predicate that returns true if any input predicate returns true
	 * @throws IllegalArgumentException if predicates is null or empty
	 */
	public static Predicate<Device> or(@SuppressWarnings("unchecked") final Predicate<Device>... predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.length > 0);

		return device -> Arrays.stream(predicates)
				.anyMatch(predicate -> predicate.test(device));
	}

	/**
	 * Creates a predicate that returns true if any of the provided predicates return true (logical OR).
	 * 
	 * @param predicates collection of device predicates to combine with OR logic
	 * @return predicate that returns true if any input predicate returns true
	 * @throws IllegalArgumentException if predicates is null or empty
	 */
	public static Predicate<Device> or(final Collection<Predicate<Device>> predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.size() > 0);

		return device -> predicates.stream()
				.anyMatch(predicate -> predicate.test(device));
	}

	/**
	 * Creates a predicate that returns true only if all provided predicates return true (logical AND).
	 * 
	 * @param predicates array of device predicates to combine with AND logic
	 * @return predicate that returns true only if all input predicates return true
	 * @throws IllegalArgumentException if predicates is null or empty
	 */
	@SafeVarargs
	public static Predicate<Device> and(final Predicate<Device>... predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.length > 0);

		return device -> Arrays.stream(predicates)
				.allMatch(predicate -> predicate.test(device));
	}

	/**
	 * Creates a predicate that returns true only if all provided predicates return true (logical AND).
	 * 
	 * @param predicates collection of device predicates to combine with AND logic
	 * @return predicate that returns true only if all input predicates return true
	 * @throws IllegalArgumentException if predicates is null or empty
	 */
	public static Predicate<Device> and(final Collection<Predicate<Device>> predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.size() > 0);

		return device -> predicates.stream()
				.allMatch(predicate -> predicate.test(device));
	}

}