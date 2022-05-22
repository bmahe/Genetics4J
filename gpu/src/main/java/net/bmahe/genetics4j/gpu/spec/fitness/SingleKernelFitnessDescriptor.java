package net.bmahe.genetics4j.gpu.spec.fitness;

import java.util.Map;

import org.immutables.value.Value;

import net.bmahe.genetics4j.gpu.spec.fitness.cldata.DataLoader;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.LocalMemoryAllocator;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.ResultAllocator;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.StaticDataLoader;
import net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext.KernelExecutionContextComputer;

/**
 * Describes all the necessary information to execute an OpenCL kernel
 * <p>
 * As we may execute across multiple GPUs, may of these are computed at runtime
 * and may vary from GPU to GPU as their specs may differ
 */
@Value.Immutable
public interface SingleKernelFitnessDescriptor {

	/**
	 * Name of the kernel to execute
	 * 
	 * @return
	 */
	String kernelName();

	/**
	 * Computer for the kernel execution context (ex: globak work size)
	 * 
	 * @return
	 */
	KernelExecutionContextComputer kernelExecutionContextComputer();

	/**
	 * Association of kernel argument index and a static data loader
	 * 
	 * @return
	 */
	Map<Integer, StaticDataLoader> staticDataLoaders();

	/**
	 * Association of kernel argument index and a data loader
	 * 
	 * @return
	 */
	Map<Integer, DataLoader> dataLoaders();

	/**
	 * Association of kernel argument index and a local memory allocator
	 * 
	 * @return
	 */
	Map<Integer, LocalMemoryAllocator> localMemoryAllocators();

	/**
	 * Association of kernel argument index and a result allocator
	 * 
	 * @return
	 */
	Map<Integer, ResultAllocator> resultAllocators();

	static class Builder extends ImmutableSingleKernelFitnessDescriptor.Builder {
	}

	static Builder builder() {
		return new Builder();
	}
}