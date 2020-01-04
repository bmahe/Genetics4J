package net.bmahe.genetics4j.samples;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.bmahe.genetics4j.core.GeneticSystem;
import net.bmahe.genetics4j.core.GeneticSystemFactory;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.Terminations;
import net.bmahe.genetics4j.core.chromosomes.Chromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.GenotypeSpec.Builder;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.EdgeRecombinationCrossover;
import net.bmahe.genetics4j.core.spec.combination.MultiCombinations;
import net.bmahe.genetics4j.core.spec.combination.OrderCrossover;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.core.spec.selection.MultiSelections;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class TSVExample {

	public static double distance(final Position pos1, final Position pos2) {
		final double xs = (pos2.x - pos1.x) * (pos2.x - pos1.x);
		final double ys = (pos2.y - pos1.y) * (pos2.y - pos1.y);

		return Math.sqrt(xs + ys);
	}

	public static void main(String[] args) {
		System.out.println("XXXXXXXXXXXX " + Arrays.deepToString(args));

		final TSPLIBParser tsplibParser = new TSPLIBParser();
		TSPLIBProblem tsplibProblem = null;
		try {
			tsplibProblem = tsplibParser.parse(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		final Random random = new Random();

		final List<Position> cities = tsplibProblem.cities;
		final int numCities = cities.size();

		//@formatter:off
		final Builder genotypeSpecBuilder = new GenotypeSpec.Builder();
		genotypeSpecBuilder
				.chromosomeSpecs(IntChromosomeSpec.of(numCities, 0, numCities))
				.parentSelectionPolicy(MultiSelections.of(RouletteWheelSelection.build(), TournamentSelection.build(15)))
				.survivorSelectionPolicy(MultiSelections.of(RouletteWheelSelection.build(), TournamentSelection.build(15)))
				.offspringRatio(0.9d)
				.combinationPolicy(MultiCombinations.of(OrderCrossover.build(), EdgeRecombinationCrossover.build()))
				.mutationPolicies(MultiMutations.of(SwapMutation.of(0.05, 80, false)))
				.optimization(Optimization.MINIMIZE)
				.fitness((genoType) -> {
					final IntChromosome intChromosome = genoType.getChromosome(0, IntChromosome.class);

					final Position firstCity = cities.get(intChromosome.getAllele(0));
					final Position lastCity  = cities.get(intChromosome.getAllele(intChromosome.getNumAlleles()-1));
					double cost = distance(lastCity, firstCity); // need to account for going back to the starting position

					for (int i = 0; i < intChromosome.getNumAlleles()-1; i++) {
						final Position city1 = cities.get(intChromosome.getAllele(i));
						final Position city2 = cities.get(intChromosome.getAllele(i+1));

						cost += distance(city1, city2);
					}
					
					return cost;
				})
				.termination(Terminations.ofMaxGeneration(200_000))
				.populationGenerator(() -> {
					final int[] values = random.ints(0, numCities).distinct()
							.limit(numCities).toArray();

					final Chromosome chromosome = new IntChromosome(numCities, 0, numCities, values);
					return new Genotype(chromosome);
				});
		//@formatter:on
		final GenotypeSpec genotypeSpec = genotypeSpecBuilder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(1500);

		final ImmutableGeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final GeneticSystemFactory geneticSystemFactory = new GeneticSystemFactory();
		final GeneticSystem geneticSystem = geneticSystemFactory.from(genotypeSpec, geneticSystemDescriptor);

		final EvolutionResult evolutionResult = geneticSystem.evolve();
		System.out.println("Best genotype: " + evolutionResult.bestGenotype());
	}
}