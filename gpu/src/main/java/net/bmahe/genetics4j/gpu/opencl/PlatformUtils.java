package net.bmahe.genetics4j.gpu.opencl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.cl_platform_id;

public class PlatformUtils {

	private PlatformUtils() {

	}

	public static int numPlatforms() {
		final int[] numPlatforms = new int[1];
		CL.clGetPlatformIDs(0, null, numPlatforms);

		return numPlatforms[0];
	}

	public static List<cl_platform_id> platformIds(final int numPlatforms) {
		Validate.isTrue(numPlatforms >= 0);

		if (numPlatforms == 0) {
			return List.of();
		}

		final cl_platform_id[] platformIds = new cl_platform_id[numPlatforms];
		CL.clGetPlatformIDs(platformIds.length, platformIds, null);

		return Arrays.asList(platformIds);
	}

	public static String getStringParameter(final cl_platform_id platformId, final int platformName) {
		Validate.notNull(platformId);

		final long[] parameterSize = new long[1];
		CL.clGetPlatformInfo(platformId, platformName, 0, null, parameterSize);

		final byte[] buffer = new byte[(int) parameterSize[0]];
		CL.clGetPlatformInfo(platformId, platformName, buffer.length, Pointer.to(buffer), null);

		return new String(buffer, 0, buffer.length - 1);
	}

	public static int numDevices(final cl_platform_id platformId, final long deviceType) {
		Validate.notNull(platformId);

		int[] numDevices = new int[1];
		CL.clGetDeviceIDs(platformId, deviceType, 0, null, numDevices);

		return numDevices[0];
	}

	public static int numDevices(final cl_platform_id platformId) {
		Validate.notNull(platformId);
		
		return numDevices(platformId, CL.CL_DEVICE_TYPE_ALL);
	}
}