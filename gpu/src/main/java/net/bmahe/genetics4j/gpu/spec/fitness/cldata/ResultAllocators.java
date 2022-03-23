package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import org.apache.commons.lang3.Validate;
import org.jocl.CL;
import org.jocl.Sizeof;
import org.jocl.cl_image_desc;
import org.jocl.cl_image_format;
import org.jocl.cl_mem;

public class ResultAllocators {

	private ResultAllocators() {
	}

	public static ResultAllocator ofSize(final int type, final int size) {
		Validate.isTrue(type > 0);
		Validate.isTrue(size > 0);

		return (openCLExecutionContext, generation, genotypes) -> {

			final var clContext = openCLExecutionContext.clContext();

			final cl_mem clMem = CL.clCreateBuffer(clContext, CL.CL_MEM_WRITE_ONLY, type * size, null, null);

			return CLData.of(clMem, type, size);
		};
	}

	public static ResultAllocator ofSizeFloat(final int size) {
		Validate.isTrue(size > 0);

		return ofSize(Sizeof.cl_float, size);
	}

	public static ResultAllocator ofSizeInt(final int size) {
		Validate.isTrue(size > 0);

		return ofSize(Sizeof.cl_int, size);
	}

	public static ResultAllocator ofMultiplePopulationSizeFloat(final int multiple) {
		Validate.isTrue(multiple > 0);

		return (openCLExecutionContext, generation, genotypes) -> {

			final var clContext = openCLExecutionContext.clContext();

			final int length = genotypes.size() * multiple;

			final cl_mem clMem = CL.clCreateBuffer(clContext, CL.CL_MEM_WRITE_ONLY, Sizeof.cl_float * length, null, null);

			return CLData.of(clMem, Sizeof.cl_float, length);
		};
	}

	public static ResultAllocator ofPopulationSizeFloat() {
		return ofMultiplePopulationSizeFloat(1);
	}

	public static ResultAllocator ofImage(final int width, final int height, final int channelOrder,
			final int dataType) {
		Validate.isTrue(width > 0);
		Validate.isTrue(height > 0);
		Validate.isTrue(channelOrder > 0);
		Validate.isTrue(dataType > 0);

		return (openCLExecutionContext, generation, genotypes) -> {
			final var clContext = openCLExecutionContext.clContext();

			final cl_image_desc imageDesc = new cl_image_desc();
			imageDesc.image_type = CL.CL_MEM_OBJECT_IMAGE2D;
			imageDesc.image_width = width;
			imageDesc.image_height = height;

			cl_image_format imageFormat = new cl_image_format();
			imageFormat.image_channel_order = channelOrder;
			imageFormat.image_channel_data_type = dataType;

			final cl_mem clImageMem = CL
					.clCreateImage(clContext, CL.CL_MEM_WRITE_ONLY, imageFormat, imageDesc, null, null);
			return CLData.of(clImageMem, Sizeof.cl_mem, 1);
		};
	}

	public static ResultAllocator ofImage(final int width, final int height) {
		return ofImage(width, height, CL.CL_RGBA, CL.CL_FLOAT);
	}
}