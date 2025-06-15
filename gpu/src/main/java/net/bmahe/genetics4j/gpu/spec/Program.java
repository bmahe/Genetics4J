package net.bmahe.genetics4j.gpu.spec;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;

/**
 * Specification for OpenCL programs containing kernel source code, build options, and compilation settings.
 * 
 * <p>Program represents a complete OpenCL program specification that includes kernel source code
 * (either as direct content or resource references), kernel definitions, and compilation options.
 * This specification is used by the GPU EA system to compile and execute OpenCL kernels for
 * fitness evaluation on GPU devices.
 * 
 * <p>A program can contain:
 * <ul>
 * <li><strong>Source content</strong>: Direct OpenCL C code as strings</li>
 * <li><strong>Resource references</strong>: Paths to OpenCL source files in the classpath</li>
 * <li><strong>Kernel definitions</strong>: Names of kernels to be compiled and made available</li>
 * <li><strong>Build options</strong>: Compiler flags and preprocessor definitions</li>
 * </ul>
 * 
 * <p>Program compilation workflow:
 * <ol>
 * <li><strong>Source loading</strong>: Load content from strings and resource files</li>
 * <li><strong>Source concatenation</strong>: Combine all sources into a single compilation unit</li>
 * <li><strong>Compilation</strong>: Compile with specified build options for target devices</li>
 * <li><strong>Kernel extraction</strong>: Create kernel objects for specified kernel names</li>
 * <li><strong>Validation</strong>: Verify all kernels were successfully created</li>
 * </ol>
 * 
 * <p>Common usage patterns:
 * <pre>{@code
 * // Simple single-kernel program from resource
 * Program basicProgram = Program.ofResource("/kernels/fitness.cl", "evaluate_fitness");
 * 
 * // Program with build options for optimization
 * Program optimizedProgram = Program.ofResource(
 *     "/kernels/optimization.cl", 
 *     "fitness_kernel",
 *     "-O3 -DPOPULATION_SIZE=1000 -DUSE_FAST_MATH"
 * );
 * 
 * // Complex program with multiple sources and kernels
 * Program complexProgram = Program.builder()
 *     .addContent("#define PROBLEM_SIZE 256")  // Direct content
 *     .addResources("/kernels/common.cl")       // Common utilities
 *     .addResources("/kernels/fitness.cl")      // Main fitness logic
 *     .addKernelNames("fitness_eval")           // Primary kernel
 *     .addKernelNames("data_preparation")       // Helper kernel
 *     .buildOptions("-cl-fast-relaxed-math -DLOCAL_SIZE=64")
 *     .build();
 * }</pre>
 * 
 * <p>Build options support:
 * <ul>
 * <li><strong>Optimization flags</strong>: -O0, -O1, -O2, -O3 for performance tuning</li>
 * <li><strong>Math optimizations</strong>: -cl-fast-relaxed-math for numerical performance</li>
 * <li><strong>Preprocessor definitions</strong>: -DMACRO=value for compile-time configuration</li>
 * <li><strong>Warning control</strong>: -w to disable warnings, -Werror to treat warnings as errors</li>
 * <li><strong>Standards compliance</strong>: -cl-std=CL1.2 for specific OpenCL version targeting</li>
 * </ul>
 * 
 * <p>Resource loading considerations:
 * <ul>
 * <li><strong>Classpath resolution</strong>: Resources loaded relative to classpath</li>
 * <li><strong>Encoding</strong>: Source files assumed to be UTF-8 encoded</li>
 * <li><strong>Include simulation</strong>: Manual concatenation instead of OpenCL #include</li>
 * <li><strong>Error handling</strong>: Resource loading failures result in compilation errors</li>
 * </ul>
 * 
 * <p>Validation and constraints:
 * <ul>
 * <li><strong>Kernel names required</strong>: At least one kernel name must be specified</li>
 * <li><strong>Source availability</strong>: Either content or resources must provide source code</li>
 * <li><strong>Name uniqueness</strong>: Kernel names must be unique within the program</li>
 * <li><strong>Compilation validity</strong>: Source code must compile successfully for target devices</li>
 * </ul>
 * 
 * @see net.bmahe.genetics4j.gpu.GPUFitnessEvaluator
 * @see net.bmahe.genetics4j.gpu.spec.GPUEAConfiguration
 * @see net.bmahe.genetics4j.gpu.opencl.OpenCLExecutionContext
 */
@Value.Immutable
public abstract class Program {

	/**
	 * Returns the direct OpenCL source code content as strings.
	 * 
	 * <p>Content represents OpenCL C source code provided directly as strings
	 * rather than loaded from resources. Multiple content strings are concatenated
	 * during compilation to form a single compilation unit.
	 * 
	 * @return list of OpenCL source code strings
	 */
	@Value.Parameter
	public abstract List<String> content();

	/**
	 * Returns the classpath resource paths containing OpenCL source code.
	 * 
	 * <p>Resources are loaded from the classpath at compilation time and
	 * concatenated with any direct content to form the complete program source.
	 * Resource paths should be relative to the classpath root.
	 * 
	 * @return set of classpath resource paths for OpenCL source files
	 */
	@Value.Parameter
	public abstract Set<String> resources();

	/**
	 * Returns the names of kernels to be extracted from the compiled program.
	 * 
	 * <p>Kernel names specify which functions in the OpenCL source should be
	 * made available as executable kernels. These names must correspond to
	 * functions declared with the {@code __kernel} qualifier in the source code.
	 * 
	 * @return set of kernel function names to extract after compilation
	 */
	@Value.Parameter
	public abstract Set<String> kernelNames();

	/**
	 * Returns the OpenCL compiler build options for program compilation.
	 * 
	 * <p>Build options are passed to the OpenCL compiler to control optimization,
	 * define preprocessor macros, and configure compilation behavior. Common options
	 * include optimization levels, math optimizations, and macro definitions.
	 * 
	 * @return optional build options string for OpenCL compilation
	 */
	public abstract Optional<String> buildOptions();

	@Value.Check
	protected void check() {
		Validate.notNull(kernelNames());
		Validate.isTrue(kernelNames().isEmpty() == false);
	}

	/**
	 * Creates a program from direct OpenCL source content with a single kernel.
	 * 
	 * <p>This factory method creates a simple program specification with source code
	 * provided directly as a string and a single kernel to be extracted.
	 * 
	 * @param content the OpenCL source code as a string
	 * @param kernelName the name of the kernel function to extract
	 * @return a new program specification with the given content and kernel
	 * @throws IllegalArgumentException if content or kernelName is null or blank
	 */
	public static Program ofContent(final String content, final String kernelName) {
		Validate.notBlank(content);
		Validate.notBlank(kernelName);

		return ImmutableProgram.builder()
				.addContent(content)
				.addKernelNames(kernelName)
				.build();
	}

	/**
	 * Creates a program from a classpath resource with a single kernel.
	 * 
	 * <p>This factory method creates a program specification that loads OpenCL source
	 * code from a classpath resource and extracts a single named kernel.
	 * 
	 * @param resource the classpath path to the OpenCL source file
	 * @param kernelName the name of the kernel function to extract
	 * @return a new program specification with the given resource and kernel
	 * @throws IllegalArgumentException if resource or kernelName is null or blank
	 */
	public static Program ofResource(final String resource, final String kernelName) {
		Validate.notBlank(resource);
		Validate.notBlank(kernelName);

		return ImmutableProgram.builder()
				.addResources(resource)
				.addKernelNames(kernelName)
				.build();
	}

	/**
	 * Creates a program from a classpath resource with a single kernel and build options.
	 * 
	 * <p>This factory method creates a program specification that loads OpenCL source
	 * code from a classpath resource, extracts a single named kernel, and applies
	 * the specified build options during compilation.
	 * 
	 * @param resource the classpath path to the OpenCL source file
	 * @param kernelName the name of the kernel function to extract
	 * @param buildOptions the build options for OpenCL compilation
	 * @return a new program specification with the given resource, kernel, and build options
	 * @throws IllegalArgumentException if resource or kernelName is null or blank
	 */
	public static Program ofResource(final String resource, final String kernelName, final String buildOptions) {
		Validate.notBlank(resource);
		Validate.notBlank(kernelName);

		return ImmutableProgram.builder()
				.addResources(resource)
				.addKernelNames(kernelName)
				.buildOptions(buildOptions)
				.build();
	}

	/**
	 * Creates a new builder for constructing complex program specifications.
	 * 
	 * <p>The builder provides a fluent interface for creating programs with multiple
	 * source files, kernels, and advanced configuration options.
	 * 
	 * @return a new builder instance for creating program specifications
	 */
	public static ImmutableProgram.Builder builder() {
		return ImmutableProgram.builder();
	}
}