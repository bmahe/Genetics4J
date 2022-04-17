package net.bmahe.genetics4j.gpu.spec.fitness;

import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;

import net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext;
import net.bmahe.genetics4j.gpu.opencl.model.Device;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.CLData;

public class ResultExtractor {
	public static final Logger logger = LogManager.getLogger(ResultExtractor.class);

	private final Map<Device, Map<Integer, CLData>> resultData;

	protected CLData extractClData(final Device device, final int argumentIndex) {
		Validate.notNull(device);
		Validate.isTrue(argumentIndex >= 0);

		if (resultData.containsKey(device) == false) {
			throw new IllegalArgumentException("Could not find entry for device [" + device.name() + "]");
		}

		final var deviceResults = resultData.get(device);

		if (deviceResults.containsKey(argumentIndex) == false) {
			throw new IllegalArgumentException("No data defined for argument " + argumentIndex);
		}

		final var clData = deviceResults.get(argumentIndex);
		return clData;
	}

	public ResultExtractor(final Map<Device, Map<Integer, CLData>> _resultData) {

		this.resultData = _resultData;
	}

	public byte[] extractImageAsByteArray(final OpenCLExecutionContext openCLExecutionContext, final int argumentIndex,
			final int width, final int height, final int numChannels, final int channelSize) {
		Validate.isTrue(argumentIndex >= 0);
		Validate.isTrue(width > 0);
		Validate.isTrue(height > 0);
		Validate.isTrue(numChannels > 0);
		Validate.isTrue(channelSize > 0);

		final var device = openCLExecutionContext.device();
		final var clData = extractClData(device, argumentIndex);

		final var clCommandQueue = openCLExecutionContext.clCommandQueue();

		final byte[] data = new byte[width * height * numChannels * channelSize];
		CL.clEnqueueReadImage(clCommandQueue,
				clData.clMem(),
				CL.CL_TRUE,
				new long[] { 0, 0, 0 },
				new long[] { width, height, 1 },
				0,
				0,
				Pointer.to(data),
				0,
				null,
				null);

		return data;
	}

	public float[] extractFloatArray(final OpenCLExecutionContext openCLExecutionContext, final int argumentIndex) {
		final var device = openCLExecutionContext.device();
		final var clData = extractClData(device, argumentIndex);

		if (clData.clType() != Sizeof.cl_float) {
			throw new IllegalArgumentException("Data is not of type of float[]");
		}

		final var clCommandQueue = openCLExecutionContext.clCommandQueue();

		final float[] data = new float[clData.size()];
		CL.clEnqueueReadBuffer(clCommandQueue,
				clData.clMem(),
				CL.CL_TRUE,
				0,
				clData.size() * Sizeof.cl_float,
				Pointer.to(data),
				0,
				null,
				null);

		return data;
	}

	public int[] extractIntArray(final OpenCLExecutionContext openCLExecutionContext, final int argumentIndex) {
		final var device = openCLExecutionContext.device();
		final var clData = extractClData(device, argumentIndex);

		if (clData.clType() != Sizeof.cl_int) {
			throw new IllegalArgumentException("Data is not of type of int[]");
		}

		final var clCommandQueue = openCLExecutionContext.clCommandQueue();

		final int[] data = new int[clData.size()];
		CL.clEnqueueReadBuffer(clCommandQueue,
				clData.clMem(),
				CL.CL_TRUE,
				0,
				clData.size() * Sizeof.cl_int,
				Pointer.to(data),
				0,
				null,
				null);

		return data;
	}

	public long[] extractLongArray(final OpenCLExecutionContext openCLExecutionContext, final int argumentIndex) {
		final var device = openCLExecutionContext.device();
		final var clData = extractClData(device, argumentIndex);

		if (clData.clType() != Sizeof.cl_long) {
			throw new IllegalArgumentException("Data is not of type of long[]");
		}

		final var clCommandQueue = openCLExecutionContext.clCommandQueue();

		final long[] data = new long[clData.size()];
		CL.clEnqueueReadBuffer(clCommandQueue,
				clData.clMem(),
				CL.CL_TRUE,
				0,
				clData.size() * Sizeof.cl_long,
				Pointer.to(data),
				0,
				null,
				null);
		return data;
	}
}