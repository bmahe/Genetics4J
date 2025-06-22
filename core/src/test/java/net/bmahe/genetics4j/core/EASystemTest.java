package net.bmahe.genetics4j.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.chromosomes.BitChromosome;
import net.bmahe.genetics4j.core.chromosomes.IntChromosome;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.chromosome.BitChromosomeSpec;
import net.bmahe.genetics4j.core.spec.chromosome.IntChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Terminations;

public class EASystemTest {

	@Test
	@DisplayName("evolve() method should complete successfully with BitChromosome")
	void testEvolveWithBitChromosome() {

		final int maxGeneration = 3;
		final int populationSize = 20;

		// Create configuration for bit counting problem
		final EAConfiguration<Integer> config = new EAConfiguration.Builder<Integer>()
				.chromosomeSpecs(BitChromosomeSpec.of(8))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(SinglePointCrossover.build())
				.mutationPolicies(RandomMutation.of(0.1))
				.fitness(genotype -> {
					final BitChromosome chromosome = genotype.getChromosome(0, BitChromosome.class);
					return chromosome.getBitSet()
							.cardinality(); // Count of 1s
				})
				.termination(Terminations.ofMaxGeneration(maxGeneration))
				.build();

		final EAExecutionContext<Integer> context = EAExecutionContexts.<Integer>forScalarFitness()
				.populationSize(populationSize)
				.build();

		final EASystem<Integer> system = EASystemFactory.from(config, context);

		final EvolutionResult<Integer> result = system.evolve();

		// Verify successful evolution
		assertNotNull(result, "Evolution result should not be null");
		assertNotNull(result.bestGenotype(), "Best genotype should not be null");
		assertNotNull(result.bestFitness(), "Best fitness should not be null");
		assertEquals(maxGeneration, result.generation(), "Should run for 3 generations");
		assertNotNull(result.population(), "Population should not be null");
		assertEquals(populationSize,
				result.population()
						.size(),
				"Population size should be maintained");

		// Fitness should be in valid range (0-8 for 8-bit chromosome)
		assertTrue(result.bestFitness() >= 0 && result.bestFitness() <= 8, "Best fitness should be between 0 and 8");
	}

	@Test
	@DisplayName("evolve() method should complete immediately with termination")
	void testEvolveWithImmediateTermination() {
		// Configuration that terminates immediately
		final EAConfiguration<Double> config = new EAConfiguration.Builder<Double>()
				.chromosomeSpecs(BitChromosomeSpec.of(5))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(SinglePointCrossover.build())
				.mutationPolicies(RandomMutation.of(0.1))
				.fitness(genotype -> 1.0) // Constant fitness
				.termination((eaConfig, generation, population, fitness) -> true) // Immediate termination
				.build();

		final EAExecutionContext<Double> context = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(10)
				.build();

		final EASystem<Double> system = EASystemFactory.from(config, context);

		// Test immediate termination path
		final EvolutionResult<Double> result = system.evolve();

		assertNotNull(result);
		assertEquals(0, result.generation(), "Should terminate at generation 0");
		assertEquals(10,
				result.population()
						.size(),
				"Should have initial population");
	}

	@Test
	@DisplayName("evaluateOnce() method should evaluate genotypes correctly")
	void testEvaluateOnce() {

		// Create simple configuration
		final EAConfiguration<Integer> config = new EAConfiguration.Builder<Integer>()
				.chromosomeSpecs(BitChromosomeSpec.of(4))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(SinglePointCrossover.build())
				.mutationPolicies(RandomMutation.of(0.1))
				.fitness(genotype -> {
					final BitChromosome chromosome = genotype.getChromosome(0, BitChromosome.class);
					return chromosome.getBitSet()
							.cardinality();
				})
				.termination(Terminations.ofMaxGeneration(1))
				.build();

		final EAExecutionContext<Integer> context = EAExecutionContexts.<Integer>forScalarFitness()
				.populationSize(5)
				.build();

		final EASystem<Integer> system = EASystemFactory.from(config, context);

		// Create test genotypes manually
		final int bitSetSize = 4;
		BitSet bitSet1 = new BitSet(bitSetSize);
		bitSet1.set(0, 4); // All bits set: 1111
		BitChromosome chromosome1 = new BitChromosome(bitSetSize, bitSet1);

		BitSet bitSet2 = new BitSet(bitSetSize);
		// No bits set: 0000
		BitChromosome chromosome2 = new BitChromosome(bitSetSize, bitSet2);

		BitSet bitSet3 = new BitSet(bitSetSize);
		bitSet3.set(0);
		bitSet3.set(2);
		BitChromosome chromosome3 = new BitChromosome(bitSetSize, bitSet3);

		final List<Genotype> genotypes = Arrays
				.asList(new Genotype(chromosome1), new Genotype(chromosome2), new Genotype(chromosome3));

		final List<Integer> fitness = system.evaluateOnce(0, genotypes);

		assertNotNull(fitness, "Fitness list should not be null");
		assertEquals(3, fitness.size(), "Should have fitness for each genotype");
		assertEquals(Integer.valueOf(4), fitness.get(0), "All bits set should give fitness 4");
		assertEquals(Integer.valueOf(0), fitness.get(1), "No bits set should give fitness 0");
		assertEquals(Integer.valueOf(2), fitness.get(2), "Two bits set should give fitness 2");
	}

	@Test
	@DisplayName("evaluateOnce() method should work with different generation values")
	void testEvaluateOnceWithDifferentGenerations() {
		EAConfiguration<Integer> config = new EAConfiguration.Builder<Integer>().chromosomeSpecs(BitChromosomeSpec.of(3))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(SinglePointCrossover.build())
				.mutationPolicies(RandomMutation.of(0.1))
				.fitness(genotype -> {
					final BitChromosome chromosome = genotype.getChromosome(0, BitChromosome.class);
					return chromosome.getBitSet()
							.cardinality();
				})
				.termination(Terminations.ofMaxGeneration(1))
				.build();

		EAExecutionContext<Integer> context = EAExecutionContexts.<Integer>forScalarFitness()
				.populationSize(5)
				.build();

		EASystem<Integer> system = EASystemFactory.from(config, context);

		// Create single test genotype
		BitSet bitSet = new BitSet(3);
		bitSet.set(0);
		bitSet.set(1); // 110
		List<Genotype> genotypes = Arrays.asList(new Genotype(new BitChromosome(3, bitSet)));

		// Test with different generation values
		List<Integer> fitness0 = system.evaluateOnce(0, genotypes);
		List<Integer> fitness10 = system.evaluateOnce(10, genotypes);
		List<Integer> fitness100 = system.evaluateOnce(100, genotypes);

		// Should get consistent results
		assertEquals(fitness0, fitness10, "Generation 0 and 10 should give same results");
		assertEquals(fitness0, fitness100, "Generation 0 and 100 should give same results");
		assertEquals(Integer.valueOf(2), fitness0.get(0), "Should count 2 bits correctly");
	}

	@Test
	@DisplayName("getters should return correct values")
	void testGetterMethods() {
		EAConfiguration<Integer> config = new EAConfiguration.Builder<Integer>().chromosomeSpecs(BitChromosomeSpec.of(6))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(SinglePointCrossover.build())
				.mutationPolicies(RandomMutation.of(0.1))
				.fitness(genotype -> 1)
				.termination(Terminations.ofMaxGeneration(1))
				.build();

		EAExecutionContext<Integer> context = EAExecutionContexts.<Integer>forScalarFitness()
				.populationSize(25)
				.build();

		EASystem<Integer> system = EASystemFactory.from(config, context);

		// Test getEAConfiguration() - previously 0% coverage
		assertEquals(config, system.getEAConfiguration(), "getEAConfiguration should return the same configuration");

		// Test getPopulationSize() - previously 0% coverage
		assertEquals(25, system.getPopulationSize(), "getPopulationSize should return the configured population size");
	}

	@Test
	@DisplayName("evolve() should work with IntChromosome")
	void testEvolveWithIntChromosome() {
		// Test with integer chromosome for sum maximization
		EAConfiguration<Double> config = new EAConfiguration.Builder<Double>()
				.chromosomeSpecs(IntChromosomeSpec.of(3, 0, 10))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(SinglePointCrossover.build())
				.mutationPolicies(RandomMutation.of(0.1))
				.fitness(genotype -> {
					final IntChromosome chromosome = genotype.getChromosome(0, IntChromosome.class);
					// Sum all values in the chromosome
					double sum = 0;
					for (int i = 0; i < chromosome.getSize(); i++) {
						sum += chromosome.getAllele(i);
					}
					return sum;
				})
				.termination(Terminations.ofMaxGeneration(2))
				.build();

		EAExecutionContext<Double> context = EAExecutionContexts.<Double>forScalarFitness()
				.populationSize(15)
				.build();

		EASystem<Double> system = EASystemFactory.from(config, context);

		EvolutionResult<Double> result = system.evolve();

		assertNotNull(result);
		assertEquals(2, result.generation(), "Should run for 2 generations");
		assertEquals(15,
				result.population()
						.size(),
				"Population size should be maintained");

		// Fitness should be reasonable (sum of 3 integers from 0-10)
		assertTrue(result.bestFitness() >= 0 && result.bestFitness() <= 30, "Best fitness should be between 0 and 30");
	}

	@Test
	@DisplayName("System construction should work consistently")
	void testSystemConstruction() {
		EAConfiguration<Integer> config = new EAConfiguration.Builder<Integer>().chromosomeSpecs(BitChromosomeSpec.of(4))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(SinglePointCrossover.build())
				.mutationPolicies(RandomMutation.of(0.1))
				.fitness(genotype -> 1)
				.termination(Terminations.ofMaxGeneration(1))
				.build();

		EAExecutionContext<Integer> context = EAExecutionContexts.<Integer>forScalarFitness()
				.populationSize(10)
				.build();

		// Create multiple systems with same configuration
		EASystem<Integer> system1 = EASystemFactory.from(config, context);
		EASystem<Integer> system2 = EASystemFactory.from(config, context);

		// Both should be valid
		assertNotNull(system1, "First system should be created");
		assertNotNull(system2, "Second system should be created");

		assertEquals(system1.getPopulationSize(),
				system2.getPopulationSize(),
				"Systems with same config should have same population size");
		assertEquals(system1.getEAConfiguration(),
				system2.getEAConfiguration(),
				"Systems with same config should reference same configuration");
	}

	@Test
	@DisplayName("evolve() should work with post-evaluation processor")
	void testEvolveWithPostEvaluationProcessor() {
		// Create a post-evaluation processor that doubles fitness values
		Function<Population<Integer>, Population<Integer>> postProcessor = population -> {
			List<Genotype> genotypes = population.getAllGenotypes();
			List<Integer> doubleFitness = population.getAllFitnesses()
					.stream()
					.map(fitness -> fitness * 2)
					.collect(java.util.stream.Collectors.toList());
			return Population.of(genotypes, doubleFitness);
		};

		// Configuration with post-evaluation processor to trigger uncovered lambdas
		EAConfiguration<Integer> config = new EAConfiguration.Builder<Integer>().chromosomeSpecs(BitChromosomeSpec.of(4))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(SinglePointCrossover.build())
				.mutationPolicies(RandomMutation.of(0.1))
				.fitness(genotype -> {
					final BitChromosome chromosome = genotype.getChromosome(0, BitChromosome.class);
					return chromosome.getBitSet()
							.cardinality();
				})
				.postEvaluationProcessor(postProcessor) // This should trigger lambda coverage
				.termination(Terminations.ofMaxGeneration(2))
				.build();

		EAExecutionContext<Integer> context = EAExecutionContexts.<Integer>forScalarFitness()
				.populationSize(10)
				.build();

		EASystem<Integer> system = EASystemFactory.from(config, context);

		// Test evolve() method with post-evaluation processor
		EvolutionResult<Integer> result = system.evolve();

		assertNotNull(result, "Evolution result should not be null");
		assertEquals(2, result.generation(), "Should run for 2 generations");
		assertEquals(10,
				result.population()
						.size(),
				"Population size should be maintained");

		// Since post-processor doubles fitness, values should be even
		// and potentially higher than original bit count
		assertTrue(result.bestFitness() >= 0 && result.bestFitness() <= 8,
				"Best fitness should be in expected range after post-processing");
	}
}