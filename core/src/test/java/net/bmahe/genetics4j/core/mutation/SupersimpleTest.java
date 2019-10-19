package net.bmahe.genetics4j.core.mutation;

import org.junit.Test;

import net.bmahe.genetics4j.core.GeneticSystem;
import net.bmahe.genetics4j.core.GeneticSystemFactory;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.spec.GeneticSystemDescriptor;
import net.bmahe.genetics4j.core.spec.GenotypeSpec;
import net.bmahe.genetics4j.core.spec.GenotypeSpec.Builder;
import net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor;
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

		final Builder genotypeSpecBuilder = new GenotypeSpec.Builder();
		genotypeSpecBuilder.chromosomeSpecs(BitChromosomeSpec.of(5), IntChromosomeSpec.of(6, 10, 100))
				.fitness((genotype) -> 1.0)
				.termination((long generation, Genotype[] population, double[] fitness) -> true)
				.parentSelectionPolicy(RandomSelectionPolicy.build())
				.survivorSelectionPolicy(RandomSelectionPolicy.build())
				.combinationPolicy(SinglePointCrossover.build())
				.addMutationPolicies(MultiMutations.of(RandomMutation.of(0.15), SwapMutation.of(0.05, 2)));

		final GenotypeSpec genotypeSpec = genotypeSpecBuilder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableGeneticSystemDescriptor.Builder geneticSystemDescriptorBuilder = ImmutableGeneticSystemDescriptor
				.builder();
		GeneticSystemDescriptor geneticSystemDescriptor = geneticSystemDescriptorBuilder.build();

		final GeneticSystemFactory geneticSystemFactory = new GeneticSystemFactory();
		final GeneticSystem geneticSystem = geneticSystemFactory.from(genotypeSpec, geneticSystemDescriptor);
	}
}