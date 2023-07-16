package net.bmahe.genetics4j.neat.combination.parentcompare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.neat.Connection;
import net.bmahe.genetics4j.neat.chromosomes.NeatChromosome;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.FitnessComparison;
import net.bmahe.genetics4j.neat.spec.combination.parentcompare.FitnessThenSizeComparison;

public class FitnessComparisonHandlerTest {

	@Test
	public void canHandle() {

		final FitnessComparisonHandler fitnessComparisonHandler = new FitnessComparisonHandler();

		assertTrue(fitnessComparisonHandler.canHandle(FitnessComparison.build()));
		assertFalse(fitnessComparisonHandler.canHandle(FitnessThenSizeComparison.build()));
	}

	@Test
	public void compare() {

		final FitnessComparisonHandler fitnessComparisonHandler = new FitnessComparisonHandler();
		final FitnessComparison fitnessComparison = FitnessComparison.build();

		final var neatChromosomeA = new NeatChromosome(3,
				3,
				-10,
				10,
				List.of(Connection.of(0, 1, 0, false, 0),
						Connection.of(0, 2, 0, false, 1),
						Connection.of(0, 3, 0, false, 2),
						Connection.of(0, 4, 0, false, 3)));

		final var neatChromosomeB = new NeatChromosome(3,
				3,
				-10,
				10,
				List.of(Connection.of(0, 1, 0, false, 0),
						Connection.of(0, 2, 0, false, 1),
						Connection.of(0, 3, 0, false, 2),
						Connection.of(1, 5, 0, false, 2),
						Connection.of(0, 4, 0, false, 3)));

		assertThrows(NullPointerException.class,
				() -> fitnessComparisonHandler.compare(null, neatChromosomeA, neatChromosomeB, -1));
		assertThrows(NullPointerException.class,
				() -> fitnessComparisonHandler.compare(fitnessComparison, null, neatChromosomeB, -1));
		assertThrows(NullPointerException.class,
				() -> fitnessComparisonHandler.compare(fitnessComparison, neatChromosomeA, null, -1));

		final ChosenOtherChromosome compareLessThan = fitnessComparisonHandler
				.compare(fitnessComparison, neatChromosomeA, neatChromosomeB, -1);
		assertNotNull(compareLessThan);
		assertEquals(neatChromosomeA, compareLessThan.other());
		assertEquals(neatChromosomeB, compareLessThan.chosen());

		final ChosenOtherChromosome compareEqual = fitnessComparisonHandler
				.compare(fitnessComparison, neatChromosomeA, neatChromosomeB, 0);
		assertNotNull(compareEqual);
		assertEquals(neatChromosomeA, compareEqual.chosen());
		assertEquals(neatChromosomeB, compareEqual.other());

		final ChosenOtherChromosome compareMoreThan = fitnessComparisonHandler
				.compare(fitnessComparison, neatChromosomeA, neatChromosomeB, 1);
		assertNotNull(compareMoreThan);
		assertEquals(neatChromosomeA, compareMoreThan.chosen());
		assertEquals(neatChromosomeB, compareMoreThan.other());

	}
}