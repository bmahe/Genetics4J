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
import java.util.stream.Stream;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Population;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;
import net.bmahe.genetics4j.core.termination.Terminations;

public class RandomSelectionPolicyHandlerTest {
	private final EAConfiguration<Double> SIMPLE_MAXIMIZING_EA_CONFIGURATION = new EAConfiguration.Builder<Double>()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelectionPolicy.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new RandomSelectionPolicyHandler<Double>(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireSelection() {
		final RandomSelectionPolicyHandler<Double> selectionPolicyHandler = new RandomSelectionPolicyHandler<>(
				new Random());

		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final RandomSelectionPolicyHandler<Double> selectionPolicyHandler = new RandomSelectionPolicyHandler<>(
				new Random());

		assertTrue(selectionPolicyHandler.canHandle(RandomSelectionPolicy.build()));
		assertFalse(selectionPolicyHandler.canHandle(TournamentSelection.of(2)));
	}

	@Test
	public void select() {

		final int numRequestedSelection = 100;
		final int evenIndex = 2;
		final int oddIndex = 3;

		final Random random = mock(Random.class);
		when(random.nextInt(anyInt())).thenReturn(2,
				Stream.iterate(1, i -> i + 1)
						.limit(numRequestedSelection)
						.map(i -> i % 2 == 0 ? evenIndex : oddIndex)
						.toArray((s) -> new Integer[s]));

		final int populationSize = 5;
		final List<Genotype> population = new ArrayList<Genotype>(populationSize);
		final List<Double> fitnessScore = new ArrayList<>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population.add(genotype);
			fitnessScore.add((double) i);
		}

		final EAExecutionContext<Double> eaExecutionContext = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(100)
				.build();
		final SelectionPolicyHandlerResolver<Double> selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver<>(
				eaExecutionContext);

		final RandomSelectionPolicyHandler<Double> selectionPolicyHandler = new RandomSelectionPolicyHandler<>(random);
		final Selector<Double> selector = selectionPolicyHandler.resolve(eaExecutionContext,
				SIMPLE_MAXIMIZING_EA_CONFIGURATION,
				selectionPolicyHandlerResolver,
				RandomSelectionPolicy.build());
		final Population<Double> selected = selector
				.select(SIMPLE_MAXIMIZING_EA_CONFIGURATION, 100, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(100, selected.size());
		for (int i = 0; i < selected.size(); i++) {
			final int expectedIndex = i % 2 == 0 ? evenIndex : oddIndex; // See values returned by the mocked random
			assertEquals(population.get(expectedIndex), selected.getGenotype(i));
		}
	}

}
