package net.bmahe.genetics4j.gpu.spec;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.gpu.opencl.model.Platform;
import net.bmahe.genetics4j.gpu.opencl.model.PlatformProfile;

/**
 * Utility class providing predicate-based filters for selecting OpenCL platforms in GPU-accelerated evolutionary algorithms.
 * 
 * <p>PlatformFilters offers a fluent API for creating platform selection criteria based on platform characteristics
 * such as profile type, supported extensions, and vendor capabilities. These filters are used to automatically
 * select appropriate OpenCL platforms before device enumeration and selection.
 * 
 * <p>Key functionality includes:
 * <ul>
 * <li><strong>Profile-based filtering</strong>: Select platforms by profile (FULL_PROFILE or EMBEDDED_PROFILE)</li>
 * <li><strong>Extension filtering</strong>: Filter platforms based on supported OpenCL extensions</li>
 * <li><strong>Logical combinations</strong>: Combine filters using AND and OR operations</li>
 * <li><strong>Predicate composition</strong>: Build complex selection criteria from simple predicates</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Select platforms with full OpenCL profile
 * Predicate<Platform> fullProfileFilter = PlatformFilters.ofProfile(PlatformProfile.FULL_PROFILE);
 * 
 * // Select platforms supporting double precision arithmetic
 * Predicate<Platform> fp64Filter = PlatformFilters.ofExtension("cl_khr_fp64");
 * 
 * // Select platforms with multiple required extensions
 * Set<String> requiredExtensions = Set.of("cl_khr_fp64", "cl_khr_global_int32_base_atomics");
 * Predicate<Platform> extensionFilter = PlatformFilters.ofExtensions(requiredExtensions);
 * 
 * // Combine multiple criteria
 * Predicate<Platform> advancedFilter = PlatformFilters.and(
 *     PlatformFilters.ofProfile(PlatformProfile.FULL_PROFILE),
 *     PlatformFilters.ofExtension("cl_khr_fp64")
 * );
 * 
 * // Apply filter to platform selection
 * GPUEAExecutionContext context = GPUEAExecutionContext.builder()
 *     .platformFilters(fullProfileFilter)
 *     .build();
 * }</pre>
 * 
 * <p>Platform selection workflow:
 * <ol>
 * <li><strong>Platform enumeration</strong>: Discover all available OpenCL platforms</li>
 * <li><strong>Filter application</strong>: Apply platform filters to candidate platforms</li>
 * <li><strong>Platform validation</strong>: Validate filtered platforms meet requirements</li>
 * <li><strong>Device discovery</strong>: Enumerate devices on selected platforms</li>
 * </ol>
 * 
 * <p>Filter criteria considerations:
 * <ul>
 * <li><strong>Profile compatibility</strong>: FULL_PROFILE platforms typically offer more features</li>
 * <li><strong>Extension requirements</strong>: Filter for extensions required by algorithms</li>
 * <li><strong>Vendor optimization</strong>: Consider vendor-specific optimizations and extensions</li>
 * <li><strong>Version requirements</strong>: Ensure platforms support required OpenCL versions</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Platform enumeration overhead</strong>: Filters are applied during platform discovery</li>
 * <li><strong>Extension validation</strong>: Extension checks may have runtime overhead</li>
 * <li><strong>Fallback strategies</strong>: Implement fallback filters for limited platform availability</li>
 * <li><strong>Caching</strong>: Platform characteristics are typically static during execution</li>
 * </ul>
 * 
 * @see Platform
 * @see PlatformProfile
 * @see GPUEAExecutionContext
 */
public class PlatformFilters {

	private PlatformFilters() {

	}

	/**
	 * Creates a predicate that filters platforms by the specified profile type.
	 * 
	 * @param platformProfile the OpenCL profile type to filter for (FULL_PROFILE or EMBEDDED_PROFILE)
	 * @return predicate that returns true for platforms with the specified profile
	 * @throws IllegalArgumentException if platformProfile is null
	 */
	public static Predicate<Platform> ofProfile(final PlatformProfile platformProfile) {
		Validate.notNull(platformProfile);

		return (platform) -> platformProfile.equals(platform.profile());
	}

	/**
	 * Creates a predicate that filters platforms supporting the specified OpenCL extension.
	 * 
	 * @param extension the OpenCL extension name to filter for (e.g., "cl_khr_fp64")
	 * @return predicate that returns true for platforms supporting the extension
	 * @throws IllegalArgumentException if extension is null or blank
	 */
	public static Predicate<Platform> ofExtension(final String extension) {
		Validate.notBlank(extension);

		return (platform) -> platform.extensions()
				.contains(extension);
	}

	/**
	 * Creates a predicate that filters platforms supporting all specified OpenCL extensions.
	 * 
	 * @param extensions set of OpenCL extension names that must all be supported
	 * @return predicate that returns true for platforms supporting all specified extensions
	 * @throws IllegalArgumentException if extensions is null
	 */
	public static Predicate<Platform> ofExtensions(final Set<String> extensions) {
		Validate.notNull(extensions);

		return (platform) -> CollectionUtils.containsAll(platform.extensions(), extensions);
	}

	/**
	 * Creates a predicate that returns true if any of the provided predicates return true (logical OR).
	 * 
	 * @param predicates array of platform predicates to combine with OR logic
	 * @return predicate that returns true if any input predicate returns true
	 * @throws IllegalArgumentException if predicates is null or empty
	 */
	public static Predicate<Platform> or(@SuppressWarnings("unchecked") final Predicate<Platform>... predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.length > 0);

		return platform -> Arrays.stream(predicates)
				.anyMatch(predicate -> predicate.test(platform));
	}

	/**
	 * Creates a predicate that returns true if any of the provided predicates return true (logical OR).
	 * 
	 * @param predicates collection of platform predicates to combine with OR logic
	 * @return predicate that returns true if any input predicate returns true
	 * @throws IllegalArgumentException if predicates is null or empty
	 */
	public static Predicate<Platform> or(final Collection<Predicate<Platform>> predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.size() > 0);

		return platform -> predicates.stream()
				.anyMatch(predicate -> predicate.test(platform));
	}

	/**
	 * Creates a predicate that returns true only if all provided predicates return true (logical AND).
	 * 
	 * @param predicates array of platform predicates to combine with AND logic
	 * @return predicate that returns true only if all input predicates return true
	 * @throws IllegalArgumentException if predicates is null or empty
	 */
	public static Predicate<Platform> and(@SuppressWarnings("unchecked") final Predicate<Platform>... predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.length > 0);

		return platform -> Arrays.stream(predicates)
				.allMatch(predicate -> predicate.test(platform));
	}

	/**
	 * Creates a predicate that returns true only if all provided predicates return true (logical AND).
	 * 
	 * @param predicates collection of platform predicates to combine with AND logic
	 * @return predicate that returns true only if all input predicates return true
	 * @throws IllegalArgumentException if predicates is null or empty
	 */
	public static Predicate<Platform> and(final Collection<Predicate<Platform>> predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.size() > 0);

		return platform -> predicates.stream()
				.allMatch(predicate -> predicate.test(platform));
	}

}