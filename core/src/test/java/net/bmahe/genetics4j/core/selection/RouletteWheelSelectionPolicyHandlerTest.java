package net.bmahe.genetics4j.core.selection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheel;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Terminations;

public class RouletteWheelSelectionPolicyHandlerTest {
	private final EAConfiguration<Double> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Double>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelection.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test
	public void randomIsRequired() {
		assertThrows(NullPointerException.class, () -> new RouletteWheelSelectionPolicyHandler<Double>(null));
	}

	@Test
	public void canHandleRequireSelection() {
		final RouletteWheelSelectionPolicyHandler<Double> selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler<>(
				new Random());

		assertThrows(NullPointerException.class, () -> selectionPolicyHandler.canHandle(null));
	}

	@Test
	public void canHandle() {
		final RouletteWheelSelectionPolicyHandler<Double> selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler<>(
				new Random());

		assertTrue(selectionPolicyHandler.canHandle(RouletteWheel.build()));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelection.build()));
		assertFalse(selectionPolicyHandler.canHandle(Tournament.of(2)));
	}

	@Test
	public void selectMaximizing() {

		final RandomGenerator random = mock(RandomGenerator.class);
		when(random.nextDouble()).thenReturn(0.1, 0.6, 0.8);

		final int populationSize = 5;
		final List<Genotype> population = new ArrayList<Genotype>(populationSize);
		final List<Double> fitnessScore = new ArrayList<>(populationSize);

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population.add(genotype);
			fitnessScore.add((double) (i * 10));
		}

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();

		final SelectionPolicyHandlerResolver<Double> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final RouletteWheelSelectionPolicyHandler<Double> selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler<>(
				random);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				RouletteWheel.build());

		final Population<Double> selected = selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 3, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(3, selected.size());
		assertEquals(population.get(1), selected.getGenotype(0));
		assertEquals(population.get(3), selected.getGenotype(1));
		assertEquals(population.get(4), selected.getGenotype(2));
	}

	@Test
	public void selectMinimizing() {

		final RandomGenerator random = mock(RandomGenerator.class);
		when(random.nextDouble()).thenReturn(0.1, 0.6, 0.8);

		final int populationSize = 5;
		final List<Genotype> population = new ArrayList<Genotype>(populationSize);
		final List<Double> fitnessScore = new ArrayList<>(populationSize);

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population.add(genotype);
			fitnessScore.add((double) (i * 10));
		}

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();

		final SelectionPolicyHandlerResolver<Double> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final EAConfiguration<Double> eaConfiguration = new EAConfiguration.Builder<Double>()
				.from(SIMPLE_MAXIMIZING_EA_CONFIGURATION)
				.optimization(Optimization.MINIMIZE)
				.build();
		final RouletteWheelSelectionPolicyHandler<Double> selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler<>(
				random);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				RouletteWheel.build());
		final Population<Double> selected = selector.select(eaConfiguration, 3, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(3, selected.size());
		assertEquals(population.get(0), selected.getGenotype(0));
		assertEquals(population.get(1), selected.getGenotype(1));
		assertEquals(population.get(2), selected.getGenotype(2));
	}
}