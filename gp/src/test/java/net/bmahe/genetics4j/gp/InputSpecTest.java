package net.bmahe.genetics4j.gp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class InputSpecTest {

	@Test
	public void inputSizeValidation() {
		final InputSpec inputSpec = ImmutableInputSpec.of(Arrays.asList(Double.class, String.class));
		assertNotNull(inputSpec);
		assertNotNull(inputSpec.types());

		assertEquals(2, inputSpec.types().size());
		assertEquals(2, inputSpec.inputSize());
	}
}