package net.bmahe.genetics4j.core.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;
import net.bmahe.genetics4j.core.termination.Terminations;

public class TournamentSelectionPolicyHandlerTest {

	private final EAConfiguration<Double> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Double>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelectionPolicy.build())
			.survivorSelectionPolicy(RandomSelectionPolicy.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new TournamentSelectionPolicyHandler<Double>(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireSelection() {
		final TournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new TournamentSelectionPolicyHandler<>(
				new Random());

		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final TournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new TournamentSelectionPolicyHandler<>(
				new Random());

		assertTrue(selectionPolicyHandler.canHandle(TournamentSelection.build(2)));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelectionPolicy.build()));
		assertFalse(selectionPolicyHandler.canHandle(RouletteWheelSelection.build()));
	}

	@Test
	public void selectMaximize() {

		final Random random = mock(Random.class);
		when(random.nextInt(anyInt())).thenReturn(2, 1, 0, 3, 4); // We will select 2 genotypes with each tournament of
																	// 2
																	// candidates

		final int populationSize = 5;
		final Genotype[] population = new Genotype[populationSize];
		final List<Double> fitnessScore = new ArrayList<>(populationSize);

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population[i] = genotype;
			fitnessScore.add((double) i * 10);
		}

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();

		final SelectionPolicyHandlerResolver<Double> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final TournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new TournamentSelectionPolicyHandler<>(
				random);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				TournamentSelection.build(2));
		final List<Genotype> selected = selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 2, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(2, selected.size());
		assertEquals(population[2], selected.get(0));
		assertEquals(population[3], selected.get(1));
	}

	@Test
	public void selectMinimize() {

		final Random random = mock(Random.class);
		when(random.nextInt(anyInt())).thenReturn(2, 1, 0, 3, 4); // We will select 2 genotypes with each tournament of
																	// 2
																	// candidates

		final int populationSize = 5;
		final Genotype[] population = new Genotype[populationSize];
		final List<Double> fitnessScore = new ArrayList<>(populationSize);

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population[i] = genotype;
			fitnessScore.add((double) i * 10);
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

		final TournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new TournamentSelectionPolicyHandler<>(
				random);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				TournamentSelection.build(2));
		final List<Genotype> selected = selector.select(eaConfiguration, 2, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(2, selected.size());
		assertEquals(population[1], selected.get(0));
		assertEquals(population[0], selected.get(1));
	}
}