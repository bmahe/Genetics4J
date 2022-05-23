package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import org.jocl.cl_mem;

/**
 * Container used for referring to data hosted on a GPU
 */
@Value.Immutable
public interface CLData {

	/**
	 * OpenCL cl_mem that references the raw data
	 * 
	 * @return
	 */
	@Value.Parameter
	cl_mem clMem();

	/**
	 * OpenCL type contained within the clMem()
	 * 
	 * @return
	 */
	@Value.Parameter
	int clType();

	/**
	 * How many entries in the clMem()
	 * 
	 * @return
	 */
	@Value.Parameter
	int size();

	static CLData of(final cl_mem clMem, final int clType, final int size) {
		Validate.notNull(clMem);
		Validate.isTrue(clType > 0);
		Validate.isTrue(size > 0);

		return ImmutableCLData.of(clMem, clType, size);
	}
}