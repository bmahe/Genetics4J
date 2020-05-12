package net.bmahe.genetics4j.core.evolutionlisteners;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;

public class EvolutionListenerLogTopN<T extends Comparable<T>> implements EvolutionListener<T> {

	private final Logger logger;
	private final int topN;
	private final int skipN;
	private final Comparator<T> comparator;
	private Function<Genotype, String> prettyPrinter;

	public EvolutionListenerLogTopN(final Logger _logger, final int _topN, final int _skipN,
			final Comparator<T> _comparator, final Function<Genotype, String> _prettyPrinter) {
		Validate.notNull(_logger);
		Validate.isTrue(_topN > 0);
		Validate.isTrue(_skipN >= 0);

		this.logger = _logger;
		this.topN = _topN;
		this.skipN = _skipN;
		this.comparator = _comparator != null ? _comparator : Comparator.naturalOrder();
		this.prettyPrinter = _prettyPrinter != null ? _prettyPrinter : t -> t.toString();
	}

	public EvolutionListenerLogTopN(final Logger _logger, final int _topN, final int _skipN) {
		this(_logger, _topN, _skipN, null, null);
	}

	@Override
	public void onEvolution(final long generation, final List<Genotype> population, final List<T> fitness,
			final boolean isDone) {
		if (skipN > 0 && generation % skipN != 0) {
			return;
		}

		logger.info("Top {} individuals at generation {}", topN, generation);
		IntStream.range(0, fitness.size())
				.boxed()
				.sorted((a, b) -> comparator.reversed().compare(fitness.get(a), fitness.get(b)))
				.limit(topN)
				.forEach((index) -> logger.info("  Fitness: {} -> {}",
						fitness.get(index),
						this.prettyPrinter.apply(population.get(index))));

	}
}