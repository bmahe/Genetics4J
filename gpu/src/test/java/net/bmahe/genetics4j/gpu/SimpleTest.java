package net.bmahe.genetics4j.gpu;

import java.util.Comparator;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.chromosome.FloatChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.selection.RandomSelection;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.gpu.spec.DeviceFilters;
import net.bmahe.genetics4j.gpu.spec.GPUEAConfiguration;
import net.bmahe.genetics4j.gpu.spec.GPUEAExecutionContext;
import net.bmahe.genetics4j.gpu.spec.Program;
import net.bmahe.genetics4j.gpu.spec.fitness.FitnessExtractor;
import net.bmahe.genetics4j.gpu.spec.fitness.SingleKernelFitness;
import net.bmahe.genetics4j.gpu.spec.fitness.SingleKernelFitnessDescriptor;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.DataLoaders;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.ResultAllocators;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.StaticDataLoaders;
import net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext.KernelExecutionContextComputers;

public class SimpleTest {
	public static final Logger logger = LogManager.getLogger(SimpleTest.class);

	public static final String KERNEL_NAME = "sampleKernel";

	@Test
	@Disabled("Need to fix Gitlab runner environment")
	public void simple() {

		final int chromosomeSize = 10;

		final int[] sizes = { chromosomeSize };

		final FitnessExtractor<Float> fitnessExtractor = (openCLExecutionContext, executorService, generation, genotypes,
				resultExtractor) -> {

			final float[] results = resultExtractor.extractFloatArray(openCLExecutionContext, 2);

			return IntStream.range(0, results.length)
					.mapToObj(i -> Float.valueOf(results[i]))
					.toList();
		};

		final var singleKernelFitnessDescriptor = SingleKernelFitnessDescriptor.builder()
				.kernelName(KERNEL_NAME)
				.kernelExecutionContextComputer(KernelExecutionContextComputers.ofGenotypeSize())
				.putStaticDataLoaders(1, StaticDataLoaders.of(sizes))
				.putDataLoaders(0, DataLoaders.ofLinearizeFloatChromosome(0))
				.putResultAllocators(2, ResultAllocators.ofPopulationSizeFloat())
				.build();

		final var eaConfiguration = GPUEAConfiguration.<Float>builder()
				.addChromosomeSpecs(FloatChromosomeSpec.of(chromosomeSize, 0, 100))
				.parentSelectionPolicy(RandomSelection.build())
				.combinationPolicy(SinglePointCrossover.build())
				.mutationPolicies(MultiMutations.of(RandomMutation.of(0.20), CreepMutation.ofNormal(0.20, 0.0, 3)))
				.program(Program.ofResource("/test.cl", KERNEL_NAME))
				.fitness(SingleKernelFitness.of(singleKernelFitnessDescriptor, fitnessExtractor))
				.optimization(Optimization.MINIMIZE)
				.termination(Terminations.<Float>or(Terminations.ofMaxGeneration(1_000), Terminations.ofStableFitness(15)))
				.build();

		final var gpuEaExecutionContext = GPUEAExecutionContext.<Float>builder()
				.deviceFilters(DeviceFilters.ofGPU())
				.populationSize(200)
				.addEvolutionListeners(EvolutionListeners.ofLogTopN(logger, 5, Comparator.<Float>reverseOrder()))
				.build();

		final var gpuEASystem = GPUEASystemFactory.from(eaConfiguration, gpuEaExecutionContext);

		gpuEASystem.evolve();
	}

}