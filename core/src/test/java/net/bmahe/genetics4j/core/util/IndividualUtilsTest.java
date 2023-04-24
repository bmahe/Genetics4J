package net.bmahe.genetics4j.core.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;

public class IndividualUtilsTest {
	public static final Logger logger = LogManager.getLogger(IndividualUtilsTest.class);

	@Test
	public void fitnessBasedComparatorNoArg() {

		assertThrows(NullPointerException.class, () -> IndividualUtils.fitnessBasedComparator(null));

		final AbstractEAConfiguration<Integer> mockAEAConfiguration = mock(AbstractEAConfiguration.class);
	}

	@Test
	public void fitnessBasedComparatorMaximize() {

		final AbstractEAConfiguration<Integer> mockAEAConfiguration = mock(AbstractEAConfiguration.class);
		when(mockAEAConfiguration.optimization()).thenReturn(Optimization.MAXIMIZE);

		final Comparator<Individual<Integer>> comparator = IndividualUtils.fitnessBasedComparator(mockAEAConfiguration);

		final Individual<Integer> individualA = Individual
				.of(new Genotype(new IntChromosome(1, -10, 10, new int[] { 1 })), 100);
		final Individual<Integer> individualB = Individual
				.of(new Genotype(new IntChromosome(1, -10, 10, new int[] { 1 })), 10);

		assertTrue(comparator.compare(individualA, individualB) > 0);
		assertTrue(comparator.compare(individualA, individualA) == 0);
		assertTrue(comparator.compare(individualB, individualA) < 0);
	}

	@Test
	public void fitnessBasedComparatorMinimize() {

		final AbstractEAConfiguration<Integer> mockAEAConfiguration = mock(AbstractEAConfiguration.class);
		when(mockAEAConfiguration.optimization()).thenReturn(Optimization.MINIMIZE);

		final Comparator<Individual<Integer>> comparator = IndividualUtils.fitnessBasedComparator(mockAEAConfiguration);

		final Individual<Integer> individualA = Individual
				.of(new Genotype(new IntChromosome(1, -10, 10, new int[] { 1 })), 100);
		final Individual<Integer> individualB = Individual
				.of(new Genotype(new IntChromosome(1, -10, 10, new int[] { 1 })), 10);

		assertTrue(comparator.compare(individualA, individualB) < 0);
		assertTrue(comparator.compare(individualA, individualA) == 0);
		assertTrue(comparator.compare(individualB, individualA) > 0);
	}
}