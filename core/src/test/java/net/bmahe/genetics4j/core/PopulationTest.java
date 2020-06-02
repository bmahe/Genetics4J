package net.bmahe.genetics4j.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import net.bmahe.genetics4j.core.chromosomes.IntChromosome;

public class PopulationTest {

	@Test(expected = NullPointerException.class)
	public void ctorNoGenotype() {
		new Population<Integer>(null, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void ctorNoFitness() {
		new Population<Integer>(Collections.emptyList(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorDifferentSizes() {
		new Population<Integer>(Collections.emptyList(), List.of(1));
	}

	@Test
	public void empty() {
		final Population<Integer> population = new Population<>();
		assertTrue(population.isEmpty());
	}

	@Test
	public void simple() {

		final List<Genotype> initialGenotypes = new ArrayList<>();
		final List<Integer> initialFitnesses = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			final Genotype genotype = new Genotype(new IntChromosome(1, 0, 100, new int[] { i }));
			initialGenotypes.add(genotype);
			initialFitnesses.add(i);
		}

		final Population<Integer> population = new Population<>(initialGenotypes, initialFitnesses);
		assertFalse(population.isEmpty());
		assertEquals(5, population.size());
		assertEquals(initialGenotypes, population.getAllGenotypes());
		assertEquals(initialFitnesses, population.getAllFitnesses());

		assertNotNull(population.getGenotype(2));
		assertEquals(initialGenotypes.get(3), population.getGenotype(3));

		assertNotNull(population.getFitness(2));
		assertEquals(initialFitnesses.get(2), population.getFitness(2));

		for (int i = 5; i < 15; i++) {
			final Genotype genotype = new Genotype(new IntChromosome(1, 0, 100, new int[] { i }));
			population.add(genotype, i);
		}
		assertEquals(15, population.size());
		assertFalse(population.isEmpty());

		final Population<Integer> population2 = Population.of(initialGenotypes, initialFitnesses);
		assertEquals(5, population2.size());
		assertNotEquals(population, population2);
	}
}