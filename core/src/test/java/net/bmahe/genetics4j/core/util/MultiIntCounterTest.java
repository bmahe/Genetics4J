package net.bmahe.genetics4j.core.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class MultiIntCounterTest {
	final static public Logger logger = LogManager.getLogger(MultiIntCounterTest.class);

	@Test
	public void basicValidation() {
		final MultiIntCounter multiIntCounter = new MultiIntCounter(2, 3, 2);
		assertArrayEquals(new int[] { 0, 0, 0 }, multiIntCounter.getIndices());
		assertEquals(true, multiIntCounter.hasNext());

		int count = 0;
		while (multiIntCounter.hasNext()) {
			logger.info(multiIntCounter.toString());

			multiIntCounter.next();
			count++;
		}

		assertEquals(2 * 3 * 2, count);
		assertEquals(count, multiIntCounter.getTotal());
	}
}