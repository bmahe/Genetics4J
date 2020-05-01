package net.bmahe.genetics4j.core.mutation;

import java.util.List;

import org.junit.Test;

import net.bmahe.genetics4j.core.GeneticSystem;
import net.bmahe.genetics4j.core.GeneticSystemFactory;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptors;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.GenotypeSpec.Builder;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.core.spec.selection.RandomSelectionPolicy;

public class SupersimpleTest {

	@Test
	public void simple() {

		final Builder<Double> genotypeSpecBuilder = new GenotypeSpec.Builder<Double>();
		genotypeSpecBuilder.chromosomeSpecs(BitChromosomeSpec.of(5), IntChromosomeSpec.of(6, 10, 100))
				.fitness((genotype) -> 1.0)
				.termination((long generation, Genotype[] population, List<Double> fitness) -> true)
				.parentSelectionPolicy(RandomSelectionPolicy.build())
				.survivorSelectionPolicy(RandomSelectionPolicy.build())
				.combinationPolicy(SinglePointCrossover.build())
				.addMutationPolicies(MultiMutations.of(RandomMutation.of(0.15), SwapMutation.of(0.05, 2, true)));

		final GenotypeSpec<Double> genotypeSpec = genotypeSpecBuilder.build();

		final GeneticSystemDescriptor<Double> geneticSystemDescriptor = GeneticSystemDescriptors
				.<Double>forScalarFitness()
				.populationSize(100)
				.build();

		final GeneticSystem<Double> geneticSystem = GeneticSystemFactory.from(genotypeSpec, geneticSystemDescriptor);
	}
}