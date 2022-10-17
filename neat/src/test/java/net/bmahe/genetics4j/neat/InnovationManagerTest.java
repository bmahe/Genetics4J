package net.bmahe.genetics4j.neat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class InnovationManagerTest {

	@Test
	public void simple() {

		final var innovationManager = new InnovationManager();

		assertThrows(IllegalArgumentException.class, () -> innovationManager.computeNewId(10, 10));

		final int id = innovationManager.computeNewId(0, 1);
		assertEquals(id, innovationManager.computeNewId(0, 1));

		assertNotEquals(id, innovationManager.computeNewId(0, 2));
		assertNotEquals(id, innovationManager.computeNewId(1, 2));

		assertEquals(id, innovationManager.computeNewId(0, 1));

		innovationManager.resetCache();
		assertNotEquals(id, innovationManager.computeNewId(0, 1));
	}
}