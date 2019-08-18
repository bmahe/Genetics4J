package net.bmahe.genetics4j.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleEvolutionListener implements EvolutionListener {
	final static public Logger logger = LogManager.getLogger(SimpleEvolutionListener.class);

	@Override
	public void onEvolution(final long generation, final Genotype[] population, final double[] fitness) {

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double sum = 0.0d;

		Genotype bestGenotype = null;
		for (int i = 0; i < fitness.length; i++) {
			double score = fitness[i];

			if (score > max) {
				max = score;
				bestGenotype = population[i];
			}

			if (score < min) {
				min = score;
			}

			sum += score;
		}

		final double average = sum / fitness.length;

		logger.info("Generation: {} - Average fitness: {} - Min fitness: {} - Max fitness: {}", generation, average, min,
				max);
	}
}