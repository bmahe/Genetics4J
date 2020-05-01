package net.bmahe.genetics4j.samples.symbolicregression;

import java.util.Arrays;
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
import net.bmahe.genetics4j.core.spec.selection.TournamentSelection;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.SimplificationRules;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.program.ImmutableProgram;
import net.bmahe.genetics4j.gp.program.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.program.ProgramGenerator;
import net.bmahe.genetics4j.gp.program.ProgramHelper;
import net.bmahe.genetics4j.gp.program.RampedHalfAndHalfProgramGenerator;
import net.bmahe.genetics4j.gp.spec.GPEAExecutionContexts;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.combination.ProgramRandomCombine;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramApplyRules;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomMutate;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;
import net.bmahe.genetics4j.gp.utils.ProgramUtils;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;

public class SymbolicRegressionWithConstantParsimonyPressure {
	final static public Logger logger = LogManager.getLogger(SymbolicRegressionWithConstantParsimonyPressure.class);

	@SuppressWarnings("unchecked")
	public void run() {
		final Random random = new Random();
		final ProgramHelper programHelper = new ProgramHelper(random);
		final ProgramGenerator programGenerator = new RampedHalfAndHalfProgramGenerator(random, programHelper);

		final Builder programBuilder = ImmutableProgram.builder();
		programBuilder.addFunctions(Functions.ADD,
				Functions.MUL,
				Functions.DIV,
				Functions.SUB,
				Functions.COS,
				Functions.SIN,
				Functions.EXP);
		programBuilder.addTerminal(Terminals.InputDouble(random),
				Terminals.PI,
				Terminals.E,
				Terminals.Coefficient(random, -50, 100),
				Terminals.CoefficientRounded(random, -25, 25));

		programBuilder.inputSpec(ImmutableInputSpec.of(Arrays.asList(Double.class, String.class)));
		programBuilder.maxDepth(4);
		final Program program = programBuilder.build();

		final Fitness<Double> computeFitness = (genoType) -> {
			final TreeChromosome<Operation<?>> chromosome = (TreeChromosome<Operation<?>>) genoType.getChromosome(0);
			final Double[][] inputs = new Double[100][1];
			for (int i = 0; i < 100; i++) {
				inputs[i][0] = (i - 50) * 1.2;
			}

			double mse = 0;
			for (final Double[] input : inputs) {

				final double x = input[0];
				final double expected = (6.0 * x * x) - x;
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
			return Double.isFinite(mse) ? Math.sqrt(mse) + 1.5 * chromosome.getSize() : Double.MAX_VALUE;
		};

		net.bmahe.genetics4j.core.spec.EAConfiguration.Builder<Double> eaConfigurationBuilder = new EAConfiguration.Builder<>();
		eaConfigurationBuilder.chromosomeSpecs(ProgramTreeChromosomeSpec.of(program))
				.parentSelectionPolicy(TournamentSelection.build(3))
				.survivorSelectionPolicy(TournamentSelection.build(3))
				.offspringRatio(0.90d)
				.combinationPolicy(ProgramRandomCombine.build())
				.mutationPolicies(ProgramRandomMutate.of(0.10),
						ProgramRandomPrune.of(0.12),
						ProgramApplyRules.of(SimplificationRules.SIMPLIFY_RULES))
				.optimization(Optimization.MINIMIZE)
				.termination(Terminations.ofMaxGeneration(100))
				.fitness(computeFitness);
		final EAConfiguration<Double> eaConfiguration = eaConfigurationBuilder.build();

		final net.bmahe.genetics4j.core.spec.ImmutableEAExecutionContext.Builder<Double> eaExecutionContextBuilder = GPEAExecutionContexts
				.<Double>forGP(random, programHelper, programGenerator);
		EAExecutionContexts.enrichForScalarFitness(eaExecutionContextBuilder);

		eaExecutionContextBuilder.populationSize(5000);
		eaExecutionContextBuilder.numberOfPartitions(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));

		eaExecutionContextBuilder.addEvolutionListeners(EvolutionListeners.ofLogTopN(logger, 5, (genotype) -> {
			final TreeChromosome<Operation<?>> chromosome = (TreeChromosome<Operation<?>>) genotype.getChromosome(0);
			final TreeNode<Operation<?>> root = chromosome.getRoot();

			return TreeNodeUtils.toStringTreeNode(root);
		}));

		final EAExecutionContext<Double> eaExecutionContext = eaExecutionContextBuilder.build();
		final EASystem<Double> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<Double> evolutionResult = eaSystem.evolve();
		final Genotype bestGenotype = evolutionResult.bestGenotype();
		final TreeChromosome<Operation<?>> bestChromosome = (TreeChromosome<Operation<?>>) bestGenotype
				.getChromosome(0);
		logger.info("Best genotype: {}", bestChromosome.getRoot());
		logger.info("Best genotype - pretty print: {}", TreeNodeUtils.toStringTreeNode(bestChromosome.getRoot()));
	}

	public static int main(String[] args) {

		final SymbolicRegressionWithConstantParsimonyPressure symbolicRegression = new SymbolicRegressionWithConstantParsimonyPressure();
		symbolicRegression.run();

		return 0;
	}
}