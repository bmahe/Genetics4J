package net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext;

public class KernelExecutionContextComputers {

	private KernelExecutionContextComputers() {
	}

	public static KernelExecutionContextComputer ofGenotypeSize() {
		return (openCLExecutionContext, kernelName, generation, genotypes) -> {
			return KernelExecutionContext.builder()
					.globalWorkSize(new long[] { genotypes.size() })
					.build();
		};
	}

	public static KernelExecutionContextComputer ofGlobalWorkSize(final long[] globalWorkSize) {
		return (openCLExecutionContext, kernelName, generation, genotypes) -> {
			return KernelExecutionContext.builder()
					.globalWorkSize(globalWorkSize)
					.build();
		};
	}

	public static KernelExecutionContextComputer ofGlobalWorkSize1D(final long globalWorkSize) {
		return (openCLExecutionContext, kernelName, generation, genotypes) -> {
			return KernelExecutionContext.builder()
					.globalWorkSize(globalWorkSize)
					.build();
		};
	}

	public static KernelExecutionContextComputer ofGenotypeSizeAndWorkGroup(final long[] workGroupSize) {
		return (openCLExecutionContext, kernelName, generation, genotypes) -> {
			return KernelExecutionContext.builder()
					.globalWorkSize(new long[] { genotypes.size() })
					.workGroupSize(workGroupSize)
					.build();
		};
	}

	public static KernelExecutionContextComputer of(final long[] globalWorkSize, final long[] workGroupSize) {
		return (openCLExecutionContext, kernelName, generation, genotypes) -> {
			return KernelExecutionContext.builder()
					.globalWorkSize(globalWorkSize)
					.workGroupSize(workGroupSize)
					.build();
		};
	}
}