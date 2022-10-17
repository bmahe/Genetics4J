package net.bmahe.genetics4j.neat;

import java.util.concurrent.atomic.AtomicInteger;

public class SpeciesIdGenerator {

	private final AtomicInteger currentId = new AtomicInteger();

	public int computeNewId() {
		return currentId.getAndIncrement();
	}
}