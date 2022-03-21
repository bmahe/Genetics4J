package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import org.apache.commons.lang3.Validate;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_image_desc;
import org.jocl.cl_image_format;
import org.jocl.cl_mem;

public class StaticDataLoaders {

	private StaticDataLoaders() {
	}

	public static StaticDataLoader of(final boolean readOnly, final int dataType, final Pointer dataPtr,
			final int dataLength) {

		long bufferFlags = CL.CL_MEM_COPY_HOST_PTR;

		if (readOnly) {
			bufferFlags |= CL.CL_MEM_READ_ONLY;
		}
		final long fBufferFlags = bufferFlags;

		return (openCLExecutionContext) -> {
			final var clContext = openCLExecutionContext.clContext();

			final cl_mem dataMem = CL.clCreateBuffer(clContext, fBufferFlags, dataType * dataLength, dataPtr, null);

			return CLData.of(dataMem, dataType, dataLength);
		};
	}

	public static StaticDataLoader of(final int[] data, final boolean readOnly) {
		return of(readOnly, Sizeof.cl_int, Pointer.to(data), data.length);
	}

	public static StaticDataLoader of(final int... data) {
		return of(data, true);
	}

	public static StaticDataLoader of(final float[] data, final boolean readOnly) {
		return of(readOnly, Sizeof.cl_float, Pointer.to(data), data.length);
	}

	public static StaticDataLoader of(final float... data) {
		return of(data, true);
	}

	public static StaticDataLoader of(final long[] data, final boolean readOnly) {
		return of(readOnly, Sizeof.cl_long, Pointer.to(data), data.length);
	}

	public static StaticDataLoader of(final long... data) {
		return of(data, true);
	}

	public static StaticDataLoader of(final double[] data, final boolean readOnly) {
		return of(readOnly, Sizeof.cl_double, Pointer.to(data), data.length);
	}

	public static StaticDataLoader of(final double... data) {
		return of(data, true);
	}

	/**
	 * Expect an evenly shaped data
	 * 
	 * @param data
	 * @param readOnly
	 * @return
	 */
	public static StaticDataLoader ofLinearize(final double[][] data, final boolean readOnly) {
		Validate.isTrue(data.length > 0);
		Validate.isTrue(data[0].length > 0);

		final int numColumns = data[0].length;
		final double[] dataLinear = new double[data.length * numColumns];
		for (int i = 0; i < data.length; i++) {
			if (data[i].length != numColumns) {
				throw new IllegalArgumentException(
						String.format("Got %d columns for index %d. Should have been %d", data[i].length, i, numColumns));
			}

			final int baseIndex = i * numColumns;
			for (int j = 0; j < numColumns; j++) {
				dataLinear[baseIndex + j] = data[i][j];
			}
		}

		return of(dataLinear, readOnly);
	}

	/**
	 * Expect an evenly shaped data
	 * 
	 * @param data
	 * @param readOnly
	 * @return
	 */
	public static StaticDataLoader ofLinearize(final float[][] data, final boolean readOnly) {
		Validate.isTrue(data.length > 0);
		Validate.isTrue(data[0].length > 0);

		final int numColumns = data[0].length;
		final float[] dataLinear = new float[data.length * numColumns];
		for (int i = 0; i < data.length; i++) {
			if (data[i].length != numColumns) {
				throw new IllegalArgumentException(
						String.format("Got %d columns for index %d. Should have been %d", data[i].length, i, numColumns));
			}

			final int baseIndex = i * numColumns;
			for (int j = 0; j < numColumns; j++) {
				dataLinear[baseIndex + j] = data[i][j];
			}
		}

		return of(dataLinear, readOnly);
	}

	/**
	 * Expect an evenly shaped data
	 * 
	 * @param data
	 * @return
	 */
	public static StaticDataLoader ofLinearize(final float[][] data) {
		return ofLinearize(data, true);
	}

	/**
	 * Expect an evenly shaped data
	 * 
	 * @param data
	 * @param readOnly
	 * @return
	 */
	public static StaticDataLoader ofLinearize(final int[][] data, final boolean readOnly) {
		Validate.isTrue(data.length > 0);
		Validate.isTrue(data[0].length > 0);

		final int numColumns = data[0].length;
		final int[] dataLinear = new int[data.length * numColumns];
		for (int i = 0; i < data.length; i++) {
			if (data[i].length != numColumns) {
				throw new IllegalArgumentException(
						String.format("Got %d columns for index %d. Should have been %d", data[i].length, i, numColumns));
			}

			final int baseIndex = i * numColumns;
			for (int j = 0; j < numColumns; j++) {
				dataLinear[baseIndex + j] = data[i][j];
			}
		}

		return of(dataLinear, readOnly);
	}

	/**
	 * Expect an evenly shaped data
	 * 
	 * @param data
	 * @param readOnly
	 * @return
	 */
	public static StaticDataLoader ofLinearize(final long[][] data, final boolean readOnly) {
		Validate.isTrue(data.length > 0);
		Validate.isTrue(data[0].length > 0);

		final int numColumns = data[0].length;
		final long[] dataLinear = new long[data.length * numColumns];
		for (int i = 0; i < data.length; i++) {
			if (data[i].length != numColumns) {
				throw new IllegalArgumentException(
						String.format("Got %d columns for index %d. Should have been %d", data[i].length, i, numColumns));
			}

			final int baseIndex = i * numColumns;
			for (int j = 0; j < numColumns; j++) {
				dataLinear[baseIndex + j] = data[i][j];
			}
		}

		return of(dataLinear, readOnly);
	}

	public static StaticDataLoader ofImage(final byte[] data, final int width, final int height, final int channelOrder,
			final int channelDataType, final boolean readOnly) {
		Validate.isTrue(data.length > 0);

		long bufferFlags = CL.CL_MEM_COPY_HOST_PTR;

		if (readOnly) {
			bufferFlags |= CL.CL_MEM_READ_ONLY;
		}
		final long fBufferFlags = bufferFlags;

		return (openCLExecutionContext) -> {
			final var clContext = openCLExecutionContext.clContext();
			final var clCommandQueue = openCLExecutionContext.clCommandQueue();

			final var imageDesc = new cl_image_desc();
			imageDesc.image_type = CL.CL_MEM_OBJECT_IMAGE2D;
			imageDesc.image_width = width;
			imageDesc.image_height = height;

			final var imageFormat = new cl_image_format();
			imageFormat.image_channel_order = channelOrder;
			imageFormat.image_channel_data_type = channelDataType;

			final var clMem = CL.clCreateImage(clContext, fBufferFlags, imageFormat, imageDesc, Pointer.to(data), null);

			CL.clEnqueueWriteImage(clCommandQueue,
					clMem,
					true,
					new long[] { 0, 0, 0 },
					new long[] { width, height, 1 },
					0,
					0,
					Pointer.to(data),
					0,
					null,
					null);

			return CLData.of(clMem, 1, data.length);
		};
	}

	public static StaticDataLoader ofImage(final byte[] data, final int width, final int height, final int channelOrder,
			final int channelDataType) {
		return ofImage(data, width, height, channelOrder, channelDataType, true);
	}
}