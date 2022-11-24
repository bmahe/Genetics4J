package net.bmahe.genetics4j.neat;

import java.util.concurrent.atomic.AtomicInteger;

public class SpeciesIdGenerator {

	public static final int DEFAULT_INITIAL_ID = 0;

	private final AtomicInteger currentId;

	public SpeciesIdGenerator(final int initialValue) {
		currentId = new AtomicInteger(initialValue);
	}

	public SpeciesIdGenerator() {
		this(DEFAULT_INITIAL_ID);
	}

	public int computeNewId() {
		return currentId.getAndIncrement();
	}
}