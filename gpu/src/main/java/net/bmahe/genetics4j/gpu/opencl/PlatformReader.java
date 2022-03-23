package net.bmahe.genetics4j.gpu.opencl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jocl.CL;
import org.jocl.cl_platform_id;

import net.bmahe.genetics4j.gpu.opencl.model.Platform;
import net.bmahe.genetics4j.gpu.opencl.model.PlatformProfile;

public class PlatformReader {
	public final static Logger logger = LogManager.getLogger(PlatformReader.class);

	public final static String PROFILE_FULL_STR = "FULL_PROFILE";
	public final static String PROFILE_EMBEDDED = "EMBEDDED_PROFILE";

	public Platform read(final cl_platform_id platformId) {
		Validate.notNull(platformId);

		final var platformBuilder = Platform.builder();

		platformBuilder.platformId(platformId);

		final String platformName = PlatformUtils.getStringParameter(platformId, CL.CL_PLATFORM_NAME);
		platformBuilder.name(platformName);

		final String platformVendor = PlatformUtils.getStringParameter(platformId, CL.CL_PLATFORM_VENDOR);
		platformBuilder.vendor(platformVendor);

		final String platformVersion = PlatformUtils.getStringParameter(platformId, CL.CL_PLATFORM_VERSION);
		platformBuilder.version(platformVersion);

		final String platformExtensions = PlatformUtils.getStringParameter(platformId, CL.CL_PLATFORM_EXTENSIONS);
		if (StringUtils.isNotBlank(platformExtensions)) {
			final String[] platformExtensionsArr = platformExtensions.split(StringUtils.SPACE);
			if (platformExtensionsArr.length > 0) {
				platformBuilder.addExtensions(platformExtensionsArr);
			}
		}

		final String platformProfileStr = PlatformUtils.getStringParameter(platformId, CL.CL_PLATFORM_PROFILE);
		final var platformProfile = switch (platformProfileStr) {
			case PROFILE_FULL_STR -> PlatformProfile.FULL_PROFILE;
			case PROFILE_EMBEDDED -> PlatformProfile.FULL_PROFILE;
			default -> throw new IllegalArgumentException("Unexpected value: " + platformProfileStr);
		};
		platformBuilder.profile(platformProfile);

		final int numDevices = PlatformUtils.numDevices(platformId);
		platformBuilder.numDevices(numDevices);

		return platformBuilder.build();
	}
}