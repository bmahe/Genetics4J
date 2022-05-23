package net.bmahe.genetics4j.gpu.opencl;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jocl.CL;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;

import net.bmahe.genetics4j.gpu.opencl.model.KernelInfo;

public class KernelInfoReader {
	public static final Logger logger = LogManager.getLogger(KernelInfoReader.class);

	public KernelInfo read(final cl_device_id deviceId, final cl_kernel kernel, final String kernelName) {
		Validate.notNull(deviceId);
		Validate.notNull(kernel);
		Validate.notBlank(kernelName);

		final var kernelInfoBuilder = KernelInfo.builder();
		kernelInfoBuilder.name(kernelName);

		final long workGroupSize = KernelInfoUtils
				.getKernelWorkGroupInfoLong(deviceId, kernel, CL.CL_KERNEL_WORK_GROUP_SIZE);
		kernelInfoBuilder.workGroupSize(workGroupSize);

		final long preferredWorkGroupSizeMultiple = KernelInfoUtils
				.getKernelWorkGroupInfoLong(deviceId, kernel, CL.CL_KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE);
		kernelInfoBuilder.preferredWorkGroupSizeMultiple(preferredWorkGroupSizeMultiple);

		final long localMemSize = KernelInfoUtils
				.getKernelWorkGroupInfoLong(deviceId, kernel, CL.CL_KERNEL_LOCAL_MEM_SIZE);
		kernelInfoBuilder.localMemSize(localMemSize);

		final long privateMemSize = KernelInfoUtils
				.getKernelWorkGroupInfoLong(deviceId, kernel, CL.CL_KERNEL_PRIVATE_MEM_SIZE);
		kernelInfoBuilder.privateMemSize(privateMemSize);

		return kernelInfoBuilder.build();
	}
}