package net.bmahe.genetics4j.samples.symbolicregression;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

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
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.replacement.Elitism;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;
import net.bmahe.genetics4j.gp.ImmutableInputSpec;
import net.bmahe.genetics4j.gp.Operation;
import net.bmahe.genetics4j.gp.math.Functions;
import net.bmahe.genetics4j.gp.math.SimplificationRules;
import net.bmahe.genetics4j.gp.math.Terminals;
import net.bmahe.genetics4j.gp.program.ImmutableProgram;
import net.bmahe.genetics4j.gp.program.ImmutableProgram.Builder;
import net.bmahe.genetics4j.gp.program.Program;
import net.bmahe.genetics4j.gp.spec.GPEAExecutionContexts;
import net.bmahe.genetics4j.gp.spec.chromosome.ProgramTreeChromosomeSpec;
import net.bmahe.genetics4j.gp.spec.combination.ProgramRandomCombine;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramApplyRules;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomMutate;
import net.bmahe.genetics4j.gp.spec.mutation.ProgramRandomPrune;
import net.bmahe.genetics4j.gp.utils.ProgramUtils;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;
import net.bmahe.genetics4j.moo.FitnessVector;
import net.bmahe.genetics4j.moo.MOOEAExecutionContexts;
import net.bmahe.genetics4j.moo.nsga2.impl.NSGA2Utils;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class SymbolicRegressionWithMOO {
	final static public Logger logger = LogManager.getLogger(SymbolicRegressionWithMOO.class);

	@SuppressWarnings("unchecked")
	public void run() {
		final Random random = new Random();

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

		final Fitness<FitnessVector<Double>> computeFitness = (genoType) -> {
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

			return Double.isFinite(mse)
					? new FitnessVector<Double>(Math.sqrt(mse), (double) chromosome.getRoot().getSize())
					: new FitnessVector<Double>(Double.MAX_VALUE, Double.MAX_VALUE);
		};

		final var eaConfigurationBuilder = new EAConfiguration.Builder<FitnessVector<Double>>();
		eaConfigurationBuilder.chromosomeSpecs(ProgramTreeChromosomeSpec.of(program))
				.parentSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 3))
				.replacementStrategy(Elitism.builder()
						.offspringRatio(0.95)
						.offspringSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 3))
						.survivorSelectionPolicy(NSGA2Selection.ofFitnessVector(2))
						.build())
				.combinationPolicy(ProgramRandomCombine.build())
				.mutationPolicies(ProgramRandomMutate.of(0.10),
						ProgramRandomPrune.of(0.05),
						ProgramApplyRules.of(SimplificationRules.SIMPLIFY_RULES))
				.optimization(Optimization.MINIMIZE)
				.termination(Terminations.or(Terminations.<FitnessVector<Double>>ofMaxGeneration(40),
						(generation, population, fitness) -> fitness.stream()
								.anyMatch(fv -> fv.get(0) <= Double.MIN_VALUE && fv.get(1) < 10)))
				.fitness(computeFitness);
		final EAConfiguration<FitnessVector<Double>> eaConfiguration = eaConfigurationBuilder.build();

		final var eaExecutionContextBuilder = GPEAExecutionContexts.<FitnessVector<Double>>forGP(random);
		MOOEAExecutionContexts.enrichWithMOO(eaExecutionContextBuilder);
		eaExecutionContextBuilder.populationSize(5000);
		eaExecutionContextBuilder.numberOfPartitions(Math.max(1, Runtime.getRuntime().availableProcessors() - 3));

		eaExecutionContextBuilder.addEvolutionListeners(EvolutionListeners.ofLogTopN(logger,
				5,
				Comparator.<FitnessVector<Double>, Double>comparing(fv -> fv.get(0)).reversed(),
				(genotype) -> {
					final TreeChromosome<Operation<?>> chromosome = (TreeChromosome<Operation<?>>) genotype
							.getChromosome(0);
					final TreeNode<Operation<?>> root = chromosome.getRoot();

					return TreeNodeUtils.toStringTreeNode(root);
				}),
				CSVEvolutionListener.<FitnessVector<Double>, List<Set<Integer>>>of("output.csv",
						(generation, population, fitness, isDone) -> NSGA2Utils
								.rankedPopulation(Comparator.reverseOrder(), fitness),
						List.of(ColumnExtractor.of("generation", evolutionStep -> evolutionStep.generation()),
								ColumnExtractor.of("score", evolutionStep -> evolutionStep.fitness().get(0)),
								ColumnExtractor.of("complexity", evolutionStep -> evolutionStep.fitness().get(1)),
								ColumnExtractor.of("rank", evolutionStep -> {

									final List<Set<Integer>> rankedPopulation = evolutionStep.context();
									Integer rank = null;
									for (int i = 0; i < 5 && i < rankedPopulation.size() && rank == null; i++) {

										if (rankedPopulation.get(i).contains(evolutionStep.individualIndex())) {
											rank = i;
										}
									}

									return rank != null ? rank : -1;
								})),
						5));

		final EAExecutionContext<FitnessVector<Double>> eaExecutionContext = eaExecutionContextBuilder.build();
		final EASystem<FitnessVector<Double>> eaSystem = EASystemFactory.from(eaConfiguration, eaExecutionContext);

		final EvolutionResult<FitnessVector<Double>> evolutionResult = eaSystem.evolve();
		final Genotype bestGenotype = evolutionResult.bestGenotype();
		final TreeChromosome<Operation<?>> bestChromosome = (TreeChromosome<Operation<?>>) bestGenotype
				.getChromosome(0);
		logger.info("Best genotype: {}", bestChromosome.getRoot());
		logger.info("Best genotype - pretty print: {}", TreeNodeUtils.toStringTreeNode(bestChromosome.getRoot()));

		final int depthIdx = 1;
		for (int i = 0; i < 15; i++) {
			final int depth = i;
			final Optional<Integer> optIdx = IntStream.range(0, evolutionResult.fitness().size())
					.boxed()
					.filter((idx) -> evolutionResult.fitness().get(idx).get(depthIdx) == depth)
					.sorted((a, b) -> Double.compare(evolutionResult.fitness().get(a).get(0),
							evolutionResult.fitness().get(b).get(0)))
					.findFirst();

			optIdx.stream().forEach((idx) -> {
				final TreeChromosome<Operation<?>> treeChromosome = (TreeChromosome<Operation<?>>) evolutionResult
						.population()
						.get(idx)
						.getChromosome(0);

				logger.info("Best genotype for depth {} - score {} -> {}",
						depth,
						evolutionResult.fitness().get(idx).get(0),
						TreeNodeUtils.toStringTreeNode(treeChromosome.getRoot()));
			});
		}
	}

	public static int main(String[] args) {

		final SymbolicRegressionWithMOO symbolicRegression = new SymbolicRegressionWithMOO();
		symbolicRegression.run();

		System.exit(0);
		return 0;
	}
}