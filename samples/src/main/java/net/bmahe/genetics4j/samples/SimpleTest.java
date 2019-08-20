package net.bmahe.genetics4j.samples;

import java.util.Arrays;

import net.bmahe.genetics4j.core.GeneticSystem;
import net.bmahe.genetics4j.core.GeneticSystemFactory;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.ImmutableGenotypeSpec;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.ImmutableGenotypeSpec.Builder;
import net.bmahe.genetics4j.core.spec.chromosome.ImmutableIntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.ImmutableMultiPointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.ImmutableRandomMutation;
import net.bmahe.genetics4j.core.spec.selection.RouletteWheelSelection;
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;

public class SimpleTest {

	public static void main(String[] args) {
		System.out.println("XXXXXXXXXXXX");

		//@formatter:off
		final Builder genotypeSpecBuilder = ImmutableGenotypeSpec.builder();
		genotypeSpecBuilder
				.chromosomeSpecs(Arrays.asList(ImmutableIntChromosomeSpec.of(10, 0, 10)))
				.parentSelectionPolicy(RouletteWheelSelection.build())
				.survivorSelectionPolicy(TournamentSelection.build(30))
				.offspringRatio(0.7d)
				.combinationPolicy(ImmutableMultiPointCrossover.of(2))
				.mutationPolicies(Arrays.asList(ImmutableRandomMutation.of(0.15)))
				.optimization(Optimization.MINIMIZE)
				.fitness((genoType) -> {
					final IntChromosome intChromosome = genoType.getChromosome(0, IntChromosome.class);
					double denominator = 0.0;
					for (int i = 0; i < intChromosome.getNumAlleles(); i++) {
						denominator += Math.abs(i - intChromosome.getAllele(i));
					}
					
					return denominator;
				})
				.termination(
						(generation, population, fitness) -> {
							return Arrays.stream(fitness).min().orElseThrow() < 0.0001;
					});
		//@formatter:on
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