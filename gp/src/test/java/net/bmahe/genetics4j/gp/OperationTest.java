package net.bmahe.genetics4j.gp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

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
		assertEquals(2, operation.acceptedTypes().size());
		assertEquals(2, operation.getArity());
		assertFalse(operation.isTerminal());
		assertEquals("lapin", operation.apply(new String[] { "LaPin" }, new Object[] {}));
	}
}