package net.bmahe.genetics4j.extras.evolutionlisteners;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.immutables.value.Value;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListener;

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

	@Value.Default
	public boolean autoFlush() {
		return DEFAULT_AUTO_FLUSH;
	}

	@Value.Default
	public GenerationFunction<T, U> evolutionContextSupplier() {
		return (generation, population, fitness, isDone) -> null;
	}

	@Value.Default
	public int skipN() {
		return 0;
	}

	@Value.Parameter
	public abstract String filename();

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

		for (int individualIndex = 0; individualIndex < population.size(); individualIndex++) {
			final int individualIndexFinal = individualIndex;

			final EvolutionStep<T, U> evolutionStep = EvolutionStep.of(context,
					generation,
					individualIndex,
					population.get(individualIndexFinal),
					fitness.get(individualIndexFinal),
					isDone);

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
		}

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

}