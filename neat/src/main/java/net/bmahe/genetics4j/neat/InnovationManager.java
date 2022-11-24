package net.bmahe.genetics4j.neat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InnovationManager {
	public static final Logger logger = LogManager.getLogger(InnovationManager.class);

	public static final int DEFAULT_INITIAL_ID = 0;

	private final AtomicInteger currentId;

	private final ConcurrentHashMap<ConnectionPair, Integer> innovationCache = new ConcurrentHashMap<>();

	public InnovationManager(final int initialValue) {
		currentId = new AtomicInteger(initialValue);
	}

	public InnovationManager() {
		this(DEFAULT_INITIAL_ID);
	}

	public int computeNewId(final int from, final int to) {
		Validate.isTrue(from != to);

		final var connectionPair = new ConnectionPair(from, to);
		return innovationCache.computeIfAbsent(connectionPair, k -> currentId.getAndIncrement());
	}

	public void resetCache() {
		logger.trace("Resetting cache with currently {} entries", innovationCache.size());
		innovationCache.clear();
	}
}