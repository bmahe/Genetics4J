package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import org.jocl.cl_mem;

/**
 * Container representing data stored in OpenCL device memory for GPU-accelerated evolutionary algorithm processing.
 * 
 * <p>CLData encapsulates the information needed to reference and manage data that has been allocated and stored
 * on an OpenCL device (GPU). This includes the OpenCL memory object, the data type, and the number of elements,
 * providing a type-safe way to work with GPU memory buffers in evolutionary algorithm fitness evaluation.
 * 
 * <p>Key characteristics:
 * <ul>
 * <li><strong>Memory reference</strong>: OpenCL memory object (cl_mem) pointing to device memory</li>
 * <li><strong>Type information</strong>: OpenCL data type for proper kernel parameter binding</li>
 * <li><strong>Size tracking</strong>: Number of elements for bounds checking and memory management</li>
 * <li><strong>Resource management</strong>: Facilitates proper cleanup of OpenCL memory objects</li>
 * </ul>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Create CLData for population chromosomes
 * CLData populationData = CLData.of(chromosomeBuffer, CL.CL_FLOAT, populationSize * chromosomeLength);
 * 
 * // Create CLData for fitness results
 * CLData fitnessData = CLData.of(fitnessBuffer, CL.CL_DOUBLE, populationSize);
 * 
 * // Create CLData for algorithm parameters
 * CLData parameterData = CLData.of(paramBuffer, CL.CL_INT, parameterCount);
 * 
 * // Use in kernel execution
 * clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(populationData.clMem()));
 * clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(fitnessData.clMem()));
 * }</pre>
 * 
 * <p>Memory lifecycle management:
 * <ol>
 * <li><strong>Allocation</strong>: Memory is allocated using clCreateBuffer or similar OpenCL functions</li>
 * <li><strong>Wrapping</strong>: CLData object is created to wrap the allocated memory</li>
 * <li><strong>Usage</strong>: Memory is used in kernel execution through CLData references</li>
 * <li><strong>Cleanup</strong>: Memory is released using clReleaseMemObject when no longer needed</li>
 * </ol>
 * 
 * <p>Data type mapping:
 * <ul>
 * <li><strong>CL_FLOAT</strong>: Single-precision floating-point data</li>
 * <li><strong>CL_DOUBLE</strong>: Double-precision floating-point data</li>
 * <li><strong>CL_INT</strong>: 32-bit integer data</li>
 * <li><strong>CL_CHAR</strong>: 8-bit character data</li>
 * </ul>
 * 
 * <p>Error handling considerations:
 * <ul>
 * <li><strong>Memory validation</strong>: Ensures cl_mem objects are valid before use</li>
 * <li><strong>Type safety</strong>: Validates OpenCL data types are positive</li>
 * <li><strong>Size validation</strong>: Ensures element counts are positive</li>
 * <li><strong>Resource tracking</strong>: Facilitates proper memory cleanup</li>
 * </ul>
 * 
 * @see DataLoader
 * @see ResultExtractor
 * @see net.bmahe.genetics4j.gpu.spec.fitness.OpenCLFitness
 */
@Value.Immutable
public interface CLData {

	/**
	 * Returns the OpenCL memory object that references the data stored on the device.
	 * 
	 * @return the OpenCL memory object (cl_mem) containing the device data
	 */
	@Value.Parameter
	cl_mem clMem();

	/**
	 * Returns the OpenCL data type of the elements stored in the memory buffer.
	 * 
	 * <p>Common OpenCL types include CL_FLOAT, CL_DOUBLE, CL_INT, and CL_CHAR.
	 * This information is used for proper kernel parameter binding and type checking.
	 * 
	 * @return the OpenCL data type constant (e.g., CL_FLOAT, CL_DOUBLE)
	 */
	@Value.Parameter
	int clType();

	/**
	 * Returns the number of elements stored in the OpenCL memory buffer.
	 * 
	 * <p>This represents the count of individual data elements (not bytes) contained
	 * in the memory object. For example, a buffer containing 1000 floating-point values
	 * would have a size of 1000, regardless of the actual byte size.
	 * 
	 * @return the number of elements in the memory buffer
	 */
	@Value.Parameter
	int size();

	/**
	 * Creates a new CLData instance with the specified OpenCL memory object, data type, and size.
	 * 
	 * @param clMem the OpenCL memory object containing the device data
	 * @param clType the OpenCL data type constant (e.g., CL_FLOAT, CL_DOUBLE)
	 * @param size the number of elements in the memory buffer
	 * @return a new CLData instance
	 * @throws IllegalArgumentException if clMem is null, clType is not positive, or size is not positive
	 */
	static CLData of(final cl_mem clMem, final int clType, final int size) {
		Validate.notNull(clMem);
		Validate.isTrue(clType > 0);
		Validate.isTrue(size > 0);

		return ImmutableCLData.of(clMem, clType, size);
	}
}