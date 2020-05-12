package net.bmahe.genetics4j.extras.evolutionlisteners;

import org.immutables.value.Value;

@Value.Immutable
public interface ColumnExtractor<T extends Comparable<T>, U> {

	@Value.Parameter
	String header();

	@Value.Parameter
	ColumnExtractorFunction<T, U> columnExtractorFunction();

	public static class Builder<T extends Comparable<T>, U> extends ImmutableColumnExtractor.Builder<T, U> {
	}

	public static <T extends Comparable<T>, U> ColumnExtractor<T, U> of(final String header,
			final ColumnExtractorFunction<T, U> columnExtractorFunction) {
		return ImmutableColumnExtractor.of(header, columnExtractorFunction);
	}

}