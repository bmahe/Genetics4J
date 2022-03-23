package net.bmahe.genetics4j.samples.symbolicregression;

import java.util.Comparator;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.bmahe.genetics4j.core.EASystem;
import net.bmahe.genetics4j.core.EASystemFactory;
import net.bmahe.genetics4j.core.Fitness;
import net.bmahe.genetics4j.core.Genotype;
import net.bmahe.genetics4j.core.chromosomes.TreeChromosome;
import net.bmahe.genetics4j.core.chromosomes.TreeNode;
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EAExecutionContexts;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.selection.Tournament;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.math.SimplificationRules;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.spec.GPEAExecutionContexts;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.combination.ProgramRandomCombine;
import net.bmahe.genetics4j.gp.spec.mutation.NodeReplacement;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramApplyRules;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomMutate;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;
import net.bmahe.genetics4j.gp.spec.mutation.TrimTree;
import net.bmahe.genetics4j.gp.utils.ProgramUtils;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;

public class SymbolicRegressionWithEnforcedMaxDepth {
	final static public Logger logger = LogManager.getLogger(SymbolicRegressionWithEnforcedMaxDepth.class);

	@SuppressWarnings("unchecked")
	public void run() {
		final Random random = new Random();

		final Program program = SymbolicRegressionUtils.buildProgram(random);

		final Fitness<Double> computeFitness = (genoType) -> {
			final TreeChromosome<Operation<?>> chromosome = (TreeChromosome<Operation<?>>) genoType.getChromosome(0);
			final Double[][] inputs = new Double[100][1];
			for (int i = 0; i < 100; i++) {
				inputs[i][0] = (i - 50) * 1.2;
			}

			double mse = 0;
			for (final Double[] input : inputs) {

				final double x = input[0];
				final double expected = SymbolicRegressionUtils.evaluate(x);
				final Object result = ProgramUtils.execute(chromosome, input);

				if (Double.isFinite(expected)) {
					if (result instanceof Double) {
						final Double resultDouble = (Double) result;
						mse += Double.isFinite(resultDouble) ? (expected - resultDouble) * (expected - resultDouble)
								: 1_000_000_000;
					} else {
						logger.error("NOT A DOUBLE: {}", result);
						mse += 1000;
					}
				}
			}
			return Double.isFinite(mse) ? mse / 100.0 : Double.MAX_VALUE;
		};

		final var eaConfigurationBuilder = new EAConfiguration.Builder<Double>();
		eaConfigurationBuilder.chromosomeSpecs(ProgramTreeChromosomeSpec.of(program))
				.parentSelectionPolicy(Tournament.of(3))
				.combinationPolicy(ProgramRandomCombine.build())
				.mutationPolicies(ProgramRandomMutate.of(0.10),
						ProgramRandomPrune.of(0.12),
						NodeReplacement.of(0.05),
						TrimTree.build(),
						ProgramApplyRules.of(SimplificationRules.SIMPLIFY_RULES))
				.optimization(Optimization.MINIMIZE)
				.termination(Terminations.or(Terminations.ofMaxGeneration(100), Terminations.ofFitnessAtMost(0.0001d)))
				.fitness(computeFitness);
		final EAConfiguration<Double> eaConfiguration = eaConfigurationBuilder.build();

		final var eaExecutionContextBuilder = GPEAExecutionContexts.<Double>forGP(random);
		EAExecutionContexts.enrichForScalarFitness(eaExecutionContextBuilder);

		eaExecutionContextBuilder.populationSize(1500);
		eaExecutionContextBuilder.numberOfPartitions(Math.max(1,
				Runtime.getRuntime()
						.availableProcessors() - 1));

		eaExecutionContextBuilder.addEvolutionListeners(
				EvolutionListeners.ofLogTopN(logger, 5, Comparator.<Double>reverseOrder(), (genotype) -> {
					final TreeChromosome<Operation<?>> chromosome = (TreeChromosome<Operation<?>>) genotype.getChromosome(0);
					final TreeNode<Operation<?>> root = chromosome.getRoot();

					return TreeNodeUtils.toStringTreeNode(root);
				}),
				SymbolicRegressionUtils.csvLoggerDouble("symbolicregression-output-enforced-max-depth.csv",
						evolutionStep -> evolutionStep.fitness(),
						evolutionStep -> (double) evolutionStep.individual()
								.getChromosome(0, TreeChromosome.class)
								.getSize()));

		final EAExecutionContext<Double> eaExecutionContext = eaExecutionContextBuilder.build();
		final EASystem<Double> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<Double> evolutionResult = eaSystem.evolve();
		final Genotype bestGenotype = evolutionResult.bestGenotype();
		final TreeChromosome<Operation<?>> bestChromosome = (TreeChromosome<Operation<?>>) bestGenotype.getChromosome(0);
		logger.info("Best genotype: {}", bestChromosome.getRoot());
		logger.info("Best genotype - pretty print: {}", TreeNodeUtils.toStringTreeNode(bestChromosome.getRoot()));
	}

	public static int main(String[] args) {

		final var symbolicRegression = new SymbolicRegressionWithEnforcedMaxDepth();
		symbolicRegression.run();

		return 0;
	}
}