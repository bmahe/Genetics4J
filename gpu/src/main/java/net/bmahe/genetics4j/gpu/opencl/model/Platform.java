package net.bmahe.genetics4j.gpu.opencl.model;

import java.util.Set;

import org.immutables.value.Value;
import org.jocl.cl_platform_id;

/**
 * Represents an OpenCL platform providing access to compute devices and their capabilities.
 * 
 * <p>Platform encapsulates the information about an OpenCL platform, which is a vendor-specific
 * implementation of the OpenCL specification. Each platform represents a collection of OpenCL
 * devices (CPUs, GPUs, accelerators) provided by a single vendor with consistent runtime behavior.
 * 
 * <p>Key platform characteristics include:
 * <ul>
 * <li><strong>Vendor identification</strong>: Platform vendor (AMD, NVIDIA, Intel, etc.)</li>
 * <li><strong>Capability profile</strong>: FULL_PROFILE or EMBEDDED_PROFILE feature support</li>
 * <li><strong>Version support</strong>: OpenCL specification version implemented</li>
 * <li><strong>Extension support</strong>: Additional features beyond the core specification</li>
 * <li><strong>Device count</strong>: Number of compute devices available on this platform</li>
 * </ul>
 * 
 * <p>Platform selection considerations:
 * <ul>
 * <li><strong>Vendor optimization</strong>: Different vendors may optimize for different workloads</li>
 * <li><strong>Feature requirements</strong>: Embedded profiles may lack required features</li>
 * <li><strong>Version compatibility</strong>: Newer OpenCL versions provide additional features</li>
 * <li><strong>Extension dependencies</strong>: Some algorithms may require specific extensions</li>
 * <li><strong>Device availability</strong>: Platforms must have compatible devices</li>
 * </ul>
 * 
 * <p>Common filtering patterns:
 * <pre>{@code
 * // Select platforms with full OpenCL profile support
 * Predicate<Platform> fullProfileFilter = platform -> 
 *     platform.profile() == PlatformProfile.FULL_PROFILE;
 * 
 * // Select platforms from specific vendors
 * Predicate<Platform> vendorFilter = platform -> 
 *     platform.vendor().toLowerCase().contains("nvidia") ||
 *     platform.vendor().toLowerCase().contains("amd");
 * 
 * // Select platforms with minimum OpenCL version
 * Predicate<Platform> versionFilter = platform -> {
 *     String version = platform.version();
 *     return version.contains("2.") || version.contains("3.");
 * };
 * 
 * // Select platforms with required extensions
 * Predicate<Platform> extensionFilter = platform ->
 *     platform.extensions().contains("cl_khr_fp64");
 * 
 * // Combine filters for comprehensive platform selection
 * Predicate<Platform> combinedFilter = fullProfileFilter
 *     .and(versionFilter)
 *     .and(platform -> platform.numDevices() > 0);
 * }</pre>
 * 
 * <p>Platform discovery workflow:
 * <ol>
 * <li><strong>Enumeration</strong>: System discovers all available OpenCL platforms</li>
 * <li><strong>Information query</strong>: Platform properties are read from OpenCL runtime</li>
 * <li><strong>Model creation</strong>: Platform objects are created with discovered information</li>
 * <li><strong>Filtering</strong>: User-defined predicates select appropriate platforms</li>
 * <li><strong>Device discovery</strong>: Selected platforms are queried for available devices</li>
 * </ol>
 * 
 * <p>Performance implications:
 * <ul>
 * <li><strong>Driver optimization</strong>: Platform vendors optimize for their hardware</li>
 * <li><strong>Memory models</strong>: Different platforms may have different memory hierarchies</li>
 * <li><strong>Kernel compilation</strong>: Platform-specific optimizations during compilation</li>
 * <li><strong>Runtime behavior</strong>: Platform-specific scheduling and resource management</li>
 * </ul>
 * 
 * <p>Error handling considerations:
 * <ul>
 * <li><strong>Platform availability</strong>: Platforms may become unavailable at runtime</li>
 * <li><strong>Version compatibility</strong>: Kernels may require specific OpenCL versions</li>
 * <li><strong>Extension support</strong>: Missing extensions may cause compilation failures</li>
 * <li><strong>Device enumeration</strong>: Platform may have no compatible devices</li>
 * </ul>
 * 
 * @see Device
 * @see PlatformProfile
 * @see net.bmahe.genetics4j.gpu.spec.GPUEAExecutionContext#platformFilters()
 * @see net.bmahe.genetics4j.gpu.opencl.PlatformUtils
 */
@Value.Immutable
public interface Platform {

	/**
	 * Returns the native OpenCL platform identifier.
	 * 
	 * @return the OpenCL platform ID for low-level operations
	 */
	cl_platform_id platformId();

	/**
	 * Returns the OpenCL profile supported by this platform.
	 * 
	 * @return the platform profile (FULL_PROFILE or EMBEDDED_PROFILE)
	 */
	PlatformProfile profile();

	/**
	 * Returns the OpenCL version string supported by this platform.
	 * 
	 * @return the OpenCL version (e.g., "OpenCL 2.1")
	 */
	String version();

	/**
	 * Returns the platform name provided by the vendor.
	 * 
	 * @return the human-readable platform name
	 */
	String name();

	/**
	 * Returns the platform vendor name.
	 * 
	 * @return the vendor name (e.g., "NVIDIA Corporation", "AMD")
	 */
	String vendor();

	/**
	 * Returns the set of OpenCL extensions supported by this platform.
	 * 
	 * @return set of extension names (e.g., "cl_khr_fp64", "cl_khr_global_int32_base_atomics")
	 */
	Set<String> extensions();

	/**
	 * Returns the number of OpenCL devices available on this platform.
	 * 
	 * @return the count of devices that can be used for computation
	 */
	int numDevices();
	
	/**
	 * Creates a new builder for constructing Platform instances.
	 * 
	 * @return a new builder for creating platform objects
	 */
	static ImmutablePlatform.Builder builder() {
		return ImmutablePlatform.builder();
	}
}