package net.bmahe.genetics4j.core.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Individual;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.ProportionalTournament;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheel;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Terminations;

public class ProportionalTournamentSelectionPolicyHandlerTest {

	private final EAConfiguration<Double> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Double>()
			.addChromosomeSpecs(BitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelection.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new ProportionalTournamentSelectionPolicyHandler<Double>(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireSelection() {
		final ProportionalTournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new ProportionalTournamentSelectionPolicyHandler<>(
				new Random());

		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final ProportionalTournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new ProportionalTournamentSelectionPolicyHandler<>(
				new Random());

		assertTrue(selectionPolicyHandler.canHandle(ProportionalTournament.<Double>of(2,
				0.5,
				Comparator.comparingDouble(Individual::fitness),
				Comparator.comparingDouble(Individual::fitness))));
		assertFalse(selectionPolicyHandler.canHandle(Tournament.of(2)));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelection.build()));
		assertFalse(selectionPolicyHandler.canHandle(RouletteWheel.build()));
	}

	@Test
	public void selectMaximize() {

		final Random mockRandom = mock(Random.class);
		when(mockRandom.nextDouble()).thenReturn(0.1, 0.8);
		when(mockRandom.nextInt(anyInt())).thenReturn(2, 1, 0, 3, 4, 0, 1, 3); // We will select 2 genotypes with each
																				// tournament of 2 candidates
		when(mockRandom.ints(anyLong(), anyInt(), anyInt())).thenCallRealMethod();

		final int populationSize = 5;
		final List<Genotype> population = new ArrayList<Genotype>(populationSize);
		final List<Double> fitnessScore = new ArrayList<>(populationSize);

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population.add(genotype);
			fitnessScore.add((double) i * 10);
		}

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();

		final SelectionPolicyHandlerResolver<Double> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final ProportionalTournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new ProportionalTournamentSelectionPolicyHandler<>(
				mockRandom);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				ProportionalTournament.<Double>of(2,
						0.5,
						Comparator.comparingDouble(Individual::fitness),
						Comparator.comparingDouble(Individual<Double>::fitness).reversed()));
		final Population<Double> selected = selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 2, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(2, selected.size());
		assertEquals(population.get(2), selected.getGenotype(0));
		assertEquals(population.get(0), selected.getGenotype(1));

		final Population<Double> selected2 = selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 2, population, fitnessScore);

		assertNotNull(selected2);
		assertEquals(2, selected2.size());
		assertEquals(population.get(0), selected2.getGenotype(0));
		assertEquals(population.get(1), selected2.getGenotype(1));

	}

	@Test
	public void selectMinimize() {

		final Random mockRandom = mock(Random.class);
		when(mockRandom.nextDouble()).thenReturn(0.1, 0.8);
		when(mockRandom.nextInt(anyInt())).thenReturn(2, 1, 0, 3, 4, 0, 1, 3); // We will select 2 genotypes with each
																				// tournament of 2 candidates
		when(mockRandom.ints(anyLong(), anyInt(), anyInt())).thenCallRealMethod();

		final int populationSize = 5;
		final List<Genotype> population = new ArrayList<Genotype>(populationSize);
		final List<Double> fitnessScore = new ArrayList<>(populationSize);

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population.add(genotype);
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

		final ProportionalTournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new ProportionalTournamentSelectionPolicyHandler<>(
				mockRandom);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				eaConfiguration,
				selectionPolicyHandlerResolver,
				ProportionalTournament.<Double>of(2,
						0.5,
						Comparator.comparingDouble(Individual::fitness),
						Comparator.comparingDouble(Individual::fitness)));
		final Population<Double> selected = selector.select(eaConfiguration, 2, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(2, selected.size());
		assertEquals(population.get(1), selected.getGenotype(0));
		assertEquals(population.get(0), selected.getGenotype(1));

		final Population<Double> selected2 = selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 2, population, fitnessScore);

		assertNotNull(selected2);
		assertEquals(2, selected2.size());
		assertEquals(population.get(4), selected2.getGenotype(0));
		assertEquals(population.get(3), selected2.getGenotype(1));
	}
}