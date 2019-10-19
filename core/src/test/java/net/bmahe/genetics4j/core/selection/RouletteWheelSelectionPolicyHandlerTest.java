package net.bmahe.genetics4j.core.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Terminations;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableBitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class RouletteWheelSelectionPolicyHandlerTest {
	private final GenotypeSpec SIMPLE_MAXIMIZING_GENOTYPE_SPEC = new GenotypeSpec.Builder()
			.addChromosomeSpecs(ImmutableBitChromosomeSpec.of(3))
			.parentSelectionPolicy(RandomSelectionPolicy.build())
			.survivorSelectionPolicy(RandomSelectionPolicy.build())
			.combinationPolicy(SinglePointCrossover.build())
			.fitness((genoType) -> genoType.hashCode() / Double.MAX_VALUE * 10.0)
			.termination(Terminations.ofMaxGeneration(100))
			.build();

	@Test(expected = NullPointerException.class)
	public void randomIsRequired() {
		new RouletteWheelSelectionPolicyHandler(null);
	}

	@Test(expected = NullPointerException.class)
	public void canHandleRequireSelection() {
		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				new Random());

		selectionPolicyHandler.canHandle(null);
	}

	@Test
	public void canHandle() {
		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				new Random());

		assertTrue(selectionPolicyHandler.canHandle(RouletteWheelSelection.build()));
		assertFalse(selectionPolicyHandler.canHandle(RandomSelectionPolicy.build()));
		assertFalse(selectionPolicyHandler.canHandle(TournamentSelection.build(2)));
	}

	@Test
	public void selectMaximizing() {

		final Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.1, 0.6, 0.8);

		final int populationSize = 5;
		final Genotype[] population = new Genotype[populationSize];
		final double[] fitnessScore = new double[populationSize];

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population[i] = genotype;
			fitnessScore[i] = i * 10;
		}

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(100);

		final ImmutableGeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();
		final SelectionPolicyHandlerResolver selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver(
				geneticSystemDescriptor);

		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				random);
		final Selector selector = selectionPolicyHandler.resolve(geneticSystemDescriptor, SIMPLE_MAXIMIZING_GENOTYPE_SPEC,
				selectionPolicyHandlerResolver, RouletteWheelSelection.build());

		final List<Genotype> selected = selector.select(SIMPLE_MAXIMIZING_GENOTYPE_SPEC, 3, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(3, selected.size());
		assertEquals(population[1], selected.get(0));
		assertEquals(population[3], selected.get(1));
		assertEquals(population[4], selected.get(2));
	}

	@Test
	public void selectMinimizing() {

		final Random random = mock(Random.class);
		when(random.nextDouble()).thenReturn(0.1, 0.6, 0.8);

		final int populationSize = 5;
		final Genotype[] population = new Genotype[populationSize];
		final double[] fitnessScore = new double[populationSize];

		for (int i = 0; i < populationSize; i++) {
			final IntChromosome intChromosome = new IntChromosome(4, 0, 10, new int[] { i, i + 1, i + 2, i + 3 });
			final Genotype genotype = new Genotype(new Chromosome[] { intChromosome });

			population[i] = genotype;
			fitnessScore[i] = i * 10;
		}

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(100);

		final ImmutableGeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();
		final SelectionPolicyHandlerResolver selectionPolicyHandlerResolver = new SelectionPolicyHandlerResolver(
				geneticSystemDescriptor);

		final GenotypeSpec genotypeSpec = new GenotypeSpec.Builder().from(SIMPLE_MAXIMIZING_GENOTYPE_SPEC)
				.optimization(Optimization.MINIMIZE)
				.build();
		final RouletteWheelSelectionPolicyHandler selectionPolicyHandler = new RouletteWheelSelectionPolicyHandler(
				random);
		final Selector selector = selectionPolicyHandler.resolve(geneticSystemDescriptor, SIMPLE_MAXIMIZING_GENOTYPE_SPEC,
				selectionPolicyHandlerResolver, RouletteWheelSelection.build());
		final List<Genotype> selected = selector.select(genotypeSpec, 3, population, fitnessScore);

		assertNotNull(selected);
		assertEquals(3, selected.size());
		assertEquals(population[0], selected.get(0));
		assertEquals(population[1], selected.get(1));
		assertEquals(population[2], selected.get(2));
	}
}