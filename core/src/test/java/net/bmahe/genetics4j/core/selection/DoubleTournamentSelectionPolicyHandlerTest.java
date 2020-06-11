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
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.DoubleTournament;
import net.bmahe.genetics4j.core.spec.selection.ImmutableDoubleTournament;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheel;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Terminations;

public class DoubleTournamentSelectionPolicyHandlerTest {

	private final EAConfiguration<Double> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Double>()
			.addChromosomeSpecs(BitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelection.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new DoubleTournamentSelectionPolicyHandler<Double>(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireSelection() {
		final DoubleTournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new DoubleTournamentSelectionPolicyHandler<>(
				new Random());

		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final DoubleTournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new DoubleTournamentSelectionPolicyHandler<>(
				new Random());

		assertTrue(selectionPolicyHandler.canHandle(DoubleTournament.<Double>of(Tournament.of(2), (a, b) -> 0, 2)));
		assertFalse(selectionPolicyHandler.canHandle(Tournament.of(2)));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelection.build()));
		assertFalse(selectionPolicyHandler.canHandle(RouletteWheel.build()));
	}

	@Test
	public void selectMaximize() {

		final Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.0);
		when(random.nextInt(anyInt())).thenReturn(2, 1, 0, 3, 4, 0, 1, 0); // We will select 2 genotypes with each
																			// tournament of
		// 2
		// candidates

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

		final DoubleTournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new DoubleTournamentSelectionPolicyHandler<>(
				random);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				DoubleTournament.<Double>of(Tournament.of(2), (a, b) -> Double.compare(a.fitness(), b.fitness()), 2));
		final Population<Double> selected = selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 2, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(2, selected.size());
		assertEquals(population.get(2), selected.getGenotype(0));
		assertEquals(population.get(1), selected.getGenotype(1));
	}

	@Test
	public void selectMaximizeAndDoFitnessLast() {

		final Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.0);
		when(random.nextInt(anyInt())).thenReturn(2, 1, 0, 3, 4, 0, 1, 2); // We will select 2 genotypes with each
																			// tournament of
		// 2
		// candidates

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

		final DoubleTournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new DoubleTournamentSelectionPolicyHandler<>(
				random);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				ImmutableDoubleTournament.<Double>builder()
						.doFitnessFirst(false)
						.fitnessTournament(Tournament.of(2))
						.parsimonyComparator((a, b) -> Double.compare(a.fitness(), b.fitness()))
						.parsimonyTournamentSize(2)
						.build());
		final Population<Double> selected = selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 2, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(2, selected.size());
		assertEquals(population.get(1), selected.getGenotype(0));
		assertEquals(population.get(1), selected.getGenotype(1));
	}

	@Test
	public void selectMinimize() {

		final Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.0);
		when(random.nextInt(anyInt())).thenReturn(2, 1, 0, 3, 4, 0, 1, 0); // We will select 2 genotypes with each
																			// tournament of
		// 2
		// candidates

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

		final DoubleTournamentSelectionPolicyHandler<Double> selectionPolicyHandler = new DoubleTournamentSelectionPolicyHandler<>(
				random);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				eaConfiguration,
				selectionPolicyHandlerResolver,
				DoubleTournament.<Double>of(Tournament.of(2), (a, b) -> Double.compare(a.fitness(), b.fitness()), 2));
		final Population<Double> selected = selector.select(eaConfiguration, 2, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(2, selected.size());
		assertEquals(population.get(0), selected.getGenotype(0));
		assertEquals(population.get(0), selected.getGenotype(1));
	}
}