package net.bmahe.genetics4j.gpu.spec.fitness.multistage;

import java.util.Map;

import org.immutables.value.Value;

import net.bmahe.genetics4j.gpu.spec.fitness.cldata.DataLoader;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.LocalMemoryAllocator;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.ResultAllocator;
import net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext.KernelExecutionContextComputer;

@Value.Immutable
public interface StageDescriptor {

	String kernelName();

	KernelExecutionContextComputer kernelExecutionContextComputer();

	Map<Integer, DataLoader> dataLoaders();

	Map<Integer, LocalMemoryAllocator> localMemoryAllocators();

	Map<Integer, ResultAllocator> resultAllocators();

	Map<Integer, Integer> reusePreviousResultAsArguments();

	Map<Integer, Integer> reusePreviousResultSizeAsArguments();

	Map<String, Integer> mapStaticDataAsArgument();

	static class Builder extends ImmutableStageDescriptor.Builder {
	}

	static Builder builder() {
		return new Builder();
	}
}