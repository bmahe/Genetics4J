package net.bmahe.genetics4j.gpu.opencl;

import org.apache.commons.lang3.Validate;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;

public class KernelInfoUtils {

	private KernelInfoUtils() {

	}

	public static long getKernelWorkGroupInfoLong(final cl_device_id deviceId, final cl_kernel kernel,
			final int parameter) {
		Validate.notNull(deviceId);
		Validate.notNull(kernel);

		final long[] values = new long[1];
		CL.clGetKernelWorkGroupInfo(kernel, deviceId, parameter, Sizeof.cl_long, Pointer.to(values), null);

		return values[0];
	}
}