package net.bmahe.genetics4j.core.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
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
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.MultiTournaments;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheel;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Terminations;

public class MultiTournamentsSelectionPolicyHandlerTest {

	private final EAConfiguration<Double> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Double>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelection.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new MultiTournamentsSelectionPolicyHandler<Double>(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireSelection() {
		final MultiTournamentsSelectionPolicyHandler<Double> selectionPolicyHandler = new MultiTournamentsSelectionPolicyHandler<>(
				new Random());

		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final MultiTournamentsSelectionPolicyHandler<Double> selectionPolicyHandler = new MultiTournamentsSelectionPolicyHandler<>(
				new Random());

		assertTrue(selectionPolicyHandler.canHandle(MultiTournaments.<Double>of(Tournament.of(2), Tournament.of(3))));
		assertFalse(selectionPolicyHandler.canHandle(Tournament.of(2)));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelection.build()));
		assertFalse(selectionPolicyHandler.canHandle(RouletteWheel.build()));
	}

	@Test
	public void selectMaxThenMin() {

		final Random random = mock(Random.class);
		when(random.ints(anyInt(), anyInt())).thenCallRealMethod();
		when(random.nextInt(anyInt())).thenReturn(2, 1, 0, 3, 4, 3, 1, 0); // We will select 2 genotypes with each
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

		final MultiTournamentsSelectionPolicyHandler<Double> selectionPolicyHandler = new MultiTournamentsSelectionPolicyHandler<>(
				random);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				MultiTournaments.<Double>of(Tournament.of(2),
						Tournament.of(2, Comparator.comparing(Individual<Double>::fitness).reversed())));
		final Population<Double> selected = selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 2, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(2, selected.size());
		assertEquals(population.get(2), selected.getGenotype(0));
		assertEquals(population.get(1), selected.getGenotype(1));
	}
}