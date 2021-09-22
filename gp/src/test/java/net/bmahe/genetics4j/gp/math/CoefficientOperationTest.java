package net.bmahe.genetics4j.gp.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class CoefficientOperationTest {

	@Test
	public void acceptedTypes() {

		final Long VALUE = 123456L;
		final String NAME = "LONG_COEFFICIENT";
		final CoefficientOperation<Long> coefficientOperation = new CoefficientOperation<Long>() {

			@Override
			public Long value() {
				return VALUE;
			}

			@Override
			public String getName() {
				return NAME;
			}

			@Override
			public Class returnedType() {
				// TODO Auto-generated method stub
				return Long.class;
			}
		};

		assertEquals(VALUE, coefficientOperation.value());
		assertEquals(NAME, coefficientOperation.getName());
		assertEquals(Long.class, coefficientOperation.returnedType());
		assertNotNull(coefficientOperation.acceptedTypes());
		assertEquals(0, coefficientOperation.acceptedTypes().size());
		assertNotNull(coefficientOperation.compute());
		assertEquals(VALUE, coefficientOperation.compute().apply(null, null));
	}
}