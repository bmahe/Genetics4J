package net.bmahe.genetics4j.core.combination.erx;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;

public class IntEdgeRecombinationCrossoverTest {
	final static public Logger logger = LogManager.getLogger(IntEdgeRecombinationCrossoverTest.class);

	@Test
	public void simple() {
		final Random random = new Random();
		final IntEdgeRecombinationCrossover intEdgeRecombinationCrossover = new IntEdgeRecombinationCrossover(random);

		final IntChromosome chromosome1 = new IntChromosome(6, 0, 5, new int[] { 0, 1, 2, 3, 4, 5 });
		final IntChromosome chromosome2 = new IntChromosome(6, 0, 5, new int[] { 5, 3, 2, 4, 1, 0 });

		logger.info("Chromosome 1: {}", chromosome1);
		logger.info("Chromosome 2: {}", chromosome2);
		final Chromosome combined = intEdgeRecombinationCrossover.combine(chromosome1, chromosome2);

		logger.info("Chromosome combined: {}", combined);
	}
}