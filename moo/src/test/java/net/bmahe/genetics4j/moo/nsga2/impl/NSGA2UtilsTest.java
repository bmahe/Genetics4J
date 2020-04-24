package net.bmahe.genetics4j.moo.nsga2.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import net.bmahe.genetics4j.moo.FitnessVector;

public class NSGA2UtilsTest {
	final static public Logger logger = LogManager.getLogger(NSGA2UtilsTest.class);

	@Test
	public void simple() {
		final Comparator<FitnessVector<Integer>> dominance = Comparator.naturalOrder();

		final FitnessVector<Integer> fv1 = new FitnessVector<Integer>(2, 4);
		final FitnessVector<Integer> fv2 = new FitnessVector<Integer>(3, 5);
		final FitnessVector<Integer> fv3 = new FitnessVector<Integer>(2, 5);
		final FitnessVector<Integer> fv4 = new FitnessVector<Integer>(6, 6);
		final List<FitnessVector<Integer>> fitnessScore = List.of(fv1, fv2, fv3, fv4);
		final List<Set<Integer>> rankedPopulation = NSGA2Utils.rankedPopulation(dominance, fitnessScore);

		logger.info("fitnessScore: {}", fitnessScore);
		logger.info("Ranked Population: {}", rankedPopulation);

		assertNotNull(rankedPopulation);
		assertEquals(5, rankedPopulation.size());
		assertTrue(rankedPopulation.get(0).contains(3));
		assertTrue(rankedPopulation.get(1).contains(1));
		assertTrue(rankedPopulation.get(2).contains(2));
		assertTrue(rankedPopulation.get(3).contains(0));
		assertTrue(rankedPopulation.get(4).isEmpty());
	}

	@Test
	public void simple2() {
		final FitnessVector<Integer> fv1 = new FitnessVector<Integer>(5, 4);
		final FitnessVector<Integer> fv2 = new FitnessVector<Integer>(3, 5);
		final FitnessVector<Integer> fv3 = new FitnessVector<Integer>(4, 5);
		final FitnessVector<Integer> fv4 = new FitnessVector<Integer>(0, 0);
		final FitnessVector<Integer> fv5 = new FitnessVector<Integer>(2, 5);
		final FitnessVector<Integer> fv6 = new FitnessVector<Integer>(6, 6);
		final List<FitnessVector<Integer>> fitnessScore = List.of(fv1, fv2, fv3, fv4, fv5, fv6);

		final double[] crowdingDistanceAssignment = NSGA2Utils.crowdingDistanceAssignment(2,
				fitnessScore,
				(m) -> Comparator.naturalOrder(),
				(a, b, m) -> b.get(m) - a.get(m));

		logger.info("fitnessScore: {}", fitnessScore);
		logger.info("CrowdingDistanceAssignment: {}", crowdingDistanceAssignment);

	}

}