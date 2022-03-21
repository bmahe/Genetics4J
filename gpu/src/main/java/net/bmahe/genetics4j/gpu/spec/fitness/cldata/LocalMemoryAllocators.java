package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import org.apache.commons.lang3.Validate;
import org.jocl.Sizeof;

public class LocalMemoryAllocators {

	private LocalMemoryAllocators() {
	}

	public static LocalMemoryAllocator ofSize(final int type, final long size) {
		Validate.isTrue(type > 0);
		Validate.isTrue(size > 0);

		return (openCLExecutionContext, generation, genotypes) -> {

			return type * size;
		};
	}

	public static LocalMemoryAllocator ofSizeFloat(final long size) {
		Validate.isTrue(size > 0);

		return ofSize(Sizeof.cl_float, size);
	}

	public static LocalMemoryAllocator ofSizeInt(final long size) {
		Validate.isTrue(size > 0);

		return ofSize(Sizeof.cl_int, size);
	}
}