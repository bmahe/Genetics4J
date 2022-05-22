package net.bmahe.genetics4j.gpu.spec.fitness.multistage;

import java.util.Map;

import org.immutables.value.Value;

import net.bmahe.genetics4j.gpu.spec.fitness.cldata.DataLoader;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.LocalMemoryAllocator;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.ResultAllocator;
import net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext.KernelExecutionContextComputer;

/**
 * Fully describes how to execute a specific stage with OpenCL
 *
 */
@Value.Immutable
public interface StageDescriptor {

	/**
	 * Kernel name
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

	/**
	 * Association of the kernel argument used for a result of the previous stage to
	 * the kernel argument for this execution
	 * 
	 * @return
	 */
	Map<Integer, Integer> reusePreviousResultAsArguments();

	/**
	 * Association of the size of the data from the result of the previous stage to
	 * the kernel argument for this execution
	 * 
	 * @return
	 */
	Map<Integer, Integer> reusePreviousResultSizeAsArguments();

	/**
	 * Association of static data to a kernel argument index
	 */
	Map<String, Integer> mapStaticDataAsArgument();

	static class Builder extends ImmutableStageDescriptor.Builder {
	}

	static Builder builder() {
		return new Builder();
	}
}