package net.bmahe.genetics4j.gp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class OperationTest {

	@Test
	public void checkTerminal() {
		final Operation<String> operation = ImmutableOperation
				.of("name", Collections.emptyList(), Double.class, (String[] str, Object[] obj) -> str[0].toLowerCase());

		assertNotNull(operation);
		assertTrue(operation.isTerminal());
	}

	@Test
	public void checkNotTerminal() {
		final Operation<String> operation = ImmutableOperation.of("name",
				Collections.singletonList(Double.class),
				Double.class,
				(String[] str, Object[] obj) -> str[0].toLowerCase());

		assertNotNull(operation);
		assertFalse(operation.isTerminal());
	}

	@Test
	public void simple() {

		final Operation<String> operation = ImmutableOperation.of("name",
				Arrays.asList(Double.class, Integer.class),
				Double.class,
				(String[] str, Object[] obj) -> str[0].toLowerCase());

		assertNotNull(operation);
		assertEquals("name", operation.getName());
		assertEquals("name", operation.getPrettyName());
		assertEquals(2,
				operation.acceptedTypes()
						.size());
		assertEquals(2, operation.getArity());
		assertFalse(operation.isTerminal());
		assertEquals("lapin", operation.apply(new String[] { "LaPin" }, new Object[] {}));
	}
}