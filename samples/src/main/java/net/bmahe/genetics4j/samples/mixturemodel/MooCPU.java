package net.bmahe.genetics4j.samples.mixturemodel;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.core.Fitness;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.FloatChromosome;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfiguration.Builder;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
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
import net.bmahe.genetics4j.moo.FitnessVector;
import net.bmahe.genetics4j.moo.MOOEAExecutionContexts;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class MooCPU {
	final static public Logger logger = LogManager.getLogger(MooCPU.class);

	private final int distributionNumParameters;
	private final Comparator<Genotype> deduplicator;
	private final String baseDir;
	private final int maxGenerations;

	public MooCPU(final int _distributionNumParameters, final Comparator<Genotype> _deduplicator, final String _baseDir,
			final int _maxGenerations) {

		this.distributionNumParameters = _distributionNumParameters;
		this.deduplicator = _deduplicator;
		this.baseDir = _baseDir;
		this.maxGenerations = _maxGenerations;
	}

	// tag::moo_cpu_fitness[]
	public Fitness<FitnessVector<Float>> fitnessCPU(final int numDistributions, final double[][] samples) {
		return (genotype) -> {
			final var fChromosome = genotype.getChromosome(0, FloatChromosome.class);

			float sumAlpha = 0.0f;
			int k = 0;
			while (k < fChromosome.getSize()) {
				final float alpha = fChromosome.getAllele(k);
				sumAlpha += alpha;

				k += distributionNumParameters;
			}

			float[] likelyhoods = new float[samples.length];
			int i = 0;
			while (i < fChromosome.getSize()) {

				final float alpha = fChromosome.getAllele(i) / sumAlpha;
				if (alpha > 0.0001) {
					final double[] mean = new double[] { fChromosome.getAllele(i + 1), fChromosome.getAllele(i + 2) };
					final double[][] covariances = new double[][] {
							{ fChromosome.getAllele(i + 3) - 15, fChromosome.getAllele(i + 4) - 15 },
							{ fChromosome.getAllele(i + 4) - 15, fChromosome.getAllele(i + 5) - 15 } };

					try {
						final var multivariateNormalDistribution = new MultivariateNormalDistribution(mean, covariances);

						for (int j = 0; j < samples.length; j++) {
							final var density = multivariateNormalDistribution.density(samples[j]);
							likelyhoods[j] += alpha * density;
						}
					} catch (NonPositiveDefiniteMatrixException | MathUnsupportedOperationException
							| SingularMatrixException e) {
					}
				}
				i += distributionNumParameters;
			}

			float sumLogs = 0.0f;
			for (int j = 0; j < samples.length; j++) {
				sumLogs += Math.log(likelyhoods[j]);
			}

			final int[] assigned = ClusteringUtils
					.assignClustersFloatChromosome(distributionNumParameters, samples, genotype);
			final Set<Integer> uniqueAssigned = new HashSet<>();
			uniqueAssigned.addAll(IntStream.of(assigned)
					.boxed()
					.toList());

			final float numUnusedCluster = uniqueAssigned.size() == 0 ? -10f
					: (float) (numDistributions - uniqueAssigned.size());

			return new FitnessVector<>(sumLogs / samples.length, numUnusedCluster);
		};
	}
	// end::moo_cpu_fitness[]

	public EvolutionResult<FitnessVector<Float>> run(final int maxPossibleDistributions, final double[][] samples,
			final float[] x, final float[] y, final String algorithmName, final Collection<Genotype> seedPopulation)
			throws IOException {

		// tag::moo_cpu_config[]
		final var fitnessFunc = fitnessCPU(maxPossibleDistributions, samples);

		final Builder<FitnessVector<Float>> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder
				.chromosomeSpecs(FloatChromosomeSpec.of(distributionNumParameters * maxPossibleDistributions, 0, 30))
				.parentSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 3, deduplicator))
				.replacementStrategy(Elitism.builder()
						.offspringRatio(0.950)
						.offspringSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 3, deduplicator))
						.survivorSelectionPolicy(NSGA2Selection.ofFitnessVector(2, deduplicator))
						.build())
				.combinationPolicy(MultiCombinations.of(SinglePointArithmetic.of(0.9),
						SinglePointCrossover.build(),
						MultiPointCrossover.of(2),
						MultiPointArithmetic.of(2, 0.9)))
				.mutationPolicies(MultiMutations.of(RandomMutation.of(0.40),
						CreepMutation.of(0.40, NormalDistribution.of(0.0, 1)),
						SwapMutation.of(0.40, 5, false)))
				.fitness(fitnessFunc)
				.termination(Terminations.ofMaxGeneration(maxGenerations));

		if (CollectionUtils.isNotEmpty(seedPopulation)) {
			eaConfigurationBuilder.seedPopulation(seedPopulation);
		}

		final var eaConfiguration = eaConfigurationBuilder.build();
		// end::moo_cpu_config[]

		// tag::moo_cpu_eaexeccontext[]
		final var csvEvolutionListener = CSVEvolutionListener.<FitnessVector<Float>, Void>of(
				baseDir + "mixturemodel-moo-cpu.csv",
				List.of(ColumnExtractor.of("generation", e -> e.generation()),
						ColumnExtractor.of("fitness",
								e -> e.fitness()
										.get(0)),
						ColumnExtractor.of("numUnusedCluster",
								e -> e.fitness()
										.get(1))));

		final var eaExecutionContextBuilder = EAExecutionContexts.<FitnessVector<Float>>standard();
		MOOEAExecutionContexts.enrichWithMOO(eaExecutionContextBuilder);
		eaExecutionContextBuilder.populationSize(250)
				.addEvolutionListeners(csvEvolutionListener,
						EvolutionListeners.<FitnessVector<Float>>ofLogTopN(logger,
								5,
								Comparator.<FitnessVector<Float>, Float>comparing(fv -> fv.get(0))))
				.build();
		final var eaExecutionContext = eaExecutionContextBuilder.build();
		// end::moo_cpu_eaexeccontext[]

		final EASystem<FitnessVector<Float>> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<FitnessVector<Float>> evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: {}", evolutionResult.bestGenotype());
		logger.info("  with fitness: {}", evolutionResult.bestFitness());
		logger.info("  at generation: {}", evolutionResult.generation());

		ClusteringUtils.categorizeByNumClusters(distributionNumParameters,
				maxPossibleDistributions,
				x,
				y,
				samples,
				evolutionResult,
				baseDir,
				"moo-cpu");

		return evolutionResult;
	}

}