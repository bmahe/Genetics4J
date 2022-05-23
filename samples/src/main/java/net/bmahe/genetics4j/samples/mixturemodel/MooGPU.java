package net.bmahe.genetics4j.samples.mixturemodel;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.chromosome.FloatChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiCombinations;
import net.bmahe.genetics4j.core.spec.combination.MultiPointArithmetic;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.core.spec.combination.SinglePointArithmetic;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.core.spec.replacement.Elitism;
import net.bmahe.genetics4j.core.spec.statistics.distributions.NormalDistribution;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;
import net.bmahe.genetics4j.gpu.GPUEASystemFactory;
import net.bmahe.genetics4j.gpu.spec.DeviceFilters;
import net.bmahe.genetics4j.gpu.spec.GPUEAConfiguration;
import net.bmahe.genetics4j.gpu.spec.GPUEAExecutionContext;
import net.bmahe.genetics4j.gpu.spec.Program;
import net.bmahe.genetics4j.gpu.spec.fitness.FitnessExtractor;
import net.bmahe.genetics4j.gpu.spec.fitness.SingleKernelFitness;
import net.bmahe.genetics4j.gpu.spec.fitness.SingleKernelFitnessDescriptor;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.DataLoaders;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.DataSupplier;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.ResultAllocators;
import net.bmahe.genetics4j.gpu.spec.fitness.cldata.StaticDataLoaders;
import net.bmahe.genetics4j.gpu.spec.fitness.kernelcontext.KernelExecutionContextComputers;
import net.bmahe.genetics4j.moo.FitnessVector;
import net.bmahe.genetics4j.moo.nsga2.impl.NSGA2SelectionPolicyHandler;
import net.bmahe.genetics4j.moo.nsga2.impl.TournamentNSGA2SelectionPolicyHandler;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;
import net.bmahe.genetics4j.moo.spea2.replacement.SPEA2ReplacementStrategyHandler;

public class MooGPU {
	final static public Logger logger = LogManager.getLogger(MooGPU.class);

	private final int distributionNumParameters;
	private final Comparator<Genotype> deduplicator;
	private final String baseDir;
	private final int maxGenerations;

	public MooGPU(final int _distributionNumParameters, final Comparator<Genotype> _deduplicator, final String _baseDir,
			final int _maxGenerations) {

		this.distributionNumParameters = _distributionNumParameters;
		this.deduplicator = _deduplicator;
		this.baseDir = _baseDir;
		this.maxGenerations = _maxGenerations;
	}

	// tag::moo_gpu_fitness_extractor[]
	public final FitnessExtractor<FitnessVector<Float>> fitnessExtractor(final int maxPossibleDistributions,
			final double[][] samples) {
		return (openCLExecutionContext, kernelExecutionContext, executorService, generation, genotypes,
				resultExtractor) -> {

			final float[] results = resultExtractor.extractFloatArray(openCLExecutionContext, 5);

			try {
				final var extractionTask = executorService.submit(() -> {
					return IntStream.range(0, genotypes.size())
							.boxed()
							.parallel()
							.map(genotypeIndex -> {
								float tally = 0;

								final int startIndex = genotypeIndex * samples.length;
								final int endIndex = startIndex + samples.length;
								int notFiniteCount = 0;
								for (int i = startIndex; i < endIndex; i++) {
									if (Float.isFinite(results[i])) {
										tally += results[i];
									} else {
										notFiniteCount++;
										tally -= 10_000f;
									}
								}
								final int[] assignedClusters = ClusteringUtils.assignClustersFloatChromosome(
										distributionNumParameters,
										samples,
										genotypes.get(genotypeIndex));
								final Set<Integer> uniqueAssigned = new HashSet<>();
								uniqueAssigned.addAll(IntStream.of(assignedClusters)
										.boxed()
										.toList());

								if (notFiniteCount > 0 || notFiniteCount == samples.length || uniqueAssigned.size() == 0) {
									return new FitnessVector<>(-100_000_000f, -100f);
								}

								final float numUnusedCluster = (float) (maxPossibleDistributions - uniqueAssigned.size());
								return new FitnessVector<>(tally / samples.length, numUnusedCluster);
							})
							.toList();
				});

				return extractionTask.get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error("Could not extract results", e);
				throw new RuntimeException(e);
			}
		};
	}
	// end::moo_gpu_fitness_extractor[]

	public DataSupplier<float[]> computeSideData(final int maxPossibleDistributions) {
		return (openCLExecutionContext, generation, genotypes) -> {
			/**
			 * We will provide: <br>
			 * - Whether the values are valid<br>
			 * - Value of the determinant<br>
			 * - A linearized 2x2 matrix for the inverse covariance
			 */
			final float[] densityDataHelper = new float[genotypes.size() * maxPossibleDistributions * (2 + 4)];
			int genotypeIndex = 0;
			for (final var genotype : genotypes) {
				final var fChromosome = genotype.getChromosome(0, FloatChromosome.class);

				for (int distributionIndex = 0; distributionIndex < maxPossibleDistributions; distributionIndex++) {
					final int baseDistributionIndex = distributionIndex * distributionNumParameters;

					final double[] mean = new double[] { fChromosome.getAllele(baseDistributionIndex + 1),
							fChromosome.getAllele(baseDistributionIndex + 2) };
					final double[][] covariances = new double[2][2];
					covariances[0][0] = fChromosome.getAllele(baseDistributionIndex + 3) - 15;
					covariances[0][1] = fChromosome.getAllele(baseDistributionIndex + 4) - 15;
					covariances[1][0] = fChromosome.getAllele(baseDistributionIndex + 4) - 15;
					covariances[1][1] = fChromosome.getAllele(baseDistributionIndex + 5) - 15;

					try {
						/**
						 * Will throw exception is the covariances aren't valid
						 */
						final var multivariateNormalDistribution = new MultivariateNormalDistribution(mean, covariances);

						final var covarianceMatrix = new Array2DRowRealMatrix(covariances);
						final var eigenDecomposition = new EigenDecomposition(covarianceMatrix);
						final var eigenDecompositionSolver = eigenDecomposition.getSolver();
						final var covarianceInverse = eigenDecompositionSolver.getInverse();
						final var covarianceDeterminant = eigenDecomposition.getDeterminant();

						final int baseDensityDataHelperIndex = genotypeIndex * maxPossibleDistributions * 6
								+ distributionIndex * 6;
						densityDataHelper[baseDensityDataHelperIndex + 1] = (float) covarianceDeterminant;
						densityDataHelper[baseDensityDataHelperIndex + 2] = (float) covarianceInverse.getEntry(0, 0);
						densityDataHelper[baseDensityDataHelperIndex + 3] = (float) covarianceInverse.getEntry(0, 1);
						densityDataHelper[baseDensityDataHelperIndex + 4] = (float) covarianceInverse.getEntry(1, 0);
						densityDataHelper[baseDensityDataHelperIndex + 5] = (float) covarianceInverse.getEntry(1, 1);

						densityDataHelper[baseDensityDataHelperIndex + 0] = 10;

					} catch (NonPositiveDefiniteMatrixException | MathUnsupportedOperationException
							| SingularMatrixException e) {

					}
				}

				genotypeIndex++;
			}
			return densityDataHelper;
		};
	}

	public void run(final int maxPossibleDistributions, final int numDistributions, final double[][] samplesDouble,
			final float[][] samples, final float[] x, final float[] y, final String algorithmName,
			final Collection<Genotype> seedPopulation, final EvolutionResult<FitnessVector<Float>> bestCPUResult)
			throws IOException {

		final var kernelName = "mixtureModelKernel";
		final int chromosomeSize = distributionNumParameters * maxPossibleDistributions;

		// tag::moo_gpu_skfd[]
		final var singleKernelFitnessDescriptorBuilder = SingleKernelFitnessDescriptor.builder();
		singleKernelFitnessDescriptorBuilder.kernelName(kernelName)
				.kernelExecutionContextComputer(KernelExecutionContextComputers.ofGlobalWorkSize1D(samples.length))
				.putStaticDataLoaders(0, StaticDataLoaders.ofLinearize(samples))
				.putStaticDataLoaders(1,
						StaticDataLoaders
								.of(distributionNumParameters, maxPossibleDistributions, chromosomeSize, samples.length))
				.putDataLoaders(2, DataLoaders.ofGenerationAndPopulationSize())
				.putDataLoaders(3, DataLoaders.ofLinearizeFloatChromosome(0))
				.putDataLoaders(4, DataLoaders.ofFloatSupplier(computeSideData(maxPossibleDistributions)))
				.putResultAllocators(5, ResultAllocators.ofMultiplePopulationSizeFloat(samples.length));
		final var singleKernelFitnessDescriptor = singleKernelFitnessDescriptorBuilder.build();
		// end::moo_gpu_skfd[]

		// tag::moo_gpu_config[]
		final var singleKernelFitness = SingleKernelFitness.of(singleKernelFitnessDescriptor,
				fitnessExtractor(maxPossibleDistributions, samplesDouble));

		final var eaConfigurationBuilder = GPUEAConfiguration.<FitnessVector<Float>>builder()
				.chromosomeSpecs(FloatChromosomeSpec.of(chromosomeSize, 0, 30))
				.parentSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 5, deduplicator))
				.replacementStrategy(Elitism.builder()
						.offspringRatio(0.950)
						.offspringSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 5, deduplicator))
						.survivorSelectionPolicy(NSGA2Selection.ofFitnessVector(2, deduplicator))
						.build())
				.combinationPolicy(MultiCombinations.of(SinglePointArithmetic.of(0.9),
						SinglePointCrossover.build(),
						MultiPointCrossover.of(3),
						MultiPointArithmetic.of(3, 0.9)))
				.mutationPolicies(MultiMutations.of(RandomMutation.of(0.40),
						CreepMutation.of(0.40, NormalDistribution.of(0.0, 1)),
						SwapMutation.of(0.40, 5, false)))
				.program(Program.ofResource("/opencl/mixturemodel/main.cl", kernelName, ""))
				.fitness(singleKernelFitness)
				.termination(Terminations.ofMaxGeneration(maxGenerations));

		if (CollectionUtils.isNotEmpty(seedPopulation)) {
			eaConfigurationBuilder.seedPopulation(seedPopulation);
		}

		final var eaConfiguration = eaConfigurationBuilder.build();
		// end::moo_gpu_config[]

		// tag::moo_gpu_eaexeccontext[]
		final var csvEvolutionListener = CSVEvolutionListener.<FitnessVector<Float>, Void>of(
				baseDir + algorithmName + ".csv",
				List.of(ColumnExtractor.of("generation", e -> e.generation()),
						ColumnExtractor.of("fitness",
								e -> e.fitness()
										.get(0)),
						ColumnExtractor.of("numUnusedCluster",
								e -> e.fitness()
										.get(1)),
						ColumnExtractor.of("numCluster",
								e -> maxPossibleDistributions - e.fitness()
										.get(1))));

		final var topNloggerEvolutionListener = EvolutionListeners.<FitnessVector<Float>>ofLogTopN(logger,
				5,
				Comparator.<FitnessVector<Float>, Float>comparing(fv -> fv.get(0)));

		final var eaExecutionContextBuilder = GPUEAExecutionContext.<FitnessVector<Float>>builder()
				.deviceFilters(DeviceFilters.ofGPU())
				.populationSize(250)
				.addEvolutionListeners(csvEvolutionListener, topNloggerEvolutionListener);

		/**
		 * TODO should be in the library
		 */
		eaExecutionContextBuilder.addSelectionPolicyHandlerFactories((gsd) -> new NSGA2SelectionPolicyHandler<>(),
				gsd -> new TournamentNSGA2SelectionPolicyHandler<>(gsd.randomGenerator()));

		eaExecutionContextBuilder
				.addReplacementStrategyHandlerFactories((gsd) -> new SPEA2ReplacementStrategyHandler<>());

		final var eaExecutionContext = eaExecutionContextBuilder.build();
		// end::moo_gpu_eaexeccontext[]

		final var executorService = Executors.newWorkStealingPool();
		final var eaSystem = GPUEASystemFactory.from(eaConfiguration, eaExecutionContext, executorService);

		final EvolutionResult<FitnessVector<Float>> evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: {}", evolutionResult.bestGenotype());
		logger.info("  with fitness: {}", evolutionResult.bestFitness());
		logger.info("  at generation: {}", evolutionResult.generation());

		ClusteringUtils.categorizeByNumClusters(distributionNumParameters,
				maxPossibleDistributions,
				x,
				y,
				samplesDouble,
				evolutionResult,
				baseDir,
				algorithmName);
	}
}