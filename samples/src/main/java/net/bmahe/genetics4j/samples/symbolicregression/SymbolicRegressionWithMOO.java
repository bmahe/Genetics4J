package net.bmahe.genetics4j.samples.symbolicregression;

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
import net.bmahe.genetics4j.core.evolutionlisteners.EvolutionListeners;
import net.bmahe.genetics4j.core.spec.EAConfiguration;
import net.bmahe.genetics4j.core.spec.EAExecutionContext;
import net.bmahe.genetics4j.core.spec.EvolutionResult;
import net.bmahe.genetics4j.core.spec.Optimization;
import net.bmahe.genetics4j.core.spec.mutation.MultiMutations;
import net.bmahe.genetics4j.core.spec.replacement.Elitism;
import net.bmahe.genetics4j.core.termination.Terminations;
import net.bmahe.genetics4j.extras.evolutionlisteners.CSVEvolutionListener;
import net.bmahe.genetics4j.extras.evolutionlisteners.ColumnExtractor;
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
import net.bmahe.genetics4j.gp.utils.ProgramUtils;
import net.bmahe.genetics4j.gp.utils.TreeNodeUtils;
import net.bmahe.genetics4j.moo.FitnessVector;
import net.bmahe.genetics4j.moo.MOOEAExecutionContexts;
import net.bmahe.genetics4j.moo.ParetoUtils;
import net.bmahe.genetics4j.moo.nsga2.spec.NSGA2Selection;
import net.bmahe.genetics4j.moo.nsga2.spec.TournamentNSGA2Selection;

public class SymbolicRegressionWithMOO {
	final static public Logger logger = LogManager.getLogger(SymbolicRegressionWithMOO.class);

	@SuppressWarnings("unchecked")
	public void run() {
		final Random random = new Random();

		final Program program = SymbolicRegressionUtils.buildProgram(random);

		final Comparator<Genotype> deduplicator = (a, b) -> TreeNodeUtils.compare(a, b, 0);

		final Fitness<FitnessVector<Double>> computeFitness = (genoType) -> {
			final TreeChromosome<Operation<?>> chromosome = (TreeChromosome<Operation<?>>) genoType.getChromosome(0);
			final Double[][] inputs = new Double[100][1];
			for (int i = 0; i < 100; i++) {
				inputs[i][0] = (i - 50) * 1.2;
			}

			double mse = 0;
			for (final Double[] input : inputs) {

				final double x = input[0];
				final double expected = (2.0 * x * x) - x + 8;
				final Object result = ProgramUtils.execute(chromosome, input);

				if (Double.isFinite(expected)) {
					final Double resultDouble = (Double) result;
					if (Double.isFinite(resultDouble)) {
						mse += (expected - resultDouble) * (expected - resultDouble);
					} else {
						mse += 1_000_000_000;
					}
				}
			}

			return Double.isFinite(mse)
					? new FitnessVector<Double>(mse / 100.0, (double) chromosome.getRoot().getSize())
					: new FitnessVector<Double>(Double.MAX_VALUE, Double.MAX_VALUE);
		};

		final var eaConfigurationBuilder = new EAConfiguration.Builder<FitnessVector<Double>>();
		eaConfigurationBuilder.chromosomeSpecs(ProgramTreeChromosomeSpec.of(program))
				.parentSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 3, deduplicator))
				.replacementStrategy(Elitism.builder()
						.offspringRatio(0.995)
						.offspringSelectionPolicy(TournamentNSGA2Selection.ofFitnessVector(2, 3, deduplicator))
						.survivorSelectionPolicy(NSGA2Selection.ofFitnessVector(2, deduplicator))
						.build())
				.combinationPolicy(ProgramRandomCombine.build())
				.mutationPolicies(MultiMutations.of(ProgramRandomMutate.of(0.15 * 3),
						ProgramRandomPrune.of(0.15 * 3),
						NodeReplacement.of(0.15 * 3)), ProgramApplyRules.of(SimplificationRules.SIMPLIFY_RULES))
				.optimization(Optimization.MINIMIZE)
				.termination(Terminations.or(Terminations.<FitnessVector<Double>>ofMaxGeneration(400),
						(generation, population, fitness) -> fitness.stream()
								.anyMatch(fv -> fv.get(0) <= 0.000001 && fv.get(1) <= 10)))
				.fitness(computeFitness);
		final EAConfiguration<FitnessVector<Double>> eaConfiguration = eaConfigurationBuilder.build();

		final var eaExecutionContextBuilder = GPEAExecutionContexts.<FitnessVector<Double>>forGP(random);
		MOOEAExecutionContexts.enrichWithMOO(eaExecutionContextBuilder);
		eaExecutionContextBuilder.populationSize(1500);
		eaExecutionContextBuilder.numberOfPartitions(Math.max(1, Runtime.getRuntime().availableProcessors() - 3));

		eaExecutionContextBuilder.addEvolutionListeners(
				EvolutionListeners.ofLogTopN(logger,
						5,
						Comparator.<FitnessVector<Double>, Double>comparing(fv -> fv.get(0)).reversed(),
						(genotype) -> TreeNodeUtils.toStringTreeNode(genotype, 0)),
				CSVEvolutionListener.<FitnessVector<Double>, List<Set<Integer>>>of("output.csv",
						(generation, population, fitness, isDone) -> ParetoUtils
								.rankedPopulation(Comparator.reverseOrder(), fitness),
						List.of(ColumnExtractor.of("generation", evolutionStep -> evolutionStep.generation()),
								ColumnExtractor.of("score", evolutionStep -> evolutionStep.fitness().get(0)),
								ColumnExtractor.of("complexity", evolutionStep -> evolutionStep.fitness().get(1)),
								ColumnExtractor.of("rank", evolutionStep -> {

									final List<Set<Integer>> rankedPopulation = evolutionStep.context().get();
									Integer rank = null;
									for (int i = 0; i < 5 && i < rankedPopulation.size() && rank == null; i++) {

										if (rankedPopulation.get(i).contains(evolutionStep.individualIndex())) {
											rank = i;
										}
									}

									return rank != null ? rank : -1;
								}),
								ColumnExtractor.of("expression",
										evolutionStep -> TreeNodeUtils.toStringTreeNode(evolutionStep.individual(),
												0))),

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

	public static void main(String[] args) {

		final SymbolicRegressionWithMOO symbolicRegression = new SymbolicRegressionWithMOO();
		symbolicRegression.run();
	}
}