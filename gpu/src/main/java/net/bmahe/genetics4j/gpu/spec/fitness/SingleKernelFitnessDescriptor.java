package net.bmahe.genetics4j.gpu.spec.fitness;

import java.util.Map;

import org.immutables.value.Value;

import net.bmahe.genetics4j.gpu.spec.fitness.cldata.DataLoader;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.LocalMemoryAllocator;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.ResultAllocator;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.StaticDataLoader;
import net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext.KernelExecutionContextComputer;

@Value.Immutable
public interface SingleKernelFitnessDescriptor {

	String kernelName();

	KernelExecutionContextComputer kernelExecutionContextComputer();

	Map<Integer, StaticDataLoader> staticDataLoaders();

	Map<Integer, DataLoader> dataLoaders();

	Map<Integer, LocalMemoryAllocator> localMemoryAllocators();

	Map<Integer, ResultAllocator> resultAllocators();

	static class Builder extends ImmutableSingleKernelFitnessDescriptor.Builder {
	}

	static Builder builder() {
		return new Builder();
	}
}