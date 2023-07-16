package net.bmahe.genetics4j.samples.mixturemodel;

import java.io.IOException;
import java.util.Collection;
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
import net.bmahe.genetics4j.core.chromosomes.DoubleChromosome;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAConfiguration.Builder;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.chromosome.DoubleChromosomeSpec;
import net.bmahe.genetics4j.core.spec.combination.MultiCombinations;
import net.bmahe.genetics4j.core.spec.combination.MultiPointArithmetic;
import net.bmahe.genetics4j.core.spec.combination.MultiPointCrossover;
import net.bmahe.genetics4j.core.spec.combination.SinglePointArithmetic;
import net.bmahe.genetics4j.core.spec.combination.SinglePointCrossover;
import net.bmahe.genetics4j.core.spec.mutation.CreepMutation;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.mutation.RandomMutation;
import net.bmahe.genetics4j.core.spec.mutation.SwapMutation;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.spec.statistics.distributions.NormalDistribution;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;

public class SingleObjectiveMethod {
	final static public Logger logger = LogManager.getLogger(SingleObjectiveMethod.class);

	private final int distributionNumParameters;
	private final String baseDir;
	private final int maxGenerations;

	public SingleObjectiveMethod(final int _distributionNumParameters, final String _baseDir,
			final int _maxGenerations) {

		this.distributionNumParameters = _distributionNumParameters;
		this.baseDir = _baseDir;
		this.maxGenerations = _maxGenerations;
	}

	// tag::som_fitness[]
	public Fitness<Double> fitnessCPU(final int numDistributions, final double[][] samples) {
		return (genotype) -> {
			final var fChromosome = genotype.getChromosome(0, DoubleChromosome.class);

			/**
			 * Normalize alpha
			 */
			double sumAlpha = 0.0f;
			int k = 0;
			while (k < fChromosome.getSize()) {
				sumAlpha += fChromosome.getAllele(k);
				k += distributionNumParameters;
			}

			double[] likelyhoods = new double[samples.length];
			int i = 0;
			while (i < fChromosome.getSize()) {

				final double alpha = fChromosome.getAllele(i) / sumAlpha;
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
						// Ignore invalid mixtures
					}
				}
				i += distributionNumParameters;
			}

			double sumLogs = 0.0f;
			for (int j = 0; j < samples.length; j++) {
				sumLogs += Math.log(likelyhoods[j]);
			}

			return sumLogs / samples.length;
		};
	}
	// end::som_fitness[]

	public EvolutionResult<Double> run(final int maxPossibleDistributions, final double[][] samples, final float[] x,
			final float[] y, final String algorithmName, final Collection<Genotype> seedPopulation) throws IOException {

		// tag::som_config[]
		final Builder<Double> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder
				.chromosomeSpecs(DoubleChromosomeSpec.of(distributionNumParameters * maxPossibleDistributions, 0, 30))
				.parentSelectionPolicy(Tournament.of(2))
				.combinationPolicy(MultiCombinations.of(SinglePointArithmetic.of(0.9),
						SinglePointCrossover.build(),
						MultiPointCrossover.of(2),
						MultiPointArithmetic.of(2, 0.9)))
				.mutationPolicies(MultiMutations.of(RandomMutation.of(0.40),
						CreepMutation.of(0.40, NormalDistribution.of(0.0, 2)),
						SwapMutation.of(0.30, 5, false)))
				.fitness(fitnessCPU(maxPossibleDistributions, samples))
				.termination(Terminations.ofMaxGeneration(maxGenerations));

		if (CollectionUtils.isNotEmpty(seedPopulation)) {
			eaConfigurationBuilder.seedPopulation(seedPopulation);
		}

		final var eaConfiguration = eaConfigurationBuilder.build();
		// end::som_config[]

		// tag::som_eaexeccontext[]
		final var csvEvolutionListener = CSVEvolutionListener.<Double, Void>of(baseDir + "mixturemodel-so-cpu.csv",
				List.of(ColumnExtractor.of("generation", e -> e.generation()),
						ColumnExtractor.of("fitness", e -> e.fitness())));

		final var eaExecutionContextBuilder = EAExecutionContexts.<Double>standard();
		eaExecutionContextBuilder.populationSize(250)
				.addEvolutionListeners(csvEvolutionListener, EvolutionListeners.ofLogTopN(logger, 5))
				.build();
		final var eaExecutionContext = eaExecutionContextBuilder.build();
		// end::som_eaexeccontext[]

		final EASystem<Double> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<Double> evolutionResult = eaSystem.evolve();
		logger.info("Best genotype: {}", evolutionResult.bestGenotype());
		logger.info("  with fitness: {}", evolutionResult.bestFitness());
		logger.info("  at generation: {}", evolutionResult.generation());

		final int[] assignedClusters = ClusteringUtils
				.assignClustersDoubleChromosome(distributionNumParameters, samples, evolutionResult.bestGenotype());
		final Set<Integer> uniqueAssigned = new HashSet<>();
		uniqueAssigned.addAll(IntStream.of(assignedClusters)
				.boxed()
				.toList());

		ClusteringUtils
				.persistClusters(x, y, assignedClusters, baseDir + "assigned-so-" + uniqueAssigned.size() + ".csv");

		return evolutionResult;
	}
}