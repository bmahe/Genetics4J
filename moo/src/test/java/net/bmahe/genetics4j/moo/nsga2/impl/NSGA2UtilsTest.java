package net.bmahe.genetics4j.moo.nsga2.impl;

import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.moo.FitnessVector;

public class NSGA2UtilsTest {
	final static public Logger logger = LogManager.getLogger(NSGA2UtilsTest.class);

	@Test
	public void simple() {
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