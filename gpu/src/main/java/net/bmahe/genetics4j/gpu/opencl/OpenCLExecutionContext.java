package net.bmahe.genetics4j.gpu.opencl;

import java.util.Map;

import org.immutables.value.Value;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_kernel;
import org.jocl.cl_program;

import net.bmahe.genetics4j.gpu.opencl.model.Device;
import net.bmahe.genetics4j.gpu.opencl.model.KernelInfo;
import net.bmahe.genetics4j.gpu.opencl.model.Platform;

/**
 * Encapsulates a complete OpenCL execution environment for a specific device with compiled kernels and runtime context.
 * 
 * <p>OpenCLExecutionContext represents a fully initialized OpenCL execution environment tied to a specific
 * device and containing all the resources needed for kernel execution. This includes the OpenCL context,
 * command queue, compiled program, and kernel objects, along with associated metadata about platform
 * and device capabilities.
 * 
 * <p>The execution context serves as the primary interface between high-level fitness evaluation code
 * and low-level OpenCL operations. It provides access to:
 * <ul>
 * <li><strong>Device information</strong>: Platform and device metadata for optimization decisions</li>
 * <li><strong>OpenCL runtime</strong>: Context and command queue for memory and execution management</li>
 * <li><strong>Compiled kernels</strong>: Ready-to-execute kernel objects with associated metadata</li>
 * <li><strong>Execution parameters</strong>: Kernel work group information for optimal kernel launch</li>
 * </ul>
 * 
 * <p>Context lifecycle and management:
 * <ol>
 * <li><strong>Creation</strong>: Built by {@link net.bmahe.genetics4j.gpu.GPUFitnessEvaluator} during initialization</li>
 * <li><strong>Usage</strong>: Passed to fitness functions for kernel execution and memory operations</li>
 * <li><strong>Cleanup</strong>: Resources automatically released by the fitness evaluator</li>
 * </ol>
 * 
 * <p>Key usage patterns in fitness evaluation:
 * <pre>{@code
 * public CompletableFuture<List<Double>> compute(OpenCLExecutionContext context, 
 *         ExecutorService executor, long generation, List<Genotype> genotypes) {
 *     
 *     return CompletableFuture.supplyAsync(() -> {
 *         // Access device capabilities for optimization
 *         Device device = context.device();
 *         int maxWorkGroupSize = device.maxWorkGroupSize();
 *         
 *         // Get compiled kernel for execution
 *         cl_kernel fitnessKernel = context.kernels().get("fitness_evaluation");
 *         
 *         // Get kernel execution parameters
 *         KernelInfo kernelInfo = context.kernelInfo("fitness_evaluation");
 *         int preferredWorkGroupSize = kernelInfo.preferredWorkGroupSizeMultiple();
 *         
 *         // Execute kernel with optimal work group configuration
 *         executeKernel(context, fitnessKernel, genotypes.size(), preferredWorkGroupSize);
 *         
 *         // Extract results using the execution context
 *         return extractResults(context, genotypes.size());
 *     }, executor);
 * }
 * }</pre>
 * 
 * <p>Memory management considerations:
 * <ul>
 * <li><strong>Context ownership</strong>: The execution context owns OpenCL resources</li>
 * <li><strong>Thread safety</strong>: OpenCL contexts are not thread-safe; use appropriate synchronization</li>
 * <li><strong>Resource lifecycle</strong>: Resources are managed by the parent fitness evaluator</li>
 * <li><strong>Command queue usage</strong>: Use the provided command queue for all operations</li>
 * </ul>
 * 
 * <p>Performance optimization capabilities:
 * <ul>
 * <li><strong>Device-specific tuning</strong>: Access device capabilities for optimal kernel configuration</li>
 * <li><strong>Kernel information</strong>: Use kernel metadata for work group size optimization</li>
 * <li><strong>Memory hierarchy</strong>: Leverage device memory characteristics for data layout</li>
 * <li><strong>Compute capabilities</strong>: Adapt algorithms based on device compute units and features</li>
 * </ul>
 * 
 * <p>Error handling and robustness:
 * <ul>
 * <li><strong>Resource validation</strong>: All OpenCL objects are validated during context creation</li>
 * <li><strong>Device compatibility</strong>: Context ensures device supports required kernels</li>
 * <li><strong>Kernel availability</strong>: All specified kernels are guaranteed to be compiled and available</li>
 * <li><strong>Exception safety</strong>: Context provides consistent state even if operations fail</li>
 * </ul>
 * 
 * @see net.bmahe.genetics4j.gpu.GPUFitnessEvaluator
 * @see net.bmahe.genetics4j.gpu.spec.fitness.OpenCLFitness
 * @see Platform
 * @see Device
 * @see KernelInfo
 */
@Value.Immutable
public interface OpenCLExecutionContext {

	/**
	 * Returns the OpenCL platform associated with this execution context.
	 * 
	 * @return the platform containing the device for this context
	 */
	@Value.Parameter
	Platform platform();

	/**
	 * Returns the OpenCL device associated with this execution context.
	 * 
	 * @return the device on which kernels will be executed
	 */
	@Value.Parameter
	Device device();

	/**
	 * Returns the OpenCL context for this execution environment.
	 * 
	 * @return the OpenCL context for memory and resource management
	 */
	@Value.Parameter
	cl_context clContext();

	/**
	 * Returns the OpenCL command queue for kernel execution and memory operations.
	 * 
	 * @return the command queue for submitting OpenCL operations
	 */
	@Value.Parameter
	cl_command_queue clCommandQueue();

	/**
	 * Returns the compiled OpenCL program containing all kernels.
	 * 
	 * @return the compiled OpenCL program object
	 */
	@Value.Parameter
	cl_program clProgram();

	/**
	 * Returns a map of kernel names to compiled kernel objects.
	 * 
	 * @return map from kernel names to executable kernel objects
	 */
	@Value.Parameter
	Map<String, cl_kernel> kernels();

	/**
	 * Returns a map of kernel names to kernel execution information.
	 * 
	 * @return map from kernel names to kernel metadata and execution parameters
	 */
	@Value.Parameter
	Map<String, KernelInfo> kernelInfos();

	/**
	 * Convenience method to retrieve kernel execution information by name.
	 * 
	 * @param kernelName the name of the kernel to get information for
	 * @return the kernel execution information, or null if not found
	 */
	default KernelInfo kernelInfo(final String kernelName) {
		return kernelInfos().get(kernelName);
	}

	public static class Builder extends ImmutableOpenCLExecutionContext.Builder {
	}

	/**
	 * Creates a new builder for constructing OpenCL execution contexts.
	 * 
	 * @return a new builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}
}