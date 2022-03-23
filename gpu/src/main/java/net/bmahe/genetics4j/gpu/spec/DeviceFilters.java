package net.bmahe.genetics4j.gpu.spec;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import org.apache.commons.lang3.Validate;

import net.bmahe.genetics4j.gpu.opencl.model.Device;
import net.bmahe.genetics4j.gpu.opencl.model.DeviceType;

public class DeviceFilters {

	private DeviceFilters() {

	}

	public static Predicate<Device> ofType(final DeviceType deviceType) {
		Validate.notNull(deviceType);

		return (device) -> device.deviceType()
				.contains(deviceType);
	}

	public static Predicate<Device> ofGPU() {
		return ofType(DeviceType.GPU);
	}

	public static Predicate<Device> ofCPU() {
		return ofType(DeviceType.CPU);
	}

	public static Predicate<Device> or(@SuppressWarnings("unchecked") final Predicate<Device>... predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.length > 0);

		return device -> Arrays.stream(predicates)
				.anyMatch(predicate -> predicate.test(device));
	}

	public static Predicate<Device> or(final Collection<Predicate<Device>> predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.size() > 0);

		return device -> predicates.stream()
				.anyMatch(predicate -> predicate.test(device));
	}

	@SafeVarargs
	public static Predicate<Device> and(final Predicate<Device>... predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.length > 0);

		return device -> Arrays.stream(predicates)
				.allMatch(predicate -> predicate.test(device));
	}

	public static Predicate<Device> and(final Collection<Predicate<Device>> predicates) {
		Validate.notNull(predicates);
		Validate.isTrue(predicates.size() > 0);

		return device -> predicates.stream()
				.allMatch(predicate -> predicate.test(device));
	}

}