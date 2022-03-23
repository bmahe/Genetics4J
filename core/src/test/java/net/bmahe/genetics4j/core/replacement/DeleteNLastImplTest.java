package net.bmahe.genetics4j.core.replacement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.factory.IntChromosomeFactory;
import net.bmahe.genetics4j.core.selection.Selector;
import net.bmahe.genetics4j.core.spec.AbstractEAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.replacement.DeleteNLast;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;

public class DeleteNLastImplTest {
	final static public Logger logger = LogManager.getLogger(DeleteNLastImplTest.class);

	@Test
	public void ctorNullArgs() {
		assertThrows(NullPointerException.class, () -> new DeleteNLastImpl<>(null, null));
	}

	@Test
	public void ctorNullSpec() {
		final Selector<Double> mockSelector = mock(Selector.class);
		assertThrows(NullPointerException.class, () -> new DeleteNLastImpl<>(null, mockSelector));
	}

	@Test
	public void ctorNullSelector() {
		final DeleteNLast deleteNLast = DeleteNLast.of(0.2, RandomSelection.build());
		assertThrows(NullPointerException.class, () -> new DeleteNLastImpl<>(deleteNLast, null));
	}

	@Test
	public void select() {
		final Random random = new Random();
		final DeleteNLast deleteNLast = DeleteNLast.of(0.2, RandomSelection.build());

		final DeleteNLastImpl<Integer> deleteNLastImpl = new DeleteNLastImpl<>(deleteNLast, new Selector<Integer>() {

			@Override
			public Population<Integer> select(AbstractEAConfiguration<Integer> eaConfiguration, int numIndividuals,
					List<Genotype> population, List<Integer> fitnessScore) {
				return new Population<Integer>(population.subList(0, numIndividuals),
						fitnessScore.subList(0, numIndividuals));
			}
		});

		final IntChromosomeFactory intChromosomeFactory = new IntChromosomeFactory(random);

		final int populationSize = 10;
		final int offspringsScore = populationSize * 10;
		final Population<Integer> population = new Population<>();
		final Population<Integer> offsprings = new Population<>();
		for (int i = 0; i < populationSize; i++) {
			final Genotype individual = new Genotype(intChromosomeFactory.generate(IntChromosomeSpec.of(4, 0, 20)));
			population.add(individual, i % 2 == 0 ? i : -i);

			final Genotype offspring = new Genotype(intChromosomeFactory.generate(IntChromosomeSpec.of(4, 0, 20)));
			offsprings.add(offspring, offspringsScore);
		}

		logger.info("Population: {}", population);
		logger.info("\nOffsprings: {}", offsprings);

		final AbstractEAConfiguration<Integer> mockEAConfiguration = mock(AbstractEAConfiguration.class);
		when(mockEAConfiguration.optimization()).thenReturn(Optimization.MINIMIZE);

		final Population<Integer> nextGeneration = deleteNLastImpl.select(mockEAConfiguration,
				populationSize,
				population.getAllGenotypes(),
				population.getAllFitnesses(),
				offsprings.getAllGenotypes(),
				offsprings.getAllFitnesses());

		assertNotNull(nextGeneration);
		logger.info("\nNext Generation: {}", nextGeneration);

		assertEquals(populationSize, nextGeneration.size());
		assertTrue(nextGeneration.getAllGenotypes().containsAll(offsprings.getAllGenotypes().subList(0, 2)));
		assertFalse(nextGeneration.getAllGenotypes().contains(population.getGenotype(6)));
		assertFalse(nextGeneration.getAllGenotypes().contains(population.getGenotype(8)));

		for (int j = 0; j < populationSize; j++) {
			if (j != 6 && j != 8) {
				assertTrue(nextGeneration.getAllGenotypes().contains(population.getGenotype(j)));
			}
		}
	}
}