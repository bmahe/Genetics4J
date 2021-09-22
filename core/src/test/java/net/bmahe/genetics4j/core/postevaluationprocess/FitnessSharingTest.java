package net.bmahe.genetics4j.core.postevaluationprocess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;

public class FitnessSharingTest {
	final static public Logger logger = LogManager.getLogger(FitnessSharingTest.class);

	final static public double EPSION = 0.001;

	@Test
	public void simple() {

		final FitnessSharing fitnessSharing = FitnessSharing.ofStandard((g1, g2) -> {
			final IntChromosome c1 = g1.getChromosome(0, IntChromosome.class);
			final IntChromosome c2 = g2.getChromosome(0, IntChromosome.class);

			return (double) Math.abs(c2.getAllele(0) - c1.getAllele(0));
		}, 10);

		assertNotNull(fitnessSharing);

		final Population<Double> population = new Population<>();
		for (int i = 0; i < 5; i++) {
			final Genotype genotype = new Genotype(new IntChromosome(1, 0, 100, new int[] { 5 }));

			population.add(genotype, (double) i);
		}

		logger.info("Population: {}", population);

		final Population<Double> population2 = fitnessSharing.apply(population);
		assertNotNull(population2);
		assertEquals(population.size(), population2.size());

		logger.info("Population with fitness sharing: {}", population2);
		for (int i = 0; i < population2.size(); i++) {
			assertEquals(population2.getFitness(i), population.getFitness(i) / population.size(), EPSION);
		}
	}
}