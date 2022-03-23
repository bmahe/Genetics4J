package net.bmahe.genetics4j.gpu.spec.fitness.cldata;

import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import org.jocl.cl_mem;

@Value.Immutable
public interface CLData {

	@Value.Parameter
	cl_mem clMem();

	@Value.Parameter
	int clType();

	@Value.Parameter
	int size();

	static CLData of(final cl_mem clMem, final int clType, final int size) {
		Validate.notNull(clMem);
		Validate.isTrue(clType > 0);
		Validate.isTrue(size > 0);

		return ImmutableCLData.of(clMem, clType, size);
	}
}