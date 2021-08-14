package net.bmahe.genetics4j.extras.evolutionlisteners;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListener;

/**
 * Evolution Listener which writes the output of each generation to a CSV file
 *
 * @author bruno
 *
 * @param <T> Fitness type
 * @param <U> Data type written to the CSV
 */
@Value.Immutable
public abstract class CSVEvolutionListener<T extends Comparable<T>, U> implements EvolutionListener<T> {
	final static public Logger logger = LogManager.getLogger(CSVEvolutionListener.class);

	public static final boolean DEFAULT_AUTO_FLUSH = true;

	private CSVPrinter csvPrinter;

	protected CSVPrinter openPrinter() {

		final List<String> headers = columnExtractors().stream()
				.map(ce -> ce.header())
				.collect(Collectors.toUnmodifiableList());

		try {
			return CSVFormat.DEFAULT.withAutoFlush(autoFlush())
					.withHeader(headers.toArray(new String[headers.size()]))
					.print(Path.of(filename()), StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Could not open {}", filename(), e);
			throw new RuntimeException("Could not open file " + filename(), e);
		}

	}

	/**
	 * Whether or not the CSV writer has auto flush enabled. Defaults to
	 * {@value #DEFAULT_AUTO_FLUSH}
	 *
	 * @return
	 */
	@Value.Default
	public boolean autoFlush() {
		return DEFAULT_AUTO_FLUSH;
	}

	/**
	 * User defined function to provide some additional information when computing
	 * the value to write. Defaults to null
	 *
	 * @return
	 */
	@Value.Default
	public GenerationFunction<T, U> evolutionContextSupplier() {
		return (generation, population, fitness, isDone) -> null;
	}

	/**
	 * How many generations to skip between each writes. Defaults to writing every
	 * generations
	 *
	 * @return
	 */
	@Value.Default
	public int skipN() {
		return 0;
	}

	/**
	 * Users can supply an optional set of filters to control which individuals get
	 * written and in which order. Default to have no impact.
	 *
	 * @return
	 */
	@Value.Default
	public Function<Stream<EvolutionStep<T, U>>, Stream<EvolutionStep<T, U>>> filter() {
		return (stream) -> stream;
	}

	/**
	 * Destination file name for the CSV file
	 *
	 * @return
	 */
	@Value.Parameter
	public abstract String filename();

	/**
	 * List of Column Extractors. They specify how and what to write from each
	 * individual at a given generation
	 *
	 * @return
	 */
	@Value.Parameter
	public abstract List<ColumnExtractor<T, U>> columnExtractors();

	@Override
	public void onEvolution(final long generation, final List<Genotype> population, final List<T> fitness,
			final boolean isDone) {
		Validate.isTrue(generation >= 0);
		Validate.notNull(population);
		Validate.notNull(fitness);
		Validate.isTrue(population.size() > 0);
		Validate.isTrue(population.size() == fitness.size());

		if (isDone == false && skipN() > 0 && generation % skipN() != 0) {
			return;
		}

		if (csvPrinter == null) {
			csvPrinter = openPrinter();
		}

		final Optional<U> context = Optional
				.ofNullable(evolutionContextSupplier().apply(generation, population, fitness, isDone));

		final var rawIndividualStream = IntStream.range(0, population.size())
				.boxed()
				.map(individualIndex -> EvolutionStep.of(context,
						generation,
						individualIndex,
						population.get(individualIndex),
						fitness.get(individualIndex),
						isDone));

		final var filteredStream = filter().apply(rawIndividualStream);

		filteredStream.forEach(evolutionStep -> {
			final List<Object> columnValues = columnExtractors().stream()
					.map(ce -> ce.columnExtractorFunction())
					.map(cef -> cef.apply(evolutionStep))
					.collect(Collectors.toUnmodifiableList());

			try {
				csvPrinter.printRecord(columnValues);
			} catch (IOException e1) {
				logger.error("Could not write values: {}", columnValues, e1);
				throw new RuntimeException("Could not write values: " + columnValues, e1);
			}
		});

		if (isDone && csvPrinter != null) {
			try {
				csvPrinter.close(true);
			} catch (IOException e) {
				logger.error("Could not close CSV printer for filename {}", filename(), e);
				throw new RuntimeException("Could not close CSV printer for filename " + filename(), e);
			}
		}
	}

	public static class Builder<T extends Comparable<T>, U> extends ImmutableCSVEvolutionListener.Builder<T, U> {
	}

	public static <T extends Comparable<T>, U> CSVEvolutionListener<T, U> of(String filename,
			List<ColumnExtractor<T, U>> columnExtractors) {
		return ImmutableCSVEvolutionListener.of(filename, (Iterable<? extends ColumnExtractor<T, U>>) columnExtractors);
	}

	public static <T extends Comparable<T>, U> CSVEvolutionListener<T, U> of(String filename,
			Iterable<? extends ColumnExtractor<T, U>> columnExtractors) {
		return ImmutableCSVEvolutionListener.of(filename, columnExtractors);
	}

	public static <T extends Comparable<T>, U> CSVEvolutionListener<T, U> of(final String filename,
			final GenerationFunction<T, U> evolutionContextSupplier,
			final Iterable<? extends ColumnExtractor<T, U>> columnExtractors) {
		var csvEvolutionListenerBuilder = new CSVEvolutionListener.Builder<T, U>();

		csvEvolutionListenerBuilder.filename(filename)
				.evolutionContextSupplier(evolutionContextSupplier)
				.addAllColumnExtractors(columnExtractors);

		return csvEvolutionListenerBuilder.build();
	}

	public static <T extends Comparable<T>, U> CSVEvolutionListener<T, U> of(final String filename,
			final GenerationFunction<T, U> evolutionContextSupplier,
			final Iterable<? extends ColumnExtractor<T, U>> columnExtractors, final int skipN) {
		var csvEvolutionListenerBuilder = new CSVEvolutionListener.Builder<T, U>();

		csvEvolutionListenerBuilder.filename(filename)
				.evolutionContextSupplier(evolutionContextSupplier)
				.addAllColumnExtractors(columnExtractors)
				.skipN(skipN);

		return csvEvolutionListenerBuilder.build();
	}

	public static <T extends Comparable<T>, U> CSVEvolutionListener<T, U> ofTopN(final String filename,
			final GenerationFunction<T, U> evolutionContextSupplier,
			final Iterable<? extends ColumnExtractor<T, U>> columnExtractors, final Comparator<T> comparator,
			final int topN) {
		var csvEvolutionListenerBuilder = new CSVEvolutionListener.Builder<T, U>();

		csvEvolutionListenerBuilder.filename(filename)
				.evolutionContextSupplier(evolutionContextSupplier)
				.addAllColumnExtractors(columnExtractors)
				.filter(stream -> stream.sorted((a, b) -> comparator.reversed().compare(a.fitness(), b.fitness()))
						.limit(topN));

		return csvEvolutionListenerBuilder.build();
	}

	public static <T extends Comparable<T>, U> CSVEvolutionListener<T, U> ofTopN(final String filename,
			final GenerationFunction<T, U> evolutionContextSupplier,
			final Iterable<? extends ColumnExtractor<T, U>> columnExtractors, final int topN) {
		var csvEvolutionListenerBuilder = new CSVEvolutionListener.Builder<T, U>();

		csvEvolutionListenerBuilder.filename(filename)
				.evolutionContextSupplier(evolutionContextSupplier)
				.addAllColumnExtractors(columnExtractors)
				.filter(stream -> stream.sorted(Comparator.comparing(EvolutionStep::fitness)).limit(topN));

		return csvEvolutionListenerBuilder.build();
	}
}