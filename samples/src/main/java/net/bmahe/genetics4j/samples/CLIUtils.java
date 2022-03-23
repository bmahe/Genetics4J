package net.bmahe.genetics4j.samples;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;

public class CLIUtils {

	private CLIUtils() {

	}

	public static void cliHelpAndExit(final Logger logger, final Class<?> clazz, final Options options,
			final String extraMessage) {
		Validate.notNull(logger);
		Validate.notNull(clazz);

		if (StringUtils.isNotBlank(extraMessage)) {
			logger.error(extraMessage);
		}

		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(clazz.getSimpleName(), options);
		System.exit(-1);
	}
}