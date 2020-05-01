package net.bmahe.genetics4j.core.evolutionlisteners;

import java.util.Comparator;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;

public class EvolutionListeners {

	private EvolutionListeners() {
	}

	public static <U extends Comparable<U>> EvolutionListener<U> ofLogTopN(final Logger logger, final int topN) {
		return ofLogTopN(logger, topN, 0);
	}

	public static <U extends Comparable<U>> EvolutionListener<U> ofLogTopN(final Logger logger, final int topN,
			final int skipN) {
		return new EvolutionListenerLogTopN<U>(logger, topN, skipN);
	}

	public static <U extends Comparable<U>> EvolutionListener<U> ofLogTopN(final Logger logger, final int topN,
			final Comparator<U> comparator) {
		return new EvolutionListenerLogTopN<U>(logger, topN, 0, comparator, null);
	}

	public static <U extends Comparable<U>> EvolutionListener<U> ofLogTopN(final Logger logger, final int topN,
			final Comparator<U> comparator, final Function<Genotype, String> prettyPrinter) {
		return new EvolutionListenerLogTopN<U>(logger, topN, 0, comparator, prettyPrinter);
	}

	public static <U extends Comparable<U>> EvolutionListener<U> ofLogTopN(final Logger logger, final int topN,
			final Function<Genotype, String> prettyPrinter) {
		return new EvolutionListenerLogTopN<U>(logger, topN, 0, null, prettyPrinter);
	}

	public static <U extends Comparable<U>> EvolutionListener<U> ofLogTopN(final Logger logger, final int topN,
			final int skipN, final Comparator<U> comparator) {
		return new EvolutionListenerLogTopN<U>(logger, topN, skipN, comparator, null);
	}

	public static <U extends Comparable<U>> EvolutionListener<U> ofLogTopN(final Logger logger, final int topN,
			final int skipN, final Comparator<U> comparator, final Function<Genotype, String> prettyPrinter) {
		return new EvolutionListenerLogTopN<U>(logger, topN, skipN, comparator, prettyPrinter);
	}

}