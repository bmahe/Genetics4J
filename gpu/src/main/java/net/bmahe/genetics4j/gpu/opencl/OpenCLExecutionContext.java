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

@Value.Immutable
public interface OpenCLExecutionContext {

	@Value.Parameter
	Platform platform();

	@Value.Parameter
	Device device();

	@Value.Parameter
	cl_context clContext();

	@Value.Parameter
	cl_command_queue clCommandQueue();

	@Value.Parameter
	cl_program clProgram();

	@Value.Parameter
	Map<String, cl_kernel> kernels();

	@Value.Parameter
	Map<String, KernelInfo> kernelInfos();

	default KernelInfo kernelInfo(final String kernelName) {
		return kernelInfos().get(kernelName);
	}

	public static class Builder extends ImmutableOpenCLExecutionContext.Builder {
	}

	public static Builder builder() {
		return new Builder();
	}
}