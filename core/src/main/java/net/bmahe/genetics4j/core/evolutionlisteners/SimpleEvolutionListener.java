package net.bmahe.genetics4j.core.evolutionlisteners;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;

public class SimpleEvolutionListener<T extends Number> implements EvolutionListener<T> {
	final static public Logger logger = LogManager.getLogger(SimpleEvolutionListener.class);

	@Override
	public void onEvolution(final long generation, final List<Genotype> population, final List<T> fitness) {

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double sum = 0.0d;

		for (int i = 0; i < fitness.size(); i++) {
			double score = fitness.get(i).doubleValue();

			if (score > max) {
				max = score;
			}

			if (score < min) {
				min = score;
			}

			sum += score;
		}

		final double average = sum / fitness.size();

		logger.info("Generation: {} - Average fitness: {} - Min fitness: {} - Max fitness: {}",
				generation,
				average,
				min,
				max);
	}
}