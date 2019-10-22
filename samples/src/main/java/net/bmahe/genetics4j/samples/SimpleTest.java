package net.bmahe.genetics4j.samples;

import java.util.Arrays;

import net.bmahe.genetics4j.core.GeneticSystem;
import net.bmahe.genetics4j.core.GeneticSystemFactory;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.GenotypeSpec.Builder;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.PartialMutation;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class SimpleTest {

	public static void main(String[] args) {
		System.out.println("XXXXXXXXXXXX");

		final Builder genotypeSpecBuilder = new GenotypeSpec.Builder();
		genotypeSpecBuilder.chromosomeSpecs(IntChromosomeSpec.of(10, 0, 10))
				.parentSelectionPolicy(RouletteWheelSelection.build())
				.survivorSelectionPolicy(TournamentSelection.build(30))
				.offspringRatio(0.7d)
				.combinationPolicy(MultiPointCrossover.of(2))
				.mutationPolicies(RandomMutation.of(0.15), PartialMutation.of(0, RandomMutation.of(0.05)))
				.optimization(Optimization.MINIMIZE)
				.fitness((genoType) -> {
					final IntChromosome intChromosome = genoType.getChromosome(0, IntChromosome.class);
					double denominator = 0.0;
					for (int i = 0; i < intChromosome.getNumAlleles(); i++) {
						denominator += Math.abs(i - intChromosome.getAllele(i));
					}

					return denominator;
				})
				.termination((generation, population, fitness) -> {
					return Arrays.stream(fitness)
							.min()
							.orElseThrow() < 0.0001;
				});
		final GenotypeSpec genotypeSpec = genotypeSpecBuilder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		geneticSystemDescriptorBuilder.populationSize(100);

		final ImmutableGeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final GeneticSystemFactory geneticSystemFactory = new GeneticSystemFactory();
		final GeneticSystem geneticSystem = geneticSystemFactory.from(genotypeSpec, geneticSystemDescriptor);

		final EvolutionResult evolutionResult = geneticSystem.evolve();
		System.out.println("Best genotype: " + evolutionResult.bestGenotype());
	}
}