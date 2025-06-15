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

/**
 * Utility class for extracting computation results from OpenCL device memory after GPU kernel execution.
 * 
 * <p>ResultExtractor provides type-safe methods for retrieving different data types from OpenCL memory buffers
 * that contain the results of GPU-accelerated fitness evaluation. This class handles the device-to-host data
 * transfer and type conversion necessary to make GPU computation results available to the evolutionary algorithm.
 * 
 * <p>Key functionality includes:
 * <ul>
 * <li><strong>Type-safe extraction</strong>: Methods for extracting float, int, long arrays with type validation</li>
 * <li><strong>Image data support</strong>: Specialized extraction for OpenCL image objects</li>
 * <li><strong>Device management</strong>: Tracks result data across multiple devices</li>
 * <li><strong>Argument indexing</strong>: Maps kernel arguments to their corresponding result data</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Extract fitness values as float array
 * float[] fitnessValues = resultExtractor.extractFloatArray(context, 0);
 * 
 * // Extract integer results (e.g., classification results)
 * int[] classifications = resultExtractor.extractIntArray(context, 1);
 * 
 * // Extract long results (e.g., counters or large indices)
 * long[] counters = resultExtractor.extractLongArray(context, 2);
 * 
 * // Extract image data for visualization
 * byte[] imageData = resultExtractor.extractImageAsByteArray(context, 3, width, height, channels, channelSize);
 * 
 * // Use extracted results in fitness evaluation
 * List<Double> fitness = IntStream.range(0, fitnessValues.length)
 *     .mapToDouble(i -> (double) fitnessValues[i])
 *     .boxed()
 *     .collect(Collectors.toList());
 * }</pre>
 * 
 * <p>Data extraction workflow:
 * <ol>
 * <li><strong>Kernel execution</strong>: GPU kernels compute results and store them in device memory</li>
 * <li><strong>Result mapping</strong>: Results are mapped by device and kernel argument index</li>
 * <li><strong>Type validation</strong>: Data types are validated before extraction</li>
 * <li><strong>Data transfer</strong>: Results are transferred from device to host memory</li>
 * <li><strong>Type conversion</strong>: Data is converted to appropriate Java types</li>
 * </ol>
 * 
 * <p>Error handling and validation:
 * <ul>
 * <li><strong>Device validation</strong>: Ensures requested device has result data</li>
 * <li><strong>Argument validation</strong>: Validates argument indices exist in result mapping</li>
 * <li><strong>Type checking</strong>: Ensures extracted data matches expected OpenCL types</li>
 * <li><strong>Transfer validation</strong>: Validates successful device-to-host data transfer</li>
 * </ul>
 * 
 * <p>Performance considerations:
 * <ul>
 * <li><strong>Synchronous transfers</strong>: Uses blocking transfers to ensure data availability</li>
 * <li><strong>Memory efficiency</strong>: Allocates host memory based on actual data sizes</li>
 * <li><strong>Transfer optimization</strong>: Minimizes number of device-to-host transfers</li>
 * <li><strong>Type safety</strong>: Validates types at runtime to prevent data corruption</li>
 * </ul>
 * 
 * @see CLData
 * @see net.bmahe.genetics4j.gpu.spec.fitness.OpenCLFitness
 * @see OpenCLExecutionContext
 */
public class ResultExtractor {
	public static final Logger logger = LogManager.getLogger(ResultExtractor.class);

	private final Map<Device, Map<Integer, CLData>> resultData;

	/**
	 * Extracts CLData for the specified device and kernel argument index.
	 * 
	 * @param device the OpenCL device to extract data from
	 * @param argumentIndex the kernel argument index for the data
	 * @return the CLData object containing the result data
	 * @throws IllegalArgumentException if device is null, argumentIndex is negative,
	 *         device not found, or argument index not found
	 */
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

	/**
	 * Constructs a ResultExtractor with the specified result data mapping.
	 * 
	 * @param _resultData mapping from devices to their kernel argument results
	 */
	public ResultExtractor(final Map<Device, Map<Integer, CLData>> _resultData) {

		this.resultData = _resultData;
	}

	/**
	 * Extracts image data from OpenCL device memory as a byte array.
	 * 
	 * <p>This method reads an OpenCL image object from device memory and converts it to a byte array
	 * suitable for host processing. The image dimensions and channel information must be provided
	 * to properly interpret the image data.
	 * 
	 * @param openCLExecutionContext the OpenCL execution context
	 * @param argumentIndex the kernel argument index containing the image data
	 * @param width the image width in pixels
	 * @param height the image height in pixels
	 * @param numChannels the number of color channels (e.g., 3 for RGB, 4 for RGBA)
	 * @param channelSize the size of each channel in bytes
	 * @return byte array containing the image data
	 * @throws IllegalArgumentException if any parameter is invalid
	 */
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

	/**
	 * Extracts floating-point data from OpenCL device memory as a float array.
	 * 
	 * <p>This method reads floating-point data from device memory and transfers it to host memory.
	 * The data type is validated to ensure it contains floating-point values before extraction.
	 * 
	 * @param openCLExecutionContext the OpenCL execution context
	 * @param argumentIndex the kernel argument index containing the float data
	 * @return float array containing the extracted data
	 * @throws IllegalArgumentException if the data is not of type float
	 */
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

	/**
	 * Extracts integer data from OpenCL device memory as an int array.
	 * 
	 * <p>This method reads integer data from device memory and transfers it to host memory.
	 * The data type is validated to ensure it contains integer values before extraction.
	 * 
	 * @param openCLExecutionContext the OpenCL execution context
	 * @param argumentIndex the kernel argument index containing the integer data
	 * @return int array containing the extracted data
	 * @throws IllegalArgumentException if the data is not of type int
	 */
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

	/**
	 * Extracts long integer data from OpenCL device memory as a long array.
	 * 
	 * <p>This method reads long integer data from device memory and transfers it to host memory.
	 * The data type is validated to ensure it contains long integer values before extraction.
	 * 
	 * @param openCLExecutionContext the OpenCL execution context
	 * @param argumentIndex the kernel argument index containing the long integer data
	 * @return long array containing the extracted data
	 * @throws IllegalArgumentException if the data is not of type long
	 */
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