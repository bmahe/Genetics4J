package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import java.util.List;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext;

/**
 * Functional interface for loading genotype data into OpenCL device memory for GPU-accelerated fitness evaluation.
 * 
 * <p>DataLoader defines the contract for converting evolutionary algorithm genotypes into OpenCL memory buffers
 * that can be processed by GPU kernels. This abstraction allows different chromosome types and data formats
 * to be efficiently transferred to GPU memory for parallel fitness evaluation.
 * 
 * <p>Key responsibilities:
 * <ul>
 * <li><strong>Data conversion</strong>: Transform genotypes into OpenCL-compatible data formats</li>
 * <li><strong>Memory allocation</strong>: Allocate appropriate device memory for the data</li>
 * <li><strong>Data transfer</strong>: Copy converted data to GPU memory buffers</li>
 * <li><strong>Lifecycle management</strong>: Handle generation-specific data loading patterns</li>
 * </ul>
 * 
 * <p>Common implementation patterns:
 * <pre>{@code
 * // DataLoader for floating-point chromosomes
 * DataLoader floatLoader = (context, generation, genotypes) -> {
 *     // Convert genotypes to float array
 *     float[] data = genotypes.stream()
 *         .flatMap(genotype -> genotype.getChromosome(FloatChromosome.class).stream()
 *             .mapToDouble(FloatChromosome::getValue)
 *             .mapToObj(d -> (float) d))
 *         .toArray(float[]::new);
 *     
 *     // Allocate and populate GPU memory
 *     cl_mem buffer = clCreateBuffer(context.context(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
 *         data.length * Sizeof.cl_float, Pointer.to(data), null);
 *     
 *     return CLData.of(buffer, CL_FLOAT, data.length);
 * };
 * 
 * // DataLoader for integer chromosomes with generation-specific logic
 * DataLoader intLoader = (context, generation, genotypes) -> {
 *     int[] data = convertIntGenotypes(genotypes, generation);
 *     cl_mem buffer = allocateIntBuffer(context, data);
 *     return CLData.of(buffer, CL_INT, data.length);
 * };
 * 
 * // DataLoader using static data (parameters)
 * DataLoader paramLoader = DataLoaders.staticFloatArray(new float[]{0.1f, 0.2f, 0.5f});
 * }</pre>
 * 
 * <p>Memory management considerations:
 * <ul>
 * <li><strong>Buffer allocation</strong>: Use appropriate OpenCL memory flags (READ_ONLY, READ_WRITE, etc.)</li>
 * <li><strong>Data copying</strong>: Consider using CL_MEM_COPY_HOST_PTR for immediate data transfer</li>
 * <li><strong>Memory alignment</strong>: Ensure data is properly aligned for GPU access patterns</li>
 * <li><strong>Resource cleanup</strong>: Returned CLData objects should be properly released after use</li>
 * </ul>
 * 
 * <p>Performance optimization strategies:
 * <ul>
 * <li><strong>Batch processing</strong>: Process multiple genotypes efficiently in single operations</li>
 * <li><strong>Memory reuse</strong>: Reuse memory buffers across generations when possible</li>
 * <li><strong>Data layout</strong>: Optimize data layout for coalesced GPU memory access</li>
 * <li><strong>Transfer minimization</strong>: Minimize host-to-device data transfers</li>
 * </ul>
 * 
 * <p>Generation-aware loading:
 * <ul>
 * <li><strong>Generation tracking</strong>: Use generation parameter for evolution-dependent data</li>
 * <li><strong>Adaptive parameters</strong>: Modify algorithm parameters based on generation number</li>
 * <li><strong>Dynamic sizing</strong>: Adjust data sizes based on population evolution</li>
 * <li><strong>Caching strategies</strong>: Cache expensive computations across generations</li>
 * </ul>
 * 
 * @see CLData
 * @see DataLoaders
 * @see StaticDataLoader
 * @see net.bmahe.genetics4j.gpu.spec.fitness.OpenCLFitness
 */
@FunctionalInterface
public interface DataLoader {

	/**
	 * Loads genotype data into OpenCL device memory for GPU processing.
	 * 
	 * <p>This method converts the provided genotypes into an appropriate data format,
	 * allocates OpenCL device memory, and transfers the converted data to the GPU.
	 * The resulting CLData object can be used as input for OpenCL kernels.
	 * 
	 * @param openCLExecutionContext the OpenCL execution context providing device access
	 * @param generation the current generation number for generation-aware loading
	 * @param genotypes the list of genotypes to convert and load into device memory
	 * @return CLData object containing the loaded data in device memory
	 * @throws IllegalArgumentException if any parameter is null
	 * @throws RuntimeException if OpenCL memory allocation or data transfer fails
	 */
	CLData load(final OpenCLExecutionContext openCLExecutionContext, final long generation,
			final List<Genotype> genotypes);
}