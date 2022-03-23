package net.bmahe.genetics4j.moo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class ParetoUtilsTest {
	final static public Logger logger = LogManager.getLogger(ParetoUtilsTest.class);

	@Test
	public void simple() {
		final Comparator<FitnessVector<Integer>> dominance = Comparator.naturalOrder();

		final FitnessVector<Integer> fv1 = new FitnessVector<Integer>(2, 4);
		final FitnessVector<Integer> fv2 = new FitnessVector<Integer>(3, 5);
		final FitnessVector<Integer> fv3 = new FitnessVector<Integer>(2, 5);
		final FitnessVector<Integer> fv4 = new FitnessVector<Integer>(6, 6);
		final List<FitnessVector<Integer>> fitnessScore = List.of(fv1, fv2, fv3, fv4);
		final List<Set<Integer>> rankedPopulation = ParetoUtils.rankedPopulation(dominance, fitnessScore);

		logger.info("fitnessScore: {}", fitnessScore);
		logger.info("Ranked Population: {}", rankedPopulation);

		assertNotNull(rankedPopulation);
		assertEquals(5, rankedPopulation.size());
		assertTrue(rankedPopulation.get(0)
				.contains(3));
		assertTrue(rankedPopulation.get(1)
				.contains(1));
		assertTrue(rankedPopulation.get(2)
				.contains(2));
		assertTrue(rankedPopulation.get(3)
				.contains(0));
		assertTrue(rankedPopulation.get(4)
				.isEmpty());
	}

	@Test
	public void simple2() {
		final Comparator<FitnessVector<Float>> dominance = Comparator.naturalOrder();

		final var fv1 = new FitnessVector<Float>(4f, 5.0201883f, 0.51985145f);
		final var fv2 = new FitnessVector<Float>(4f, 5.0217524f, 0.54367197f);
		final var fv3 = new FitnessVector<Float>(4f, 5.0219045f, 0.5535033f);
		final var fv4 = new FitnessVector<Float>(4f, 5.0860705f, 0.78280944f);
		final var fitnessScore = List.of(fv1, fv2, fv3, fv4);
		final var rankedPopulation = ParetoUtils.rankedPopulation(dominance, fitnessScore);

		logger.info("fitnessScore: {}", fitnessScore);
		logger.info("Ranked Population: {}", rankedPopulation);

		assertNotNull(rankedPopulation);
		assertEquals(5, rankedPopulation.size());
	}
}