package net.bmahe.genetics4j.samples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.bmahe.genetics4j.core.GeneticSystem;
import net.bmahe.genetics4j.core.GeneticSystemFactory;
import net.bmahe.genetics4j.core.Terminations;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.chromosomes.factory.ChromosomeFactory;
import net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.ImmutableGenotypeSpec;
import net.bmahe.genetics4j.core.spec.ImmutableGenotypeSpec.Builder;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.ChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.ImmutableOrderCrossover;
import net.bmahe.genetics4j.core.spec.mutation.ImmutableSwapMutation;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class TSVExample {

	public static class Position {
		public final int x;
		public final int y;

		public Position(final int _x, final int _y) {
			this.x = _x;
			this.y = _y;
		}
	}

	public static double distance(final Position pos1, final Position pos2) {
		final double xs = (pos2.x - pos1.x) * (pos2.x - pos1.x);
		final double ys = (pos2.y - pos1.y) * (pos2.y - pos1.y);

		return Math.sqrt(xs + ys);
	}

	public static void main(String[] args) {
		System.out.println("XXXXXXXXXXXX");

		final Random random = new Random();

		final int numCities = 200;
		final List<Position> cities = new ArrayList<TSVExample.Position>();
		for (int i = 0; i < numCities; i++) {
			cities.add(new Position(random.nextInt(10000), random.nextInt(10000)));
		}
		//@formatter:off
		final Builder genotypeSpecBuilder = ImmutableGenotypeSpec.builder();
		genotypeSpecBuilder
				.chromosomeSpecs(Arrays.asList(ImmutableIntChromosomeSpec.of(numCities, 0, numCities)))
				.parentSelectionPolicy(RouletteWheelSelection.build())
				.survivorSelectionPolicy(TournamentSelection.build(15))
				.offspringRatio(0.7d)
				.combinationPolicy(ImmutableOrderCrossover.builder().build())
				.mutationPolicies(Arrays.asList(ImmutableSwapMutation.of(0.10, 1)))
				.optimization(Optimization.MINIMIZE)
				.fitness((genoType) -> {
					final IntChromosome intChromosome = genoType.getChromosome(0, IntChromosome.class);
					double cost = 0.0;
					for (int i = 0; i < intChromosome.getNumAlleles()-1; i++) {
						final Position city1 = cities.get(intChromosome.getAllele(i));
						final Position city2 = cities.get(intChromosome.getAllele(i+1));
						
						cost += distance(city1, city2);
					}
					
					return cost;
				})
				.termination(Terminations.ofMaxGeneration(100_000));
		//@formatter:on
		final GenotypeSpec genotypeSpec = genotypeSpecBuilder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(200);

		final net.bmahe.genetics4j.core.chromosomes.factory.ImmutableChromosomeFactoryProvider.Builder chromosomeFactoryProviderBuilder = ImmutableChromosomeFactoryProvider
				.builder();
		chromosomeFactoryProviderBuilder.random(random);
		chromosomeFactoryProviderBuilder.chromosomeFactories(Arrays.asList(new ChromosomeFactory<IntChromosome>() {

			@Override
			public boolean canHandle(ChromosomeSpec chromosomeSpec) {
				return chromosomeSpec instanceof IntChromosomeSpec;
			}

			@Override
			public IntChromosome generate(ChromosomeSpec chromosomeSpec) {

				final IntChromosomeSpec intChromosomeSpec = (IntChromosomeSpec) chromosomeSpec;
				final int[] values = random.ints(intChromosomeSpec.minValue(), intChromosomeSpec.maxValue()).distinct()
						.limit(intChromosomeSpec.size()).toArray();

				return new IntChromosome(intChromosomeSpec.size(), intChromosomeSpec.minValue(),
						intChromosomeSpec.maxValue(), values);
			}
		}));
		geneticSystemDescriptorBuilder.chromosomeFactoryProvider(chromosomeFactoryProviderBuilder.build());

		final ImmutableGeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final GeneticSystemFactory geneticSystemFactory = new GeneticSystemFactory();
		final GeneticSystem geneticSystem = geneticSystemFactory.from(genotypeSpec, geneticSystemDescriptor);

		final EvolutionResult evolutionResult = geneticSystem.evolve();
		System.out.println("Best genotype: " + evolutionResult.bestGenotype());
	}
}