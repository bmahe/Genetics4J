package net.bmahe.genetics4j.gpu.spec;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

@Value.Immutable
public abstract class Program {

	@Value.Parameter
	public abstract List<String> content();

	@Value.Parameter
	public abstract Set<String> resources();

	@Value.Parameter
	public abstract Set<String> kernelNames();

	public abstract Optional<String> buildOptions();

	@Value.Check
	protected void check() {
		Validate.notNull(kernelNames());
		Validate.isTrue(kernelNames().size() > 0);

	}

	public static Program ofContent(final String content, final String kernelName) {
		Validate.notBlank(content);
		Validate.notBlank(kernelName);

		return ImmutableProgram.builder()
				.addContent(content)
				.addKernelNames(kernelName)
				.build();
	}

	public static Program ofResource(final String resource, final String kernelName) {
		Validate.notBlank(resource);
		Validate.notBlank(kernelName);

		return ImmutableProgram.builder()
				.addResources(resource)
				.addKernelNames(kernelName)
				.build();
	}

	public static Program ofResource(final String resource, final String kernelName, final String buildOptions) {
		Validate.notBlank(resource);
		Validate.notBlank(kernelName);

		return ImmutableProgram.builder()
				.addResources(resource)
				.addKernelNames(kernelName)
				.buildOptions(buildOptions)
				.build();
	}
}