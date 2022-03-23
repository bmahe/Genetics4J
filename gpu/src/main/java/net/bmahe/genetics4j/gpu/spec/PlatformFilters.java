package net.bmahe.genetics4j.gpu.spec;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.gpu.opencl.model.Platform;
import net.bmahe.genetics4j.gpu.opencl.model.PlatformProfile;

public class PlatformFilters {

	private PlatformFilters() {

	}

	public static Predicate<Platform> ofProfile(final PlatformProfile platformProfile) {
		Validate.notNull(platformProfile);

		return (platform) -> platformProfile.equals(platform.profile());
	}

	public static Predicate<Platform> ofExtension(final String extension) {
		Validate.notBlank(extension);

		return (platform) -> platform.extensions()
				.contains(extension);
	}

	public static Predicate<Platform> ofExtensions(final Set<String> extensions) {
		Validate.notNull(extensions);

		return (platform) -> CollectionUtils.containsAll(platform.extensions(), extensions);
	}

	public static Predicate<Platform> or(@SuppressWarnings("unchecked") final Predicate<Platform>... predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.length > 0);

		return platform -> Arrays.stream(predicates)
				.anyMatch(predicate -> predicate.test(platform));
	}

	public static Predicate<Platform> or(final Collection<Predicate<Platform>> predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.size() > 0);

		return platform -> predicates.stream()
				.anyMatch(predicate -> predicate.test(platform));
	}

	public static Predicate<Platform> and(@SuppressWarnings("unchecked") final Predicate<Platform>... predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.length > 0);

		return platform -> Arrays.stream(predicates)
				.allMatch(predicate -> predicate.test(platform));
	}

	public static Predicate<Platform> and(final Collection<Predicate<Platform>> predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.size() > 0);

		return platform -> predicates.stream()
				.allMatch(predicate -> predicate.test(platform));
	}

}