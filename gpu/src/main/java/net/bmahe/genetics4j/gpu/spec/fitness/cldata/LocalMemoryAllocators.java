package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import org.apache.commons.lang3.Validate;
import org.jocl.Sizeof;

public class LocalMemoryAllocators {

	private LocalMemoryAllocators() {
	}

	public static LocalMemoryAllocator ofSize(final int type, final long size) {
		Validate.isTrue(type > 0);
		Validate.isTrue(size > 0);

		return (openCLExecutionContext, kernelExecutionContext, generation, genotypes) -> {

			return type * size;
		};
	}

	public static LocalMemoryAllocator ofWorkGroupSize(final int type, final int multiple) {
		Validate.isTrue(multiple > 0);

		return (openCLExecutionContext, kernelExecutionContext, generation, genotypes) -> {

			final long[] workGroupSize = kernelExecutionContext.workGroupSize()
					.get();
			long totalWorkGroupSize = 1;
			for (int i = 0; i < workGroupSize.length; i++) {
				totalWorkGroupSize *= workGroupSize[i];
			}
			return type * totalWorkGroupSize * multiple;
		};
	}

	public static LocalMemoryAllocator ofWorkGroupSize(final int type) {
		return ofWorkGroupSize(type, 1);
	}

	public static LocalMemoryAllocator ofMaxWorkGroupSize(final int type, final int multiple) {
		Validate.isTrue(multiple > 0);

		return (openCLExecutionContext, kernelExecutionContext, generation, genotypes) -> {

			final var device = openCLExecutionContext.device();
			return type * device.maxWorkGroupSize() * multiple;
		};
	}

	public static LocalMemoryAllocator ofMaxWorkGroupSize(final int type) {
		return ofMaxWorkGroupSize(type, 1);
	}

	public static LocalMemoryAllocator ofMaxWorkGroupSizeFloat(final int multiple) {
		return ofMaxWorkGroupSize(Sizeof.cl_float, multiple);
	}

	public static LocalMemoryAllocator ofMaxWorkGroupSizeFloat() {
		return ofMaxWorkGroupSize(Sizeof.cl_float, 1);
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